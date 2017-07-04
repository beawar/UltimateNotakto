package com.example.misterweeman.ultimatenotakto;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConnectionHandler implements RoomUpdateListener,
        RealTimeMessageReceivedListener, RoomStatusUpdateListener,
        OnInvitationReceivedListener, GoogleApiHelper.ConnectionListener {
    private static final String TAG = "ConnectionHandler";

    private final static int RC_SELECT_PLAYERS = 10000;
    private static final int RC_WAITING_ROOM = 10002;
    private static final int RC_INVITATION_INBOX = 10001;

    private GoogleApiHelper mGoogleApiHelper;
    // room id of the current game
    private String mRoomId = null;
    // partecipants of the current game
    private ArrayList<Participant> mPartecipants = null;
    // my partecipant ID in the current game
    private String mMyId = null;
    // id of the game invitation received
    private String mIncomingInvitationId = null;

    // message buffer for the sending message
    // format of a message: [Y = lost, N = not lost][X coordinate][Y coordinate][turn id]
    private byte[] mMsgBuffer = new byte[4];

    private int mCurScreen;
    private int mFromScreen;
    private boolean mResolvingConnectionFailure = false;

//    private List<String> mActivePartecipant = new ArrayList<>();
    private Set<String> mFinishedPartecipants = new HashSet<>();
    private int mCurrentTurn = 0;
//    private int mSecondsLeft;

    private Activity mParentActivity = null;
    private int mMyTurn;


    ConnectionHandler(Activity activity, int layoutId) {
        mParentActivity = activity;
        mFromScreen = layoutId;
        mGoogleApiHelper = App.getGoogleApiHelper();
    }

    // Sets the flag to keep this screen on. It's recommended to do that during
    // the handshake when setting up a game, because if the screen turns off, the
    // game will be cancelled.
    private void keepScreenOn() {
        mParentActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Clears the flag that keeps the screen on.
    private void stopKeepingScreenOn() {
        mParentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void handleSelectPlayersResult(int resultCode, Intent data) {
        Log.d(TAG, "handleSelectPlayersResult: ");
        if (resultCode != Activity.RESULT_OK) {
            Log.w(TAG, "*** select players UI cancelled, resultCode: " + resultCode);
            switchToMainScreen();
        } else {
            Log.d(TAG, "Select players UI succeded");

            // get the invitee list
            final ArrayList<String> invitess = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
            Log.d(TAG, "Invitee count:" + invitess.size());

            // get auto-match criteria
            Bundle autoMatchCriteria;
            int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

            if (minAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoMatchPlayers, maxAutoMatchPlayers, 0);
                Log.d(TAG, "Automatch criteria: " + autoMatchCriteria);
            } else {
                Log.d(TAG, "minAutoMatchPlayers = " + minAutoMatchPlayers);
                autoMatchCriteria = null;
            }

            // create the room and specify a variant if appropriate
            RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
            roomConfigBuilder.addPlayersToInvite(invitess);
            if (autoMatchCriteria != null) {
                roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
            }
            switchToScreen(R.layout.screen_wait);
            keepScreenOn();
            Games.RealTimeMultiplayer.create(App.getGoogleApiHelper().getGoogleApiClient(), roomConfigBuilder.build());
            Log.d(TAG, "Room created, waiting for it to be ready");
        }
    }

    private void acceptInviteToRoom(String invitationId) {
        Log.d(TAG, "Accepting invitation " + invitationId);
        RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
        roomConfigBuilder.setInvitationIdToAccept(invitationId);

        Games.RealTimeMultiplayer.join(mGoogleApiHelper.getGoogleApiClient(), roomConfigBuilder.build());
        mIncomingInvitationId = null;
        keepScreenOn();

        startGame();
    }

    @NonNull
    private RoomConfig.Builder makeBasicRoomConfigBuilder() {
        return RoomConfig.builder(this)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
    }

//    public void selectPlayer(int minOpponents, int maxOpponents) {
//        Intent intent = Games.RealTimeMultiplayer
//                .getSelectOpponentsIntent(mGoogleApiHelper.getGoogleApiClient(), minOpponents, maxOpponents);
//        switchToScreen(R.layout.screen_wait);
//        mParentActivity.startActivityForResult(intent, RC_SELECT_PLAYERS);
//    }

    void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");
        switch (requestCode){
            case RC_SELECT_PLAYERS:
                // we got the result from the "select players" UI -> ready to create the room
                handleSelectPlayersResult(resultCode, data);
                break;
            case RC_WAITING_ROOM:
                // we got the result from the "waiting room" UI
                if (resultCode == Activity.RESULT_OK) {
                    //ready to start playing
                    Log.d(TAG, "Starting game (waiting room returned OK)");
                    startGame();
                } else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                    // player indicated that he want to leave the room
                    leaveRoom();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // Dialog was canceled (user pressed back key)
                    // TODO: choose between leaving room or inimize waiting room
                    leaveRoom();
                }
                break;
        }
    }

    private void startGame() {
        Log.d(TAG, "startGame: ");
//        mParentActivity.startActivity(new Intent(mParentActivity, GameActivity.class));
        switchToScreen(R.layout.fragment_game);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected() called");

        // register the listener so we are notified if we receive an invitation to play
        Games.Invitations.registerInvitationListener(App.getGoogleApiHelper().getGoogleApiClient(), this);
        if (connectionHint != null) {
            Log.d(TAG, "onConnected: connection hint provided. Checking for invite");
            Invitation invitation = connectionHint.getParcelable(Multiplayer.EXTRA_INVITATION);
            if (invitation != null && invitation.getInvitationId() != null) {
                // retrieve and cache the invitation ID
                Log.d(TAG, "onConnected: connectionHint has a room invite");
                acceptInviteToRoom(invitation.getInvitationId());
                // go to game screen
            }
        } else {
            switchToMainScreen();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed() called, result: " + connectionResult);
        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed() already resolving");
        } else {
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(mParentActivity, mGoogleApiHelper.getGoogleApiClient(),
                    connectionResult, GoogleApiHelper.RC_SIGN_IN, R.string.sign_in_other_error);
            switchToScreen(R.layout.fragment_signin);
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: called");

    }

    private void leaveRoom() {
        Log.d(TAG, "Leaving room");
//        mSecondsLeft = 0;
        stopKeepingScreenOn();
        if (mRoomId != null) {
            Games.RealTimeMultiplayer.leave(mGoogleApiHelper.getGoogleApiClient(), this, mRoomId);
            mPartecipants.remove(mMyTurn);
            mRoomId = null;
            switchToScreen(R.layout.screen_wait);
        } else {
            switchToMainScreen();
        }
    }

    private void showWaitingRoom(Room room) {
        Log.d(TAG, "showWaitingRoom: ");
        final int MIN_PLAYERS = Integer.MAX_VALUE;
        Intent intent = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiHelper.getGoogleApiClient(), room, MIN_PLAYERS);

        // show waiting room UI
        mParentActivity.startActivityForResult(intent, RC_WAITING_ROOM);
    }

    private void showGameError(){
        Log.d(TAG, "showGameError: ");
        BaseGameUtils.makeSimpleDialog(mParentActivity, mParentActivity.getString(R.string.game_problem));
        switchToMainScreen();
    }

    private void updateRoom(Room room) {
        Log.d(TAG, "updateRoom: ");
        if (room != null) {
            mPartecipants = room.getParticipants();
            for (int i=0; i<mPartecipants.size(); ++i){
                if (mPartecipants.get(i).getParticipantId().equals(mMyId)) {
                    mMyTurn = i;
                }
            }
            Log.d(TAG, "updateRoom: room " + room.getRoomId());
        }
    }

    @Override
    public void onInvitationReceived(Invitation invitation) {
        Log.d(TAG, "onInvitationReceived: ");
        // store invitation for use when player accepts this invitation
        mIncomingInvitationId = invitation.getInvitationId();

        mParentActivity.findViewById(R.id.invitation_popup).setVisibility(View.VISIBLE);

        // show in-game popup to let user know of pending invitation
        ((TextView) mParentActivity.findViewById(R.id.incoming_invitation_text)).setText(
                invitation.getInviter().getDisplayName() + " " +
                        mParentActivity.getString(R.string.is_inviting_you));
        // show the invitation popup
        switchToScreen(mCurScreen);
    }

    @Override
    public void onInvitationRemoved(String s) {
        Log.d(TAG, "onInvitationRemoved: ");
        if (mIncomingInvitationId != null && mIncomingInvitationId.equals(s)) {
            mIncomingInvitationId = null;
            // hide the invitation popup
            switchToScreen(mCurScreen);
        }
    }

    public void broadcastTurn(boolean hasLost, int x, int y) {
        Log.d(TAG, "broadcastTurn: ");
        // has the player lost with his move?
        mMsgBuffer[0] = (byte) (hasLost ? 'Y' : 'N');
        // coordinates of the cell clicked
        mMsgBuffer[1] = (byte) x;
        mMsgBuffer[2] = (byte) y;
        // turnId indicates whos turn has take place
        mMsgBuffer[3] = (byte) mCurrentTurn;


        // send to every other partecipant
        // Reliable messages are used because receiving this information is essential for the game
        for (Participant p : mPartecipants) {
            if (!p.getParticipantId().equals(mMyId) && p.getStatus() != Participant.STATUS_JOINED) {
                Games.RealTimeMultiplayer.sendReliableMessage(
                        mGoogleApiHelper.getGoogleApiClient(), null, mMsgBuffer, mRoomId, p.getParticipantId());
            }
        }
    }

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        Log.d(TAG, "onRealTimeMessageReceived: ");
        byte[] buf = realTimeMessage.getMessageData();
        String sender = realTimeMessage.getSenderParticipantId();
        boolean hasLost = (char) buf[0] == 'Y';
        int x = (int) buf[1];
        int y = (int) buf[2];
        int turn = (int) buf[3];

        Log.d(TAG, "onRealTimeMessageReceived: hasLost=" + hasLost + "/("
                + x + ", " + y +") - Turn: " + turn);
        // if it's a final score, mark this participant as having finished
        // the game
        if (!mFinishedPartecipants.contains(sender)) {
            if (hasLost) {
                mFinishedPartecipants.add(sender);
            }
        }
        mCurrentTurn = ((int) buf[3] + 1) % mPartecipants.size();

    }

    @Override
    public void onRoomConnecting(Room room) {
        Log.d(TAG, "onRoomConnecting: ");
        updateRoom(room);
    }


    @Override
    public void onRoomAutoMatching(Room room) {
        Log.d(TAG, "onRoomAutoMatching: ");
        updateRoom(room);
    }


    @Override
    public void onPeerInvitedToRoom(Room room, List<String> list) {
        Log.d(TAG, "onPeerInvitedToRoom: ");
        updateRoom(room);
    }


    @Override
    public void onPeerDeclined(Room room, List<String> list) {
        Log.d(TAG, "onPeerDeclined: ");
        updateRoom(room);
    }


    @Override
    public void onPeerJoined(Room room, List<String> list) {
        Log.d(TAG, "onPeerJoined: ");
        updateRoom(room);
    }


    @Override
    public void onPeerLeft(Room room, List<String> list) {
        Log.d(TAG, "onPeerLeft: ");
        updateRoom(room);
    }


    @Override
    public void onConnectedToRoom(Room room) {
        Log.d(TAG, "onConnectedToRoom() called");

        // get partecipants and my id
        mPartecipants = room.getParticipants();
        mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiHelper.getGoogleApiClient()));

        // save room id if its not initialized in onRoomCreated()
        if (mRoomId == null) {
            mRoomId = room.getRoomId();
        }

        // print the list of partecipants
        Log.d(TAG, "Room ID: " +  mRoomId);
        Log.d(TAG, "My ID: " + mMyId);
        Log.d(TAG, "<< CONNECTED TO ROOM >>");
    }

    @Override
    public void onDisconnectedFromRoom(Room room) {
        Log.d(TAG, "onDisconnectedFromRoom: ");
        mRoomId = null;
        showGameError();
    }

    @Override
    public void onPeersConnected(Room room, List<String> list) {
        Log.d(TAG, "onPeersConnected: ");
        updateRoom(room);
    }


    @Override
    public void onPeersDisconnected(Room room, List<String> list) {
        Log.d(TAG, "onPeersDisconnected: ");
        updateRoom(room);
    }


    @Override
    public void onP2PConnected(String partecipant) {
        Log.d(TAG, "onP2PConnected: partecipant " + partecipant);
    }

    @Override
    public void onP2PDisconnected(String partecipant) {
        Log.d(TAG, "onP2PDisconnected: partecipant " + partecipant);
    }

    @Override
    public void onRoomCreated(int statusCode, Room room) {
        Log.d(TAG, "onRoomCreated(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
            showGameError();
        } else {
            // save room id so we can leave cleanly before the game starts
            mRoomId = room.getRoomId();
            // show the waiting room UI
            showWaitingRoom(room);
        }
    }

    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        Log.d(TAG, "onJoinedRoom(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onJoinedRoom, status " + statusCode);
            showGameError();
        } else {
            showWaitingRoom(room);
        }
    }

    @Override
    public void onLeftRoom(int statusCode, String roomId) {
        // we have left the room, return to main screen
        Log.d(TAG, "onLeftRoom, code: " + statusCode);
        switchToMainScreen();
    }

    @Override
    public void onRoomConnected(int statusCode, Room room) {
        Log.d(TAG, "onRoomConnected(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            showGameError();
        } else {
            updateRoom(room);
        }
    }

    void startQuickGame(int opponents) {
        Log.d(TAG, "startQuickGame()");

        // auto-match criteria to invite the number of opponents.
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(opponents, opponents, 0);

        // build the room config
        RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
        roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);

        switchToScreen(R.layout.screen_wait);
        keepScreenOn();

        if (mGoogleApiHelper.getGoogleApiClient() != null && mGoogleApiHelper.getGoogleApiClient().isConnected()) {
            // create room
            Games.RealTimeMultiplayer.create(mGoogleApiHelper.getGoogleApiClient(), roomConfigBuilder.build());
        } else {
            BaseGameUtils.makeSimpleDialog(mParentActivity, mParentActivity.getString(R.string.game_problem));
           switchToScreen(R.layout.fragment_signin);
        }
    }

    void createGame(int opponents){
        Log.d(TAG, "createGame()");
        Intent intent = Games.RealTimeMultiplayer.
                getSelectOpponentsIntent(App.getGoogleApiHelper().getGoogleApiClient(), opponents, opponents);
        mParentActivity.startActivityForResult(intent, RC_SELECT_PLAYERS);

    }

    void onStop() {
        Log.d(TAG, "*** got onStop");
        // if we are in a room, leave it
        leaveRoom();

        // stop trying to keep screen on
        stopKeepingScreenOn();
    }

    private void switchToScreen(int layout) {
        Log.d(TAG, "switchToScreen: ");
        if (mCurScreen != layout) {
            if (layout == R.layout.fragment_game || layout == R.layout.fragment_game_option) {
                mCurScreen = R.layout.activity_game;
                switchToGameScreen(layout);
            } else {
                mCurScreen = layout;
                App.setLayout(mParentActivity, layout);
            }

            // do not show invitation while in an game
            boolean showInvPopup = mIncomingInvitationId != null && mCurScreen != R.layout.activity_game;
            if (mParentActivity.findViewById(R.id.invitation_popup) != null) {
                mParentActivity.findViewById(R.id.invitation_popup).setVisibility(showInvPopup ? View.VISIBLE : View.GONE);
            }
        }
    }

    private void switchToMainScreen() {
        Log.d(TAG, "switchToMainScreen: ");
        if (mGoogleApiHelper.getGoogleApiClient() != null && mGoogleApiHelper.isConnected()) {
            switchToScreen(mFromScreen);
        }
        else {
            mParentActivity.startActivity(new Intent(Intent.ACTION_MAIN));
        }
    }

    private void switchToGameScreen(int layout) {
        Log.d(TAG, "switchToGameScreen: ");
        if (mParentActivity instanceof GameActivity) {
            GameActivity gameActivity = (GameActivity) mParentActivity;
            if (layout == R.layout.fragment_game) {
                gameActivity.replaceFragment(gameActivity.getGameFragment());
            } else if (layout == R.layout.fragment_game_option) {
                gameActivity.replaceFragment(gameActivity.getGameOptionFragment());
            }
        }
    }

//    public String getIncomingInvitationId() {
//        return mIncomingInvitationId;
//    }

//    public void checkInvites()
//    {
//        if(mGoogleApiHelper.isConnected()){
//            PendingResult<Invitations.LoadInvitationsResult> invs =
//                    Games.Invitations.loadInvitations(mGoogleApiHelper.getGoogleApiClient());
//            invs.setResultCallback(new ResultCallback<Invitations.LoadInvitationsResult>(){
//                @Override
//                public void onResult(@NonNull Invitations.LoadInvitationsResult list) {
//                    if(list.getInvitations().getCount() > 0) {
//                        invitationInbox();
//                    }
//                }});
//        }
//    }

//    private void invitationInbox() {
//        Intent intent = Games.Invitations.getInvitationInboxIntent(mGoogleApiHelper.getGoogleApiClient());
//        mParentActivity.startActivityForResult(intent, RC_INVITATION_INBOX);
//    }

    public boolean isMyTurn() {
        Log.d(TAG, "isMyTurn: " + (mMyTurn == mCurrentTurn));
        return mMyTurn == mCurrentTurn;
    }

    public boolean hasLost() {
        Log.d(TAG, "hasLost: " + mFinishedPartecipants.contains(mMyId));
        return mFinishedPartecipants.contains(mMyId);
    }

    public boolean isConnectedToRoom() {
        Log.d(TAG, "isConnectedToRoom: " + (mRoomId != null));
        return mRoomId != null;
    }

}
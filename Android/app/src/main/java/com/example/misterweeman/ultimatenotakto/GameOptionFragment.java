package com.example.misterweeman.ultimatenotakto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class GameOptionFragment extends Fragment{

    private static final String TAG = "GameOptionFragment";

    private ConnectionHandler mConnectionHandler;
    private Activity mParentActivity;

    public GameOptionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GameFragment.
     */
    public static GameOptionFragment newInstance() {
        return new GameOptionFragment();
    }


    private int getNumberOfPlayers() {
        // number of player is stored on the checked radio button
        RadioButton checkedRadioPlayers = (RadioButton) mParentActivity.findViewById(
                ((RadioGroup) mParentActivity.findViewById(R.id.button_player_layout)).getCheckedRadioButtonId());
        return Integer.parseInt(checkedRadioPlayers.getText().toString());
    }

    private int getBoardSize() {
        // size of board is stored on the checked radio button
        RadioButton checkedRadioSize = (RadioButton) mParentActivity.findViewById(
                ((RadioGroup) mParentActivity.findViewById(R.id.button_size_layout)).getCheckedRadioButtonId());
        return Integer.parseInt(checkedRadioSize.getText().toString().substring(0, 1));
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mParentActivity = getActivity();
        if (mParentActivity instanceof GameActivity) {
            mConnectionHandler = ((GameActivity) mParentActivity).getConnectionHandler();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_game_option, container);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void startQuickGame(View view) {
        if (mConnectionHandler != null) {
            mConnectionHandler.startQuickGame(getNumberOfPlayers()-1);
        }
    }

    public void createGame(View view) {
        if (mConnectionHandler != null) {
            mConnectionHandler.createGame(getNumberOfPlayers()-1);
        }
    }

    @Override
    public void onStart() {
        if (App.getGoogleApiHelper().getGoogleApiClient() == null) {
            // TODO: 04/06/2017 Mostrare fragment per sign-in o gioco offline
        } else if (!App.getGoogleApiHelper().isConnected()) {
            Log.d(TAG, "Connecting client");
            App.getGoogleApiHelper().connect();
        } else {
            Log.w(TAG, "GameHelper: client was already connected on onStart()");
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mConnectionHandler.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mConnectionHandler.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* GAME LOGIC*/
//    private int mSecondsLeft = -1;
//    private static final int GAME_DURATION = 20;
//    int mScore = 0;
//
//    // reset the game variables in preparation for a new game
//    private void resetGameVars() {
//        mSecondsLeft = 20;
//        mScore = 0;
//        mPartecipantScore.clear();
//        mFinishedPartecipants.clear();
//    }
//
//    // Start the gameplay phase of the game.
//    void startGame(boolean multiplayer) {
//        mMultiplayer = multiplayer;
//        updateScoreDisplay();
//        broadcastScore(false);
//        switchToScreen(R.id.screen_game);
//
//        findViewById(R.id.button_click_me).setVisibility(View.VISIBLE);
//
//        // run the gameTick() method every second to update the game.
//        final Handler h = new Handler();
//        h.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (mSecondsLeft <= 0)
//                    return;
//                gameTick();
//                h.postDelayed(this, 1000);
//            }
//        }, 1000);
//    }
//
//    // Game tick -- update countdown, check if game ended.
//    void gameTick() {
//        if (mSecondsLeft > 0)
//            --mSecondsLeft;
//
//        // update countdown
//        ((TextView) findViewById(R.id.countdown)).setText("0:" +
//                (mSecondsLeft < 10 ? "0" : "") + String.valueOf(mSecondsLeft));
//
//        if (mSecondsLeft <= 0) {
//            // finish game
//            findViewById(R.id.button_click_me).setVisibility(View.GONE);
//            broadcastScore(true);
//        }
//    }
//
//    // indicates the player scored one point
//    void scoreOnePoint() {
//        if (mSecondsLeft <= 0)
//            return; // too late!
//        ++mScore;
//        updateScoreDisplay();
//        updatePeerScoresDisplay();
//
//        // broadcast our new score to our peers
//        broadcastScore(false);
//    }

    /*
     * UI SECTION. Methods that implement the game's UI.
     */

    // This array lists everything that's clickable, so we can install click
    // event handlers.
//    final static int[] CLICKABLES = {
//            R.id.button_accept_popup_invitation, R.id.button_invite_players,
//            R.id.button_quick_game, R.id.button_see_invitations, R.id.button_sign_in,
//            R.id.button_sign_out, R.id.button_click_me, R.id.button_single_player,
//            R.id.button_single_player_2
//    };
//
//    // This array lists all the individual screens our game has.
//    final static int[] SCREENS = {
//            R.id.screen_game, R.id.screen_main, R.id.screen_sign_in,
//            R.id.screen_wait
//    };
//    int mCurScreen = -1;
//
//
//
//    // updates the label that shows my score
//    void updateScoreDisplay() {
//        ((TextView) findViewById(R.id.my_score)).setText(formatScore(mScore));
//    }
//
//    // formats a score as a three-digit number
//    String formatScore(int i) {
//        if (i < 0)
//            i = 0;
//        String s = String.valueOf(i);
//        return s.length() == 1 ? "00" + s : s.length() == 2 ? "0" + s : s;
//    }
//
//    // updates the screen with the scores from our peers
//    void updatePeerScoresDisplay() {
//        ((TextView) findViewById(R.id.score0)).setText(formatScore(mScore) + " - Me");
//        int[] arr = {
//                R.id.score1, R.id.score2, R.id.score3
//        };
//        int i = 0;
//
//        if (mRoomId != null) {
//            for (Participant p : mParticipants) {
//                String pid = p.getParticipantId();
//                if (pid.equals(mMyId))
//                    continue;
//                if (p.getStatus() != Participant.STATUS_JOINED)
//                    continue;
//                int score = mParticipantScore.containsKey(pid) ? mParticipantScore.get(pid) : 0;
//                ((TextView) findViewById(arr[i])).setText(formatScore(score) + " - " +
//                        p.getDisplayName());
//                ++i;
//            }
//        }
//
//        for (; i < arr.length; ++i) {
//            ((TextView) findViewById(arr[i])).setText("");
//        }
//    }

}

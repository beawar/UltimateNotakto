package com.example.misterweeman.ultimatenotakto;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class GameOptionActivity extends AppCompatActivity{

    private static final String TAG = "GameOptionActivity";

    private ConnectionHandler mConnectionHandler;

    private int getNumberOfPlayers() {
        // number of player is stored on the checked radio button
        RadioButton checkedRadioPlayers = (RadioButton) findViewById(
                ((RadioGroup) findViewById(R.id.button_player_layout)).getCheckedRadioButtonId());
        return Integer.parseInt(checkedRadioPlayers.getText().toString());
    }

    private int getBoardSize() {
        // size of board is stored on the checked radio button
        RadioButton checkedRadioSize = (RadioButton) findViewById(
                ((RadioGroup) findViewById(R.id.button_size_layout)).getCheckedRadioButtonId());
        return Integer.parseInt(checkedRadioSize.getText().toString().substring(0, 1));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mConnectionHandler = new ConnectionHandler(this, R.layout.game_option_activity);
        setContentView(R.layout.base_layout);
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_container);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout.addView(layoutInflater.inflate(R.layout.game_option_activity, layout, false));
    }

    public void startQuickGame(View view) {
        if (mConnectionHandler != null) {
            mConnectionHandler.startQuickGame(view, getNumberOfPlayers()-1);
        }
    }

    public void createGame(View view) {
        if (mConnectionHandler != null) {
            mConnectionHandler.createGame(view, getNumberOfPlayers()-1);
        }
    }

    @Override
    protected void onStart() {
        if (App.getGoogleApiHelper().getGoogleApiClient() == null) {
            // TODO: 04/06/2017 Mostrare fragment per sign-in
        } else if (!App.getGoogleApiHelper().isConnected()) {
            Log.d(TAG, "Connecting client");
            App.getGoogleApiHelper().connect();
        } else {
            Log.w(TAG, "GameHelper: client was already connected on onStart()");
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mConnectionHandler.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

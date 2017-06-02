package com.example.misterweeman.ultimatenotakto;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;


public class GameOptionActivity extends AppCompatActivity {

    private static final String TAG = "GameOptionActivity";
    // request code for the "select players" UI
    // can be any number as long as it's unique
    private final static int RC_SELECT_PLAYERS = 10000;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_option_activity);

    }

    public void createGame(View view){

        Log.d(TAG, "createGame()");

        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);

    }

    public void startQuickGame(View view) {
        Log.d(TAG, "startQuickGame()");

        // number of player is stored on the checked radio button
        RadioButton checkedRadioPlayers = (RadioButton) findViewById(
                ((RadioGroup) findViewById(R.id.button_player_layout)).getCheckedRadioButtonId());
        int playersNum = Integer.parseInt(checkedRadioPlayers.getText().toString());

        // size of board is stored on the checked radio button
        RadioButton checkedRadioSize = (RadioButton) findViewById(
                ((RadioGroup) findViewById(R.id.button_size_layout)).getCheckedRadioButtonId());
        int boardSize = Integer.parseInt(checkedRadioSize.getText().toString().substring(0, 1));

        // auto-match criteria to invite the number of player - 1 (the user himself) opponent.
        Bundle am = RoomConfig.createAutoMatchCriteria(playersNum - 1, playersNum - 1, 0);

        // build the room config
        RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
        roomConfigBuilder.setAutoMatchCriteria(am);
        RoomConfig roomConfig = roomConfigBuilder.build();

        // create room
        Games.RealTimeMultiplayer.create(App.getGoogleApiHelper().getGoogleApiClient(), roomConfig);

        // prevent the screen from sleeping during handshake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // go to game screen
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(getString(R.string.ARG_PLAYERS_NUM), playersNum);
        intent.putExtra(getString(R.string.ARG_GRID_SIZE), boardSize);
        startActivity(intent);
    }

    private RoomConfig.Builder makeBasicRoomConfigBuilder() {
        return null;
    }
}

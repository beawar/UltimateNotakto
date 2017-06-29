package com.example.misterweeman.ultimatenotakto;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.misterweeman.ultimatenotakto.view.GameFragment;

import static android.R.attr.fragment;

public class GameActivity extends AppCompatActivity implements GameFragment.GameLostListener {

    private static final String TAG = "Notakto Board";
    private CountDownTimer timer;
    private AlertDialog alertDialog;
    private static final String ARG_GAMELOST = "gameLost";
    private boolean gameLost = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_game);

        /*
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {
            // However, if we're being restored from a previous state,
            // then we don't need to do anything or else
            // we could end up with overlapping fragments.
            if (savedInstanceState == null) {
                // Create a new Fragment to be placed in the activity layout
                GameFragment gameFragment = new GameFragment();

                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                gameFragment.setArguments(getIntent().getExtras());

                // Add the fragment to the 'fragment_container' Layout
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, gameFragment).commit();
            } else if (savedInstanceState.getBoolean(ARG_GAMELOST, true)){
                onGameLost();
            }
        }
        */

        Intent intent = getIntent();

        int boardSize = intent.getIntExtra("BoardSizeChecked", R.id.button_3x3);
        int playerSize = intent.getIntExtra("PlayerNumberChecked", R.id.button_2players);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        GameFragment gameFragment = GameFragment.newInstance(getBoardSize(boardSize));

        fragmentTransaction.add(R.id.game_board, gameFragment);
        fragmentTransaction.commit();

        setContentView(R.layout.base_activity);

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_container);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout.addView(layoutInflater.inflate(R.layout.game_activity, layout, false));

        addPlayers(playerSize);

        createTimer();
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (alertDialog != null && alertDialog.isShowing()) {
            // close dialog to prevent leaked window
            alertDialog.dismiss();
        }
        outState.putBoolean(ARG_GAMELOST, gameLost);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            gameLost = savedInstanceState.getBoolean(ARG_GAMELOST, false);
        }
    }

    @Override
    public void onGameLost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.lost_dialog_message)
                .setTitle(R.string.list_dialog_title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

        /*
        gameLost = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.lost_dialog_message)
                .setTitle(R.string.list_dialog_title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: do something else
                        finish();
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                            finish();
                        }
                        return false;
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
        */
    }

    private int getBoardSize(int id){

        int bSize = 3;

        if(id == R.id.button_3x3){
            bSize = 3;
        }
        else if(id == R.id.button_4x4){
            bSize = 4;
        }
        else if(id == R.id.button_5x5){
            bSize = 5;
        }
        else if(id == R.id.button_6x6) {
            bSize = 6;
        }

        return bSize;

    }

    private void createTimer(){

        final TextView textTimer = (TextView) findViewById(R.id.game_timer);
        final Toast toast = Toast.makeText(this, "Turno finito", Toast.LENGTH_LONG);

        timer = new CountDownTimer(40000, 1000) {

            public void onTick(long millisUntilFinished) {
                textTimer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish(){
                toast.show();
                this.start();
            }

        }.start();

    }

    private void addPlayers(int playerSize){

        TextView player1 = (TextView) findViewById(R.id.player_1);
        player1.setBackgroundResource(R.color.green);

        TextView player2 = (TextView) findViewById(R.id.player_2);
        player2.setBackgroundResource(R.color.green);

        if(playerSize == R.id.button_3players){

            TextView player3 = (TextView) findViewById(R.id.player_3);
            player3.setBackgroundResource(R.color.green);

        }
        else if(playerSize == R.id.button_4players){

            TextView player3 = (TextView) findViewById(R.id.player_3);
            player3.setBackgroundResource(R.color.green);

            TextView player4 = (TextView) findViewById(R.id.player_4);
            player4.setBackgroundResource(R.color.green);
        }
    }

    @Override
    public void onBackPressed(){
        timer.cancel();
    }

}

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.misterweeman.ultimatenotakto.view.GameFragment;

import static android.R.attr.fragment;


public class GameActivity extends AppCompatActivity implements GameFragment.GameLostListener{

    private static final String TAG = "Notakto Board";

    @Override
    protected void onCreate(Bundle saveInstanceBundle){
        super.onCreate(saveInstanceBundle);

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

        createTimer();

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

        new CountDownTimer(40000, 1000) {

            public void onTick(long millisUntilFinished) {
                textTimer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish(){
                toast.show();
            }

        }.start();

    }

}

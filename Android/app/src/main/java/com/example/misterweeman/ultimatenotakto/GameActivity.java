package com.example.misterweeman.ultimatenotakto;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import com.example.misterweeman.ultimatenotakto.view.GameFragment;

import static android.R.attr.fragment;


public class GameActivity extends AppCompatActivity implements GameFragment.GameLostListener{

    private static final String TAG = "Notakto Board";

    @Override
    protected void onCreate(Bundle saveInstanceBundle){
        super.onCreate(saveInstanceBundle);

        Intent intent = getIntent();

        int boardSize = intent.getIntExtra("BoardSizeChecked", R.id.button_3x3);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        GameFragment gameFragment = GameFragment.newInstance(getBoardSize(boardSize));

        fragmentTransaction.add(R.id.game_layout, gameFragment);
        fragmentTransaction.commit();

        setContentView(R.layout.game_activity);

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

}

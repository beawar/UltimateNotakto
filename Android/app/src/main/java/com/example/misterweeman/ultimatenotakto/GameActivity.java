package com.example.misterweeman.ultimatenotakto;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.example.misterweeman.ultimatenotakto.view.GameFragment;


public class GameActivity extends AppCompatActivity implements GameFragment.GameLostListener{

    private static final String TAG = "Notakto Board";

    @Override
    protected void onCreate(Bundle saveInstanceBundle){
        super.onCreate(saveInstanceBundle);

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
                        
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}

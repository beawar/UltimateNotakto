package com.example.misterweeman.ultimatenotakto;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class GameOptionActivity extends AppCompatActivity {

    private static final String TAG = "GameOptionActivity";

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
}

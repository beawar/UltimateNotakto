package com.example.misterweeman.ultimatenotakto;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


public class MainActivity extends AppCompatActivity{

    private static final String TAG = "UltimateNotakto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    // called when the user click "Crea Partita"
    public void createGame(View view){

        Log.d(TAG, "createGame()");

        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy(){

        Log.d(TAG, "destroy");

        super.onDestroy();
    }



}

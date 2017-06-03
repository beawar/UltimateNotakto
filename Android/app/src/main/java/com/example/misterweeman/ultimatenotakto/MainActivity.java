package com.example.misterweeman.ultimatenotakto;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import static com.example.misterweeman.ultimatenotakto.Utility.loadLocale;


public class MainActivity extends AppCompatActivity{

    private static final String TAG = "UltimateNotakto";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        loadLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onRestart() {
        loadLocale(this);
        super.onRestart();
        setContentView(R.layout.activity_main);
    }
    // called when the user click "Crea Partita"
    public void goToOption(View view){

        Log.d(TAG, "goToOption()");

        Intent intent = new Intent(this, GameOptionActivity.class);
        startActivity(intent);
    }

    // called when the user click "Statistiche"
    public void goToStats(View view){

        Log.d(TAG, "goToStats()");

        Intent intent = new Intent(this, GameStats.class);
        startActivity(intent);
    }

    // called when the user click "Opzioni"
    public void goToOptions(View view){

        Log.d(TAG, "goToOptions()");

        Intent intent = new Intent(this, Options.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy(){

        Log.d(TAG, "destroy");

        super.onDestroy();
    }

}

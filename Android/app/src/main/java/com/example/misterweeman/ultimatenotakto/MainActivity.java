package com.example.misterweeman.ultimatenotakto;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "UltimateNotakto";
    private SignInFragment signInFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_container);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout.addView(layoutInflater.inflate(R.layout.activity_main, layout, false));

        if (findViewById(R.id.signin_fragment) != null) {
            if (savedInstanceState == null) {
                signInFragment = new SignInFragment();
                signInFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.signin_fragment, signInFragment).commit();
            }
        }
    }

    // called when the user click "Crea Partita"
    public void goToNewGame(View view){

        Log.d(TAG, "goToNewGame()");

        Intent intent = new Intent(this, GameActivity.class);
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

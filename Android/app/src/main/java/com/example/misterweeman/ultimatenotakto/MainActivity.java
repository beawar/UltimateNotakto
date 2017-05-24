package com.example.misterweeman.ultimatenotakto;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.misterweeman.ultimatenotakto.view.GameFragment;

public class MainActivity extends AppCompatActivity
        implements GameFragment.OnGameFragmentInteractionListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onDestroy(){
        System.out.print("Distrutto");
        super.onDestroy();
    }

    @Override
    public boolean onCellTouched(float x, float y) {
        GameFragment gameFragment = (GameFragment) getSupportFragmentManager().findFragmentById(R.id.game_fragment);
        if (gameFragment != null) { // If the game fragment is available
            // TODO: comunicazione tra fragment e activity per generare nuova schermata "Hai perso"/"Hai vinto"
            System.out.println("MainActivity.onCellTouched.if");
            return true;
        } else {
            // Otherwise, we are in an other layout
            System.out.println("MainActivity.onCellTouched.else");
            return false;
        }
    }
}

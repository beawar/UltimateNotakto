package com.example.misterweeman.ultimatenotakto.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.misterweeman.ultimatenotakto.App;
import com.example.misterweeman.ultimatenotakto.R;

public class GameStatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);
        App.setLayout(this, R.layout.activity_game_stats);
    }
}

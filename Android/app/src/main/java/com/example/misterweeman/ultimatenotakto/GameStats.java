package com.example.misterweeman.ultimatenotakto;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class GameStats extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_container);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout.addView(layoutInflater.inflate(R.layout.activity_game_stats, layout, false));
    }
}

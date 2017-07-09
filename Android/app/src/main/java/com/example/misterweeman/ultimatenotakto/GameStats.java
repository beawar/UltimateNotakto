package com.example.misterweeman.ultimatenotakto;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public class GameStats extends AppCompatActivity {


    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_container);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout.addView(layoutInflater.inflate(R.layout.activity_game_stats, layout, false));
    }

    public void showAchievements(View v){
        mGoogleApiClient = App.getGoogleApiHelper().getGoogleApiClient();
        if(mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient),
                    1);
        }else{
            Toast.makeText(this,R.string.notConnected,Toast.LENGTH_SHORT).show();
        }

    }

    public void showLeaderBoard(View v){
        mGoogleApiClient = App.getGoogleApiHelper().getGoogleApiClient();
        if(mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                    String.valueOf(R.string.leaderboard_victories)), 2);
        }else{
            Toast.makeText(this,R.string.notConnected,Toast.LENGTH_SHORT).show();
        }
    }
}

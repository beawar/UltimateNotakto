package com.example.misterweeman.ultimatenotakto.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.misterweeman.ultimatenotakto.App;
import com.example.misterweeman.ultimatenotakto.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public class GameStatsActivity extends AppCompatActivity {
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = App.getGoogleApiHelper().getGoogleApiClient();
        setContentView(R.layout.base_layout);
        App.setLayout(this, R.layout.activity_game_stats);
    }

    public void showAchievements(View v){
//        mGoogleApiClient = App.getGoogleApiHelper().getGoogleApiClient();
        if(mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient),
                    1);
        }else{
            Toast.makeText(this,R.string.notConnected,Toast.LENGTH_SHORT).show();
        }

    }

    public void showLeaderBoard(View v){
//        mGoogleApiClient = App.getGoogleApiHelper().getGoogleApiClient();
        if(mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                    String.valueOf(R.string.leaderboard_victories)), 2);
        }else{
            Toast.makeText(this,R.string.notConnected,Toast.LENGTH_SHORT).show();
        }
    }
}

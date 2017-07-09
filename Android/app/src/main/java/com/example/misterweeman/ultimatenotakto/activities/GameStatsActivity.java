package com.example.misterweeman.ultimatenotakto.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.misterweeman.ultimatenotakto.App;
import com.example.misterweeman.ultimatenotakto.R;
import com.example.misterweeman.ultimatenotakto.services.MusicService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public class GameStatsActivity extends AppCompatActivity {
    GoogleApiClient mGoogleApiClient;
    private boolean mIsBound = false;
    private MusicService mServ;
    private boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = App.getGoogleApiHelper().getGoogleApiClient();
        setContentView(R.layout.base_layout);
        App.setLayout(this, R.layout.activity_game_stats);
        doBindService();
        Intent music = new Intent(this,MusicService.class);
        startService(music);
    }


    protected void onResume() {
        super.onResume();
        if(!firstTime) {
            mServ.resumeMusic();
            firstTime=false;
        }
    }

    protected void onPause() {
        super.onPause();
        mServ.pauseMusic();
    }

    protected void onRestart() {
        super.onRestart();
        mServ.resumeMusic();
    }

    protected void onDestroy() {
        doUnbindService();
        super.onDestroy();
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

    //bind servica musica
    private ServiceConnection Scon =new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService(){
        bindService(new Intent(this,MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService()
    {
        if(mIsBound)
        {
            unbindService(Scon);
            mIsBound = false;
        }
    }
}

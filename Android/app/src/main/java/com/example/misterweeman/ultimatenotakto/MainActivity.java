package com.example.misterweeman.ultimatenotakto;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import static com.example.misterweeman.ultimatenotakto.Utility.loadLocale;


public class MainActivity extends AppCompatActivity{

    private static final String TAG = "UltimateNotakto";
    private boolean mIsBound = false;
    private MusicService mServ;
    private boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        loadLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        doBindService();
        Intent music = new Intent(this,MusicService.class);
        startService(music);
        firstTime=true;
    }

    @Override
    protected void onRestart() {
        loadLocale(this);
        super.onRestart();
        setContentView(R.layout.activity_main);
        mServ.resumeMusic();
    }

    protected void onResume() {
        super.onResume();
        if(!firstTime) {
            mServ.resumeMusic();
            firstTime=false;
        }
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
    protected void onPause() {
        super.onPause();
        mServ.pauseMusic();
    }


    @Override
    public void onDestroy(){

        Log.d(TAG, "destroy");
        doUnbindService();
        if(!isChangingConfigurations()) {
            Intent music = new Intent(this, MusicService.class);
            stopService(music);
        }
        super.onDestroy();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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

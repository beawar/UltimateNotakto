package com.example.misterweeman.ultimatenotakto.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

import com.example.misterweeman.ultimatenotakto.services.MusicService;
import com.example.misterweeman.ultimatenotakto.R;

public class HowtoplayActivity extends Activity {

    private TextView howto;
    private boolean mIsBound = false;
    private MusicService mServ;
    private boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initializeVariables();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.howtoplay);
        doBindService();
        Intent music = new Intent(this,MusicService.class);
        startService(music);
    }


    @Override
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


    private void initializeVariables(){
        howto = (TextView) findViewById(R.id.how_to_play_content);
    }

    //bind service musica
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

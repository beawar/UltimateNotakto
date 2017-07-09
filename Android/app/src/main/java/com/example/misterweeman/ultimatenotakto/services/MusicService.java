package com.example.misterweeman.ultimatenotakto.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.example.misterweeman.ultimatenotakto.R;


public class MusicService extends Service  implements MediaPlayer.OnErrorListener {

    int maxVolume=1000;
    int currVolume;
    public static final String PREFS_NAME = "MySettingsFile";
    private final IBinder mBinder = new ServiceBinder();
    MediaPlayer mPlayer;
    private int length = 0;

    public MusicService() { }

    public class ServiceBinder extends Binder {
        public MusicService getService()
        {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0){return mBinder;}

    @Override
    public void onCreate (){
        super.onCreate();

        mPlayer = MediaPlayer.create(this, R.raw.bg_music);
        mPlayer.setOnErrorListener(this);

        if(mPlayer!= null)
        {
            mPlayer.setLooping(true);
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            currVolume = (settings.getInt("music", 100))*10;
            float log1=(float)(Math.log(maxVolume-currVolume)/Math.log(maxVolume));
            mPlayer.setVolume(1-log1,1-log1);
        }

    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId)
    {
        mPlayer.start();
        return START_STICKY;
    }

    public void pauseMusic()
    {
        if(mPlayer.isPlaying())
        {
            mPlayer.pause();
            length=mPlayer.getCurrentPosition();

        }
    }

    public void resumeMusic()
    {
        if(!mPlayer.isPlaying())
        {
            mPlayer.seekTo(length);
            mPlayer.start();
        }
    }

    public void changeVolume(int vol){
        vol=vol*10;
        float log1=(float)(Math.log(maxVolume-vol)/Math.log(maxVolume));
        mPlayer.setVolume(1-log1,1-log1);
    }

    public void stopMusic()
    {
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
    }

    @Override
    public void onDestroy ()
    {
        super.onDestroy();
        if(mPlayer != null)
        {
            try{
                mPlayer.stop();
                mPlayer.release();
            }finally {
                mPlayer = null;
            }
        }
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {

        Toast.makeText(this, "music player failed", Toast.LENGTH_SHORT).show();
        if(mPlayer != null)
        {
            try{
                mPlayer.stop();
                mPlayer.release();
            }finally {
                mPlayer = null;
            }
        }
        return false;
    }

}

package com.example.misterweeman.ultimatenotakto;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.util.Locale;

import static com.example.misterweeman.ultimatenotakto.Utility.loadLocale;

public class Options extends AppCompatActivity {
    private SeekBar SoundSeekbar;
    private SeekBar EffectsSeekbar;
    private TextView SoundVolume;
    private TextView EffectsVolume;
    private RadioGroup Languages;
    Locale myLocale;
    public static final String PREFS_NAME = "MySettingsFile";
    private boolean mIsBound = false;
    private MusicService mServ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        doBindService();
        Intent music = new Intent(this,MusicService.class);
        startService(music);
        initializeVariables();

        SoundSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int sound_progress = 0;
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {

                sound_progress=progresValue;
                SoundVolume.setText(""+sound_progress);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveMusicSetting(sound_progress);
                mServ.changeVolume(sound_progress);
            }

        });

        EffectsSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int effects_progress = 0;
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {

                effects_progress=progresValue;
                EffectsVolume.setText(""+effects_progress);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveSoundSetting(effects_progress);
            }

        });
        Languages.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                View radioButton = Languages.findViewById(checkedId);
                int index = Languages.indexOfChild(radioButton);

                // Add logic here

                switch (index) {
                    case 0: // first button
                        saveLanguage("en");
                        setLocale("en");
                        break;
                    case 1: // secondbutton
                        saveLanguage("it");
                        setLocale("it");
                        break;
                }
            }
        });


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mServ.resumeMusic();
    }

    protected void onPause() {
        super.onPause();
        mServ.pauseMusic();
    }

    // inizializza elementi del layout
    private void initializeVariables() {
        SoundSeekbar = (SeekBar) findViewById(R.id.Sound_seekbar);
        SoundVolume = (TextView) findViewById(R.id.Sound_volume);
        EffectsSeekbar = (SeekBar) findViewById(R.id.Effects_seekbar);
        EffectsVolume = (TextView) findViewById(R.id.Effects_volume);
        Languages = (RadioGroup) findViewById(R.id.languages);
        //per settare il radiobutton di default
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String lang = settings.getString("lang", "en");
        switch(lang) {
            case "en":
                ((RadioButton) Languages.getChildAt(0)).setChecked(true);
                break;
            case "it":
                ((RadioButton) Languages.getChildAt(1)).setChecked(true);
                break;
        }
        int music_volume= settings.getInt("music", 100);
        int effects_volume= settings.getInt("effects", 100);
        SoundSeekbar.setProgress(music_volume);
        SoundVolume.setText(""+music_volume);
        EffectsSeekbar.setProgress(effects_volume);
        EffectsVolume.setText(""+effects_volume);
    }

    //cambia lingua
    public void setLocale(String lang) {

        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, Options.class);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(refresh);
        finish();
    }



    // salva la lingua in Sharedpreferences
    public void saveLanguage(String lang){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("lang", lang);
        editor.apply();
    }
    // salva il volume della musica in Sharedpreferences
    public void saveMusicSetting(int volume){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("music", volume);
        editor.apply();
    }
    // salva il volume degli effetti sonori in Sharedpreferences
    public void saveSoundSetting(int volume){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("effects", volume);
        editor.apply();
    }

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

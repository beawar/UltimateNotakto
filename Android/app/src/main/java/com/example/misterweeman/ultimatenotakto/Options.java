package com.example.misterweeman.ultimatenotakto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        //inizializzo variabili
        initializeVariables();

        SoundSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int sound_progress = 0;
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {

                sound_progress=progresValue;
                SoundVolume.setText(""+sound_progress);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {
                //TODO sharedPreferences
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
                //TODO sharedPreferences
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
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        loadLocale(this);
    }

    @Override
    protected void onResume() {
        loadLocale(this);
        super.onResume();
    }

    public void onBackPressed(){
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    // inizializza elementi del layout
    private void initializeVariables() {
        SoundSeekbar = (SeekBar) findViewById(R.id.Sound_seekbar);
        SoundVolume = (TextView) findViewById(R.id.Sound_volume);
        EffectsSeekbar = (SeekBar) findViewById(R.id.Effects_seekbar);
        EffectsVolume = (TextView) findViewById(R.id.Effects_volume);
        Languages = (RadioGroup) findViewById(R.id.languages);
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

}

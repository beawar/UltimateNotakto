package com.example.misterweeman.ultimatenotakto;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class Options extends AppCompatActivity {
    private SeekBar SoundSeekbar;
    private SeekBar EffectsSeekbar;
    private TextView SoundVolume;
    private TextView EffectsVolume;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    }

    private void initializeVariables() {
        SoundSeekbar = (SeekBar) findViewById(R.id.Sound_seekbar);
        SoundVolume = (TextView) findViewById(R.id.Sound_volume);
        EffectsSeekbar = (SeekBar) findViewById(R.id.Effects_seekbar);
        EffectsVolume = (TextView) findViewById(R.id.Effects_volume);
    }
}

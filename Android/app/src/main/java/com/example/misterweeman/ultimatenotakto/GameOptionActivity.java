package com.example.misterweeman.ultimatenotakto;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;


public class GameOptionActivity extends AppCompatActivity {

    private static final String TAG = "GameOptionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.base_activity);

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_container);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout.addView(layoutInflater.inflate(R.layout.game_option_activity, layout, false));

    }

    public void createGame(View view){

        Log.d(TAG, "createGame()");

        RadioGroup boardSizeGroup = (RadioGroup) findViewById(R.id.button_size_layout);
        RadioGroup playerNumberGroup = (RadioGroup) findViewById(R.id.button_player_layout);

        int selectedBoardSize = boardSizeGroup.getCheckedRadioButtonId();
        int selectedPlayerNumber = playerNumberGroup.getCheckedRadioButtonId();

        Intent intent = new Intent(this, GameActivity.class);

        intent.putExtra("BoardSizeChecked", selectedBoardSize);
        intent.putExtra("PlayerNumberChecked", selectedPlayerNumber);

        startActivity(intent);

    }
}

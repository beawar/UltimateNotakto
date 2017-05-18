package com.example.misterweeman.ultimatenotakto;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.misterweeman.ultimatenotakto.view.BoardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BoardView boardView = new BoardView(this);
        boardView.setGridSize(6);

        setContentView(boardView);
    }

    @Override
    public void onDestroy(){
        System.out.print("Distrutto");
        super.onDestroy();
    }

}

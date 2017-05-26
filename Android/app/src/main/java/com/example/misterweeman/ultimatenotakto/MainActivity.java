package com.example.misterweeman.ultimatenotakto;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.example.misterweeman.ultimatenotakto.view.GameFragment;

public class MainActivity extends AppCompatActivity implements GameFragment.GameLostListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {
            // However, if we're being restored from a previous state,
            // then we don't need to do anything or else
            // we could end up with overlapping fragments.
            // Quindi controllo eseguo le istruzioni solo se non c'è uno stato precedente
            if (savedInstanceState == null) {
                // Create a new Fragment to be placed in the activity layout
                GameFragment gameFragment = new GameFragment();

                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                gameFragment.setArguments(getIntent().getExtras());

                // Add the fragment to the 'fragment_container' Layout
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, gameFragment).commit();
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onGameLost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.lost_dialog_message)
                .setTitle(R.string.list_dialog_title)
                .setPositiveButton(R.string.oh_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BlankFragment blankFragment = BlankFragment.newInstance("param1", "param2");

                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, blankFragment);

                        transaction.commit();
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                            finish();
                        }
                        return false;
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

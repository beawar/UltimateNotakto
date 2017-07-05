package com.example.misterweeman.ultimatenotakto;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.misterweeman.ultimatenotakto.view.GameFragment;

import static android.R.attr.fragment;
import java.util.ArrayDeque;
import java.util.Queue;

public class GameActivity extends AppCompatActivity implements
        GameFragment.GameListener {
    private static final String TAG = "GameActivity";
    private CountDownTimer timer;
    private AlertDialog alertDialog;
    private static final String ARG_GAMELOST = "gameLost";
    private boolean gameLost = false;
    private ConnectionHandler mConnectionHandler;
    private GameOptionFragment mGameOptionFragment;
    private GameFragment mGameFragment;
    private Fragment mCurrentFragment;
    private boolean isRunning;
    private Queue<FragmentTransactionHelper> fragmentTransactionHelpers = new ArrayDeque<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);
        mConnectionHandler = new ConnectionHandler(this, R.layout.activity_game);
        App.setLayout(this, R.layout.activity_game);

        //Create the fragments
        mGameOptionFragment = GameOptionFragment.newInstance();
        mGameOptionFragment.setArguments(getIntent().getExtras());
        mGameFragment = GameFragment.newInstance(getIntent().getIntExtra(getString(R.string.ARG_GRID_SIZE), 3));

        if (findViewById(R.id.fragment_container) != null) {
            if (!mConnectionHandler.isConnectedToRoom() && savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, mGameOptionFragment).commit();
                mCurrentFragment = mGameOptionFragment;
            } else if (savedInstanceState == null) {
                mGameFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, mGameFragment).commit();
                mCurrentFragment = mGameFragment;
            } else if (savedInstanceState.getBoolean(ARG_GAMELOST, true)) {
                onGameLost();
            }
        }

        // Intent intent = getIntent();

        // int boardSize = intent.getIntExtra("BoardSizeChecked", R.id.button_3x3);
        // int playerSize = intent.getIntExtra("PlayerNumberChecked", R.id.button_2players);

        // android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        // FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // GameFragment gameFragment = GameFragment.newInstance(getBoardSize(boardSize));

        // fragmentTransaction.add(R.id.game_board, gameFragment);
        // fragmentTransaction.commit();

        // setContentView(R.layout.base_activity);

        // LinearLayout layout = (LinearLayout) findViewById(R.id.layout_container);
        // LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // layout.addView(layoutInflater.inflate(R.layout.game_activity, layout, false));

        addPlayers(playerSize);

        createTimer();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
        isRunning = false;
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        isRunning = true;
    }

    @Override
    protected void onPostResume() {
        Log.d(TAG, "onPostResume: ");
        super.onPostResume();
        while (!fragmentTransactionHelpers.isEmpty()) {
            FragmentTransactionHelper fragmentTransactionHelper = fragmentTransactionHelpers.remove();
            fragmentTransactionHelper.commit();
            mCurrentFragment = fragmentTransactionHelper.getReplacingFragment();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: ");
        super.onSaveInstanceState(outState);
        if (alertDialog != null && alertDialog.isShowing()) {
            // close dialog to prevent leaked window
            alertDialog.dismiss();
        }
        outState.putBoolean(ARG_GAMELOST, gameLost);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState: ");
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            gameLost = savedInstanceState.getBoolean(ARG_GAMELOST, false);
        }
    }
*/

    @Override
    public void onGameLost() {
        Log.d(TAG, "onGameLost: ");
        gameLost = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.lost_dialog_message)
                .setTitle(R.string.list_dialog_title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: do something else
                        finish();
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
        alertDialog = builder.create();
        alertDialog.show();
    }

    public ConnectionHandler getConnectionHandler() {
        return mConnectionHandler;
    }


    public void startQuickGame(View view) {
        if (mGameOptionFragment != null) {
            mGameOptionFragment.startQuickGame(view);
        }
    }

    public void createGame(View view) {
        if (mGameOptionFragment != null) {
            mGameOptionFragment.createGame(view);
        }
    }

    public void replaceFragment(Fragment fragment) {
        Log.d(TAG, "replaceFragment: ");
        if (!isRunning) {
            FragmentTransactionHelper fragmentTransactionHelper = new FragmentTransactionHelper() {
                @Override
                public void commit() {
                    replaceFragmentInternal(getContentFrameId(), getReplacingFragment());
                }
            };

            fragmentTransactionHelper.setContentFrameId(R.id.fragment_container);
            fragmentTransactionHelper.setReplacingFragment(fragment);
            fragmentTransactionHelpers.add(fragmentTransactionHelper);
        } else {
            replaceFragmentInternal(R.id.fragment_container, fragment);
        }
    }

    private void replaceFragmentInternal(int contentFrameId, Fragment replacingFragment) {
        Log.d(TAG, "replaceFragmentInternal: ");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(contentFrameId, replacingFragment).commit();
        mCurrentFragment = replacingFragment;
        recreate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mCurrentFragment != null) {
            mCurrentFragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public GameOptionFragment getGameOptionFragment() {
        return mGameOptionFragment;
    }

    public GameFragment getGameFragment() {
        return mGameFragment;
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    public void updateBoard(int x, int y, String sender, int turn) {
        Log.d(TAG, "updateBoard()");
        mGameFragment.updateBoard(x, y, sender, turn);
    }

    private int getBoardSize(int id){

        int bSize = 3;

        if(id == R.id.button_3x3){
            bSize = 3;
        }
        else if(id == R.id.button_4x4){
            bSize = 4;
        }
        else if(id == R.id.button_5x5){
            bSize = 5;
        }
        else if(id == R.id.button_6x6) {
            bSize = 6;
        }

        return bSize;

    }

    private void createTimer(){

        final TextView textTimer = (TextView) findViewById(R.id.game_timer);
        final Toast toast = Toast.makeText(this, "Turno finito", Toast.LENGTH_LONG);

        timer = new CountDownTimer(40000, 1000) {

            public void onTick(long millisUntilFinished) {
                textTimer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish(){
                toast.show();
                this.start();
            }

        }.start();

    }

    private void addPlayers(int playerSize){

        TextView player1 = (TextView) findViewById(R.id.player_1);
        player1.setBackgroundResource(R.color.green);

        TextView player2 = (TextView) findViewById(R.id.player_2);
        player2.setBackgroundResource(R.color.green);

        if(playerSize == R.id.button_3players){

            TextView player3 = (TextView) findViewById(R.id.player_3);
            player3.setBackgroundResource(R.color.green);

        }
        else if(playerSize == R.id.button_4players){

            TextView player3 = (TextView) findViewById(R.id.player_3);
            player3.setBackgroundResource(R.color.green);

            TextView player4 = (TextView) findViewById(R.id.player_4);
            player4.setBackgroundResource(R.color.green);
        }
    }

    @Override
    public void onBackPressed(){
        timer.cancel();
    }

}

package com.example.misterweeman.ultimatenotakto;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.example.misterweeman.ultimatenotakto.view.GameFragment;

import java.util.ArrayDeque;
import java.util.Queue;

import static com.example.misterweeman.ultimatenotakto.view.GameFragment.DEFAULT_GRID_SIZE;
import static com.example.misterweeman.ultimatenotakto.view.GameFragment.DEFAULT_PLAYERS_NUM;

public class GameActivity extends AppCompatActivity implements
        GameFragment.GameListener {
    private static final String TAG = "GameActivity";
    private static final String ARG_GAMELOST = "gameLost";

    private AlertDialog alertDialog;
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
        App.setLayout(this, R.layout.game_activity);

        //Create the fragments
        mGameOptionFragment = GameOptionFragment.newInstance();
        mGameOptionFragment.setArguments(getIntent().getExtras());
        mGameFragment = GameFragment.newInstance(getIntent().getIntExtra(getString(R.string.ARG_GRID_SIZE), DEFAULT_GRID_SIZE),
                getIntent().getIntExtra(getString(R.string.ARG_PLAYERS_NUM), DEFAULT_PLAYERS_NUM));

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
        updateLayout();
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


    public void onWinning() {
        Log.d(TAG, "onGameWon: ");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.won_dialog_message)
                .setTitle(R.string.win_dialog_title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mConnectionHandler.leaveRoom();
                        finish();
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                            mConnectionHandler.leaveRoom();
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
        updateLayout();
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

    protected void updateLayout() {
        if (mCurrentFragment == mGameFragment) {
            findViewById(R.id.game_timer).setVisibility(View.VISIBLE);
            findViewById(R.id.players_layout).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.game_timer).setVisibility(View.GONE);
            findViewById(R.id.players_layout).setVisibility(View.GONE);
        }
    }

    public void onBackPressed(){
//        mTimer.cancel();
        Log.d(TAG, "onBackPressed: "+mConnectionHandler.getmRoomId());
        mConnectionHandler.onBackPressed();
        super.onBackPressed();
    }

    public void goToOptions(View view) {
        Log.d(TAG, "goToOptions: ");
        Intent intent = new Intent(this, Options.class);
        startActivity(intent);
    }
}

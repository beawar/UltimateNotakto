package com.example.misterweeman.ultimatenotakto.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.example.misterweeman.ultimatenotakto.App;
import com.example.misterweeman.ultimatenotakto.R;
import com.example.misterweeman.ultimatenotakto.fragments.GameFragment;
import com.example.misterweeman.ultimatenotakto.fragments.GameOptionFragment;
import com.example.misterweeman.ultimatenotakto.helpers.ConnectionHandler;
import com.example.misterweeman.ultimatenotakto.helpers.FragmentTransactionHelper;
import com.example.misterweeman.ultimatenotakto.services.MusicService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;

import java.util.ArrayDeque;
import java.util.Queue;

import static com.example.misterweeman.ultimatenotakto.fragments.GameFragment.DEFAULT_GRID_SIZE;
import static com.example.misterweeman.ultimatenotakto.fragments.GameFragment.DEFAULT_PLAYERS_NUM;

public class GameActivity extends AppCompatActivity implements
        GameFragment.GameListener {
    private static final String TAG = "GameActivity";
    private static final String ARG_GAMELOST = "gameLost";
    private static final String ARG_CURRFRAGMENT = "currentFragment";
    private static final String ARG_GAMEFRAGMENT = "gameFragment";
    private static final String ARG_GAMEOPTFRAGMENT = "gameOptionFragment";
    private static final String ARG_ROOMID = "roomId";

    private GoogleApiClient mGoogleApiClient;

    private AlertDialog alertDialog;
    private boolean gameLost = false;
    private ConnectionHandler mConnectionHandler;
    private GameOptionFragment mGameOptionFragment;
    private GameFragment mGameFragment;
    private Fragment mCurrentFragment;
    private boolean isRunning;
    private Queue<FragmentTransactionHelper> fragmentTransactionHelpers = new ArrayDeque<>();

    private boolean mIsBound = false;
    private MusicService mServ;
    private boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient=App.getGoogleApiHelper().getGoogleApiClient();

        setContentView(R.layout.base_layout);
        mConnectionHandler = new ConnectionHandler(this);
        Log.d(TAG, "onCreate: "+ mConnectionHandler.getRoomId());
        App.setLayout(this, R.layout.activity_game);

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
            } else {
                mConnectionHandler.setRoomId(ARG_ROOMID);
                gameLost = savedInstanceState.getBoolean(ARG_GAMELOST, false);
                if (gameLost) {
                    onGameLost();
                }
                String strFragment = savedInstanceState.getString(ARG_CURRFRAGMENT,
                        ARG_GAMEOPTFRAGMENT);
                if (strFragment != null) {
                    if (strFragment.equals(ARG_GAMEFRAGMENT)){
                        mCurrentFragment = mGameFragment;
                    } else if (strFragment.equals(ARG_GAMEOPTFRAGMENT)) {
                        mCurrentFragment = mGameOptionFragment;
                    }
                }
            }
        }

        doBindService();
        Intent music = new Intent(this,MusicService.class);
        startService(music);
        updateLayout();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: "+ mConnectionHandler.getRoomId());
        super.onPause();
        isRunning = false;
        if(mServ!=null) {
            mServ.pauseMusic();
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: "+ mConnectionHandler.getRoomId());
        super.onResume();
        isRunning = true;
        if(!firstTime) {
            mServ.resumeMusic();
            firstTime=false;
        }
    }


    protected void onRestart() {
        super.onRestart();
        mServ.resumeMusic();
    }
    @Override
    protected void onPostResume() {
        Log.d(TAG, "onPostResume: "+ mConnectionHandler.getRoomId());
        super.onPostResume();
        if (fragmentTransactionHelpers != null && !fragmentTransactionHelpers.isEmpty()) {
            while (!fragmentTransactionHelpers.isEmpty()) {
                FragmentTransactionHelper fragmentTransactionHelper = fragmentTransactionHelpers.remove();
                fragmentTransactionHelper.commit();
                mCurrentFragment = fragmentTransactionHelper.getReplacingFragment();
            }
            updateLayout();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: "+ mConnectionHandler.getRoomId());
        super.onSaveInstanceState(outState);
        if (alertDialog != null && alertDialog.isShowing()) {
            // close dialog to prevent leaked window
            alertDialog.dismiss();
        }
        outState.putBoolean(ARG_GAMELOST, gameLost);
        outState.putString(ARG_ROOMID, mConnectionHandler.getRoomId());
        String strFragment = null;
        if (mCurrentFragment == mGameFragment) {
            strFragment = ARG_GAMEFRAGMENT;
        } else if (mCurrentFragment == mGameOptionFragment) {
            strFragment = ARG_GAMEOPTFRAGMENT;
        }
        outState.putString(ARG_CURRFRAGMENT, strFragment);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState: "+ mConnectionHandler.getRoomId());
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            gameLost = savedInstanceState.getBoolean(ARG_GAMELOST, false);
            mConnectionHandler.setRoomId(savedInstanceState.getString(ARG_ROOMID));
            String strCurrentFragment = savedInstanceState.getString(ARG_CURRFRAGMENT,
                    ARG_GAMEOPTFRAGMENT);
            if (strCurrentFragment != null) {
                if (strCurrentFragment.equals(ARG_GAMEFRAGMENT)) {
                    mCurrentFragment = mGameFragment;
                } else if (strCurrentFragment.equals(ARG_GAMEOPTFRAGMENT)) {
                    mCurrentFragment = mGameOptionFragment;
                }
            }
        }
        updateLayout();
    }

    @Override
    public void onGameLost() {
        Log.d(TAG, "onGameLost: "+ mConnectionHandler.getRoomId());
        gameLost = true;
        Games.Achievements.increment(mGoogleApiClient, String.valueOf(R.string.achievement_loser), 1);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.lost_dialog_message)
                .setTitle(R.string.list_dialog_title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing: become a watcher
                    }
                })
                .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mConnectionHandler.leaveRoom();
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                            mConnectionHandler.leaveRoom();
                        }
                        return false;
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onGameWon() {
        Log.d(TAG, "onGameWon: "+ mConnectionHandler.getRoomId());
        Games.Achievements.increment(mGoogleApiClient, String.valueOf(R.string.achievement_winner), 1);
        addScore();
        mConnectionHandler.broadcastWin();
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

//    @Override
//    public void onGameEnd(String winner) {
//        Log.d(TAG, "onGameEnd: "+ mConnectionHandler.getRoomId());
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(getResources().getString(R.string.winner_is, winner))
//                .setTitle(R.string.winner_is_title)
//                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        mConnectionHandler.leaveRoom();
//                        finish();
//                    }
//                })
//                .setOnKeyListener(new DialogInterface.OnKeyListener() {
//                    @Override
//                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
//                            mConnectionHandler.leaveRoom();
//                            finish();
//                        }
//                        return false;
//                    }
//                });
//        alertDialog = builder.create();
//        alertDialog.show();
//    }

    public ConnectionHandler getConnectionHandler() {
        return mConnectionHandler;
    }

    public void createGame(View view) {
        if (mGameOptionFragment != null) {
            mGameOptionFragment.createGame(view);
        }
    }

    public void startQuickGame(View view) {
        if (mGameOptionFragment != null) {
            mGameOptionFragment.startQuickGame(view);
        }
    }

    public void replaceFragment(Fragment fragment) {
        Log.d(TAG, "replaceFragment: "+ mConnectionHandler.getRoomId());
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
        Log.d(TAG, "replaceFragmentInternal: "+ mConnectionHandler.getRoomId());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(contentFrameId, replacingFragment).commit();
        mCurrentFragment = replacingFragment;
        recreate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + mConnectionHandler.getRoomId());
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
        Log.d(TAG, "updateBoard() "+ mConnectionHandler.getRoomId());
        mGameFragment.updateBoard(x, y, sender, turn);
    }

    public void updateGraphics() {
        mGameFragment.turnGraphics(mConnectionHandler.getCurrTurn());
    }

    protected void updateLayout() {
        Log.d(TAG, "updateLayout: " + mConnectionHandler.getRoomId());
        if (mCurrentFragment == mGameFragment) {
            findViewById(R.id.game_timer).setVisibility(View.VISIBLE);
            findViewById(R.id.players_layout).setVisibility(View.VISIBLE);
        } else if (mCurrentFragment == mGameOptionFragment){
            findViewById(R.id.game_timer).setVisibility(View.GONE);
            findViewById(R.id.players_layout).setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed(){
        Log.d(TAG, "onBackPressed: "+ mConnectionHandler.getRoomId());
        mConnectionHandler.onBackPressed();
    }

    public void goToOptions(View view) {
        Log.d(TAG, "goToOptions: " + mConnectionHandler.getRoomId());
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: " + mConnectionHandler.getRoomId());
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        super.onDestroy();
        doUnbindService();


    }

    public void setPlayersNum(int number) {
        mGameFragment.setPlayersNum(number);
    }

    public void setGridSize(int gridSize) {
        mGameFragment.setGridSize(gridSize);
    }

    protected void addScore() {
        Games.Leaderboards.loadCurrentPlayerLeaderboardScore(mGoogleApiClient,
                String.valueOf(R.string.leaderboard_victories),
                LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC)
                .setResultCallback(
                        new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
                            @Override
                            public void onResult(@NonNull final Leaderboards.LoadPlayerScoreResult scoreResult) {
                                if (isScoreResultValid(scoreResult)) {
                                    long mPoints = scoreResult.getScore().getRawScore();
                                    Games.Leaderboards.submitScore(mGoogleApiClient,
                                            String.valueOf(R.string.leaderboard_victories), mPoints + 1);
                                }
                            }
                        }
                );
    }

    protected boolean isScoreResultValid(final Leaderboards.LoadPlayerScoreResult scoreResult) {
        return  GamesStatusCodes.STATUS_OK == scoreResult.getStatus().getStatusCode() && scoreResult.getScore() != null;
    }

    //bind servica musica
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

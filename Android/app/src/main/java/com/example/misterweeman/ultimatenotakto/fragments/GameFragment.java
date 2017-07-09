package com.example.misterweeman.ultimatenotakto.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.misterweeman.ultimatenotakto.R;
import com.example.misterweeman.ultimatenotakto.activities.GameActivity;
import com.example.misterweeman.ultimatenotakto.helpers.ConnectionHandler;
import com.example.misterweeman.ultimatenotakto.model.Board;
import com.example.misterweeman.ultimatenotakto.model.Notakto;
import com.example.misterweeman.ultimatenotakto.view.BoardView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment implements
        View.OnTouchListener {
    private static final String TAG = "GameFragment";
    private static final String ARG_GRIDSIZE = "mGridSize";
    private static final String ARG_PLAYERS = "PlayerNumberChecked";
    public static final int DEFAULT_GRID_SIZE = 3;
    public static final int DEFAULT_PLAYERS_NUM = 2;
    public static final int TURN_TIME = 30;

    private Board mBoard;
    private BoardView mBoardView;
    private int mGridSize = DEFAULT_GRID_SIZE;
    private int mPlayersNum = DEFAULT_PLAYERS_NUM;
    private CountDownTimer mTimer;
    private boolean mTimerIsRunning;

    private List<String> mPlayersList;

    private GameListener mGameListener;
    private ConnectionHandler mConnectionHandler;

    private TextView player1;
    private TextView player2;
    private TextView player3;
    private TextView player4;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GameFragment.
     */
    public static GameFragment newInstance(int gridSize, int players) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_GRIDSIZE, gridSize);
        args.putInt(ARG_PLAYERS, players);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBoard = new Board(mGridSize);
        mPlayersList = new ArrayList<>();
        setRetainInstance(true);
        if (getActivity() instanceof GameActivity) {
            mConnectionHandler =((GameActivity) getActivity()).getConnectionHandler();
            Log.d(TAG, "onCreate: " + mConnectionHandler.getRoomId());
        }
        final TextView textTimer = (TextView) getActivity().findViewById(R.id.game_timer);
        createTimer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: " + mConnectionHandler.getRoomId());
        mBoardView = new BoardView(getActivity());
        mBoardView.setGrid(this.mBoard);
        mBoardView.setOnTouchListener(this);
        addPlayersLabels(mPlayersNum);
        turnGraphics(mConnectionHandler.getCurrTurn());
        return mBoardView;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: " + mConnectionHandler.getRoomId());
        super.onResume();
//        if (!mTimerIsRunning && mConnectionHandler.isMyTurn()) {
//            startTimer();
//        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: " + mConnectionHandler.getRoomId());
        super.onDestroy();
//        if (mTimer != null) {
//            mTimer.cancel();
//        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "onTouch: " + event.getAction());
        boolean onTouchEvent = v.onTouchEvent(event);
        if (v instanceof BoardView && event.getAction() == MotionEvent.ACTION_UP) {
            BoardView bv = (BoardView) v;
            int x = bv.getXTouch();
            int y = bv.getYTouch();
            if (x < mGridSize && y < mGridSize) {
                if (mConnectionHandler.isMyTurn()) {
                    if (!mConnectionHandler.hasLost()) {
                        if (mTimerIsRunning && mTimer != null) {
                            // stop the timer
                            mTimer.cancel();
                            mTimerIsRunning = false;
                        }

                        // if it's my turn and I have not lost yet, I play
                        // if I click on an already cheched cell, do nothing and wait for a valid touch
                        if (bv.updateBoard(x, y, BoardView.getColors()[mConnectionHandler.getCurrTurn()])) {
                            // if the touch is valid, check for lost and broadcast the move
                            if (Notakto.checkBoardForLost(mBoard, x, y)) {
                                if (mGameListener != null) {
                                    mGameListener.onGameLost();
                                }
                                // if it's my turn and i just lost
                                mConnectionHandler.broadcastTurn(true, x, y);
                            } else {
                                // if it's my turn and I haven't lost yet
                                mConnectionHandler.broadcastTurn(false, x, y);
                            }
                        }
                        turnGraphics(mConnectionHandler.getCurrTurn());
                    } else {
                        // if it's my turn but I lost already, I just skip it
                        mConnectionHandler.broadcastTurn(true, -1, -1);
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.notTurn, Toast.LENGTH_SHORT).show();
                    if (!mConnectionHandler.hasLost()) {
                        Toast.makeText(getActivity(), R.string.notTurn, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: ");
        super.onAttach(context);
        if (context instanceof GameListener) {
            mGameListener = (GameListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: " + mConnectionHandler.getRoomId());
        super.onDetach();
        mGameListener = null;
    }

    public void updateBoard(int x, int y, String sender, int turn) {
        Log.d(TAG, "updateBoard: " + mConnectionHandler.getRoomId());
        if (sender != null && !sender.isEmpty() && !mPlayersList.contains(sender)) {
            mPlayersList.add(sender);
        }
        if (mBoardView != null) {
            int color = BoardView.getColors()[turn];
            boolean set = mBoardView.updateBoard(x, y, color);
            turnGraphics(mConnectionHandler.getCurrTurn());
            if (set && mConnectionHandler.hasPlayerLost(sender) && mConnectionHandler.checkForWin()) {
                mGameListener.onGameWon();
            }
            startTimer();
        }
    }

    // This method is called when a player doesn't make his move before the timer ends.
    public void onTurnFinished() {
        Log.d(TAG, "onTurnFinished: ");
        // Obtain MotionEvent object
        if (mConnectionHandler.isMyTurn()) {
            boolean done = false;
            for (int y = 0; y < mGridSize && !done; ++y) {
                for (int x = 0; x < mGridSize && !done; ++x) {
//                    if (mBoardView.updateBoard(x, y, mConnectionHandler.getCurrTurn())) {
//                        boolean isLost = Notakto.checkBoardForLost(mBoard, x, y);
//                        if (isLost && mGameListener != null) {
//                            mGameListener.onGameLost();
//                        }
//                        mConnectionHandler.broadcastTurn(isLost, x, y);
//                        done = true;
//                    }
                    if (!mBoard.isChecked(x, y)) {
                        done = true;
                        long downTime = SystemClock.uptimeMillis();
                        long eventTime = SystemClock.uptimeMillis() + 100;
                        // List of meta states found here: developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
                        int metaState = 0;
                        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime,
                                MotionEvent.ACTION_UP, x, y, metaState);
                        // Dispatch touch event to view
                        mBoardView.dispatchTouchEvent(motionEvent);
                    }
                }
            }
        }
    }

    private void addPlayersLabels(int playersNum){
        String[] playerNames = mConnectionHandler.getNames();
        player1 = (TextView) getActivity().findViewById(R.id.player_1);
        player2 = (TextView) getActivity().findViewById(R.id.player_2);
        player3 = (TextView) getActivity().findViewById(R.id.player_3);
        player4 = (TextView) getActivity().findViewById(R.id.player_4);

        player1.setText(playerNames[0]);
        player2.setText(playerNames[1]);

        if (playersNum < 3) {
            player3.setVisibility(View.GONE);
        } else {
            player3.setText(playerNames[2]);
        }
        if (playersNum < 4) {
            player4.setVisibility(View.GONE);
        } else {
            player4.setText(playerNames[3]);
        }
    }

    protected void createTimer() {
        Log.d(TAG, "createTimer: " + mConnectionHandler.getRoomId());
        final TextView textTimer = (TextView) getActivity().findViewById(R.id.game_timer);
        textTimer.setText(String.valueOf(TURN_TIME));
        mTimer = new CountDownTimer(TURN_TIME * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                textTimer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                Toast.makeText(getActivity(), R.string.finished_turn, Toast.LENGTH_LONG).show();
//                onTurnFinished();
                mGameListener.onGameLost();
                mTimerIsRunning = false;
            }
        };
        mTimerIsRunning = false;
    }

    protected void startTimer(){
        Log.d(TAG, "startTimer: " + mConnectionHandler.getRoomId());
        if (mTimer == null) {
            createTimer();
        }
        if (mConnectionHandler.isMyTurn()) {
            getActivity().findViewById(R.id.game_timer).setVisibility(View.VISIBLE);
            mTimer.start();
            mTimerIsRunning = true;
        } else {
            getActivity().findViewById(R.id.game_timer).setVisibility(View.GONE);
            ((TextView) getActivity().findViewById(R.id.game_timer)).setText(String.valueOf(TURN_TIME));
        }
    }

    public void turnGraphics(int i){
        if(i>=mPlayersNum){
            i = 0;
        }
        Log.d(TAG, "turnGraphics: " +i);
        switch(i){
            case 0:
                player1.setTextColor(Color.WHITE);
                player2.setTextColor(Color.BLACK);
                player3.setTextColor(Color.BLACK);
                player4.setTextColor(Color.BLACK);
                break;
            case 1:
                player1.setTextColor(Color.BLACK);
                player2.setTextColor(Color.WHITE);
                player3.setTextColor(Color.BLACK);
                player4.setTextColor(Color.BLACK);
                break;
            case 2:
                player1.setTextColor(Color.BLACK);
                player2.setTextColor(Color.BLACK);
                player3.setTextColor(Color.WHITE);
                player4.setTextColor(Color.BLACK);
                break;
            case 3:
                player1.setTextColor(Color.BLACK);
                player2.setTextColor(Color.BLACK);
                player3.setTextColor(Color.BLACK);
                player4.setTextColor(Color.WHITE);
                break;
        }
    }

    public int getPlayersNum() {
        return mPlayersNum;
    }

    public void setPlayersNum(int playersNum) {
        this.mPlayersNum = playersNum;
    }

    public void setGridSize(int gridSize) {
        this.mGridSize = gridSize;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface GameListener{
        void onGameLost();
        void onGameWon();
        void onGameEnd(String winner);
    }
}
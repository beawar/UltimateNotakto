package com.example.misterweeman.ultimatenotakto.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
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

    private Board mBoard;
    private BoardView mBoardView;
    private int mGridSize;
    private int mPlayersNum;
    private CountDownTimer mTimer;


    private List<String> mPlayersList;

    private GameListener mGameListener;
    private ConnectionHandler mConnectionHandler;

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
        if (getArguments() != null) {
            mGridSize = getArguments().getInt(ARG_GRIDSIZE, DEFAULT_GRID_SIZE);
            mPlayersNum = getArguments().getInt(ARG_PLAYERS, DEFAULT_PLAYERS_NUM);

        }
        mBoard = new Board(mGridSize);
        mPlayersList = new ArrayList<>();
        setRetainInstance(true);
        if (getActivity() instanceof GameActivity) {
            mConnectionHandler =((GameActivity) getActivity()).getConnectionHandler();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBoardView = new BoardView(getActivity());
        mBoardView.setGrid(this.mBoard);
        mBoardView.setOnTouchListener(this);
        addPlayersLabels(mPlayersNum);
        return mBoardView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d("GameFragment", "onTouch: " + event.getAction());
        boolean onTouchEvent = v.onTouchEvent(event);
        if (v instanceof BoardView && event.getAction() == MotionEvent.ACTION_UP) {
            BoardView bv = (BoardView) v;
            int x = bv.getXTouch();
            int y = bv.getYTouch();
            if (x < mGridSize && y < mGridSize) {
                if (mConnectionHandler.isMyTurn()) {
                    if (!mConnectionHandler.hasLost()) {
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
                    } else {
                        // if it's my turn but I lost already, I just skip it
                        mConnectionHandler.broadcastTurn(true, -1, -1);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setPlayersNum(mPlayersNum);
        if (context instanceof GameListener) {
            mGameListener = (GameListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mGameListener = null;
    }

    public boolean updateBoard(int x, int y, String sender, int turn) {
        Log.d(TAG, "updateBoard()" );
        if (sender != null && !sender.isEmpty() && !mPlayersList.contains(sender)) {
            mPlayersList.add(sender);
        }
        if (mBoardView != null) {
            int color = BoardView.getColors()[turn];
            boolean set = mBoardView.updateBoard(x, y, color);
            if (set && mConnectionHandler.hasPlayerLost(sender) && mConnectionHandler.checkForWin()) {
                mGameListener.onGameWon();
            }
            return set;
        }
        return false;
    }

    // This method is called when a player doesn't make his move before the timer ends.
    public void onTurnFinished() {
        Log.d(TAG, "onTurnFinished: ");
        boolean done = false;
        for (int y=0; y<mGridSize && !done; ++y) {
            for (int x=0; x<mGridSize && !done; ++x) {
                if (mBoardView.updateBoard(x, y,  mConnectionHandler.getCurrTurn())) {
                    boolean isLost = Notakto.checkBoardForLost(mBoard, x, y);
                    if (isLost && mGameListener != null) {
                        mGameListener.onGameLost();
                    }
                    mConnectionHandler.broadcastTurn(isLost, x, y);
                    done = true;
                }
            }
        }
    }

    private void addPlayersLabels(int playersNum){
//        TextView player1 = (TextView) getActivity().findViewById(R.id.player_1);
//        TextView player2 = (TextView) getActivity().findViewById(R.id.player_2);

//        player1.setBackgroundResource(R.color.green);
//        player2.setBackgroundResource(R.color.green);

        if (playersNum < 3) {
            TextView player3 = (TextView) getActivity().findViewById(R.id.player_3);
            player3.setVisibility(View.GONE);
        }
        if (playersNum < 4) {
            TextView player4 = (TextView) getActivity().findViewById(R.id.player_4);
            player4.setVisibility(View.GONE);
        }
    }

    private void createTimer(){
        final TextView textTimer = (TextView) getActivity().findViewById(R.id.game_timer);
        final Toast toast = Toast.makeText(getActivity(), R.string.finished_turn, Toast.LENGTH_LONG);

        mTimer = new CountDownTimer(40000, 1000) {

            public void onTick(long millisUntilFinished) {
                textTimer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish(){
                toast.show();
                onTurnFinished();
                this.start();
            }
        }.start();
    }

    public int getPlayersNum() {
        return mPlayersNum;
    }

    public void setPlayersNum(int mPlayersNum) {
        this.mPlayersNum = mPlayersNum;
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
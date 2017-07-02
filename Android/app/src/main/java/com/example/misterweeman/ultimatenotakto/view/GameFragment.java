package com.example.misterweeman.ultimatenotakto.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.misterweeman.ultimatenotakto.ConnectionHandler;
import com.example.misterweeman.ultimatenotakto.GameActivity;
import com.example.misterweeman.ultimatenotakto.model.Board;
import com.example.misterweeman.ultimatenotakto.model.Notakto;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment implements
        View.OnTouchListener, RealTimeMessageReceivedListener {
    private static final String ARG_GRIDSIZE = "gridSize";
    private Board board;
    private BoardView boardView;
    private int gridSize = 3;

    private List<String> players;

    private GameLostListener gameLostListener;
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
    public static GameFragment newInstance(int gridSize) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_GRIDSIZE, gridSize);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gridSize = getArguments().getInt(ARG_GRIDSIZE, 3);
        }
        board = new Board(gridSize);
        players = new ArrayList<>();
        setRetainInstance(true);
        if (getActivity() instanceof GameActivity) {
            mConnectionHandler =((GameActivity) getActivity()).getConnectionHandler();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        boardView = new BoardView(getActivity());
        boardView.setGrid(this.board);
        boardView.setOnTouchListener(this);
        return boardView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v instanceof BoardView && event.getAction() == MotionEvent.ACTION_UP) {
            BoardView bv = (BoardView) v;
            int x = bv.getXTouch();
            int y = bv.getYTouch();
            if (x < gridSize && y < gridSize) {
                if (mConnectionHandler.isMyTurn()) {
                    if (!mConnectionHandler.hasLost()) {
                        // if it's my turn and I have not lost yet, I play
                        boolean b = bv.onTouchEvent(event);
                        if (Notakto.checkBoardForLost(board, x, y)) {
                            if (gameLostListener != null) {
                                gameLostListener.onGameLost();
                            }
                            // if it's my turn and i just lost
                            mConnectionHandler.broadcastTurn(true, x, y);
                        } else {
                            // if it's my turn and I haven't lost yet
                            mConnectionHandler.broadcastTurn(false, x, y);
                        }
                        return b;
                    }
                    // if it's my turn but I lost already, I just pass it
                    mConnectionHandler.broadcastTurn(true, -1, -1);
                }
                return false;
            }
        }
        return v.onTouchEvent(event);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GameLostListener) {
            gameLostListener = (GameLostListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        gameLostListener = null;
    }

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        mConnectionHandler.onRealTimeMessageReceived(realTimeMessage);
        byte[] buf = realTimeMessage.getMessageData();
        String sender = realTimeMessage.getSenderParticipantId();
        boolean hasLost = (char) buf[0] == 'Y';
        int x = (int) buf[1];
        int y = (int) buf[2];
        int turn = (int) buf[3];
        updateBoard(x, y, sender, turn);
    }

    public void updateBoard(int x, int y, String sender, int turn) {
        if (!players.contains(sender)) {
            players.add(sender);
        }
        if (boardView != null) {
            int color = BoardView.getColors()[turn];
            boardView.updateBoard(x, y, color);
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface GameLostListener {
        void onGameLost();
    }
}
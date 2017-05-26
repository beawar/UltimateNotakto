package com.example.misterweeman.ultimatenotakto.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.misterweeman.ultimatenotakto.model.Board;
import com.example.misterweeman.ultimatenotakto.model.Notakto;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment implements View.OnTouchListener {
    private static final String ARG_GRIDSIZE = "gridSize";
    private Board board;
    private int gridSize = 3;

    private GameLostListener gameLostListener;

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
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final BoardView boardView = new BoardView(getActivity());
        boardView.setGrid(this.board);
        boardView.setOnTouchListener(this);
        return boardView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean viewOnTouchEvent = v.onTouchEvent(event);
        if (v instanceof BoardView && event.getAction() == MotionEvent.ACTION_UP) {
            BoardView bv = (BoardView) v;
            if (bv.getXTouch() < gridSize && bv.getYTouch() < gridSize) {
                if (Notakto.checkBoardForLost(board, bv.getXTouch(), bv.getYTouch())) {
                    System.out.println("LOST");
                    if (gameLostListener != null) {
                        gameLostListener.onGameLost();
                    }
                    return true;
                }
            }
            System.out.println("Continua a giocare");
            return false;
        }
        return viewOnTouchEvent;
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
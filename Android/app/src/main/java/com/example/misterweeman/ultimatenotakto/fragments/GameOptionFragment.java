package com.example.misterweeman.ultimatenotakto.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.misterweeman.ultimatenotakto.App;
import com.example.misterweeman.ultimatenotakto.R;
import com.example.misterweeman.ultimatenotakto.activities.GameActivity;
import com.example.misterweeman.ultimatenotakto.helpers.ConnectionHandler;


public class GameOptionFragment extends Fragment{

    private static final String TAG = "GameOptionFragment";

    private ConnectionHandler mConnectionHandler;
    private Activity mParentActivity;

    public GameOptionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GameFragment.
     */
    public static GameOptionFragment newInstance() {
        return new GameOptionFragment();
    }


    private int getNumberOfPlayers() {
        // number of player is stored on the checked radio button
        RadioButton checkedRadioPlayers = (RadioButton) mParentActivity.findViewById(
                ((RadioGroup) mParentActivity.findViewById(R.id.button_player_layout)).getCheckedRadioButtonId());
        return Integer.parseInt(checkedRadioPlayers.getText().toString());
    }

    private int getBoardSize() {
        // size of board is stored on the checked radio button
        RadioButton checkedRadioSize = (RadioButton) mParentActivity.findViewById(
                ((RadioGroup) mParentActivity.findViewById(R.id.button_size_layout)).getCheckedRadioButtonId());
        return Integer.parseInt(checkedRadioSize.getText().toString().substring(0, 1));
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mParentActivity = getActivity();
        if (mParentActivity instanceof GameActivity) {
            mConnectionHandler = ((GameActivity) mParentActivity).getConnectionHandler();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_game_option, container);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void createGame(View view) {
        if (mConnectionHandler != null) {
            if (getActivity() instanceof GameActivity) {
                GameActivity gameActivity = ((GameActivity) getActivity());
                gameActivity.setPlayersNum(getNumberOfPlayers());
                gameActivity.setGridSize(getBoardSize());
            }
            mConnectionHandler.createGame(getNumberOfPlayers()-1,getBoardSize());
            Toast.makeText(getActivity(), R.string.automatching , Toast.LENGTH_SHORT).show();
        }
    }

    public void startQuickGame(View view) {
        if (mConnectionHandler != null) {
            mConnectionHandler.startQuickGame();
            Toast.makeText(getActivity(), R.string.automatching , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        if (App.getGoogleApiHelper().getGoogleApiClient() == null) {
            // TODO: 04/06/2017 Mostrare fragment per sign-in o gioco offline
        } else if (!App.getGoogleApiHelper().isConnected()) {
            Log.d(TAG, "Connecting client");
            App.getGoogleApiHelper().connect();
        } else {
            Log.w(TAG, "GameHelper: client was already connected on onStart()");
        }
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mConnectionHandler.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}

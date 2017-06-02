package com.example.misterweeman.ultimatenotakto;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;

import static android.app.Activity.RESULT_OK;

public class SignInFragment extends Fragment implements GoogleApiHelper.ConnectionListener{

    private GoogleApiHelper mGoogleApiHelper;

    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mSignInClicked = false;
    // set true when you are in the middle of the sign in flow, to know you should not attempt to connect in OnStart()
    private boolean mInSignInFlow = false;
    private boolean mExplicitSignOut = false;

    private SignInButton mSignInButton;
    private Button mSignOutButton;
//    private View.OnClickListener mClickListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiHelper = App.getGoogleApiHelper();
        if (!mGoogleApiHelper.isSetConnectionListener()){
            mGoogleApiHelper.setConnectionListener(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_signin, container, false);
        if (view != null){
            mSignInButton = (SignInButton) view.findViewById(R.id.sign_in_button);
            mSignOutButton = (Button) view.findViewById(R.id.sign_out_button);
        }
        if (mSignInButton != null && mSignOutButton != null){
            mSignInButton.setOnClickListener(this);
            mSignOutButton.setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mGoogleApiHelper != null) {
            mGoogleApiHelper.setConnectionListener(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mGoogleApiHelper != null) {
            mGoogleApiHelper.setConnectionListener(null);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // if not already resolving
        if (!mResolvingConnectionFailure) {
            // if the sign-in button was clicked or if auto sign-in is enabled, launch the sign-in flow
            if (mSignInClicked || mAutoStartSignInflow) {
                mAutoStartSignInflow = false;
                mSignInClicked = false;
                mResolvingConnectionFailure = false;

                // Attempt to resolve the connection failure using BaseGameUtils
                if (!BaseGameUtils.resolveConnectionFailure(getActivity(),
                        mGoogleApiHelper.getGoogleApiClient(), connectionResult, RC_SIGN_IN,
                        R.string.sign_in_other_error)) {
                    mResolvingConnectionFailure = false;
                }
            }

        }
        mSignInButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mSignInButton.getVisibility() != View.GONE) {
            // The player is signed in. Hide the sign-in button and allow the player to proceed
            mSignInButton.setVisibility(View.GONE);
            mSignOutButton.setVisibility(View.VISIBLE);
        }
        // Your code here: update UI, enable functionality that depends on sign in, etc
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiHelper.connect();
            } else {
                // Bring up an error dialog to alert the user that the sign-in failed
                BaseGameUtils.showActivityResultError(getActivity(), requestCode, resultCode, R.string.signin_failure);
            }
        }
    }

    public void signIn() {
        mInSignInFlow = true;
        mSignInClicked = true;
        mGoogleApiHelper.connect();
    }

    public void signOut() {
        mExplicitSignOut = true;
        mSignInClicked = false;
        if (mGoogleApiHelper.isConnected()){
            Games.signOut(mGoogleApiHelper.getGoogleApiClient());
            mGoogleApiHelper.disconnect();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mInSignInFlow && !mExplicitSignOut){
            // Auto sign-in
            mGoogleApiHelper.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiHelper.disconnect();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            signIn();

        } else if (v.getId() == R.id.sign_out_button) {
            signOut();
            mSignInButton.setVisibility(View.VISIBLE);
            mSignOutButton.setVisibility(View.GONE);
        }
    }

}

package com.example.misterweeman.ultimatenotakto.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.misterweeman.ultimatenotakto.App;
import com.example.misterweeman.ultimatenotakto.R;
import com.example.misterweeman.ultimatenotakto.helpers.GoogleApiHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.example.games.basegameutils.BaseGameUtils;

import static android.app.Activity.RESULT_OK;

public class SignInFragment extends Fragment implements GoogleApiHelper.ConnectionListener{
    private static final String TAG = "SignInFragment";
    private GoogleApiHelper mGoogleApiHelper;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mSignInClicked = false;
    // set true when you are in the middle of the sign in flow, to know you should not attempt to connect in OnStart()
    private boolean mInSignInFlow = false;
    private boolean mExplicitSignOut = false;

    private SignInButton mSignInButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiHelper = App.getGoogleApiHelper();
        if (!mGoogleApiHelper.isSetConnectionListener()){
            mGoogleApiHelper.setConnectionListener(this);
        }
    }

    private void setButtonVisibility() {
        if (mGoogleApiHelper.isConnected()){
            mSignInButton.setVisibility(View.GONE);
        } else {
            mSignInButton.setVisibility(View.VISIBLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_signin, container, false);
        if (view != null){
            mSignInButton = (SignInButton) view.findViewById(R.id.sign_in_button);
        }
        if (mSignInButton != null){
            mSignInButton.setOnClickListener(this);
            setButtonVisibility();
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
                mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(getActivity(),
                        mGoogleApiHelper.getGoogleApiClient(), connectionResult, GoogleApiHelper.RC_SIGN_IN,
                        R.string.sign_in_other_error);
            }

        }
        setButtonVisibility();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setButtonVisibility();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GoogleApiHelper.RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiHelper.connect();
            } else {
                // Bring up an error dialog to alert the user that the sign-in failed
                BaseGameUtils.showActivityResultError(getActivity(), requestCode, resultCode, R.string.signin_failure);
            }
        }
        setButtonVisibility();
    }

    public void signIn() {
        mInSignInFlow = true;
        mSignInClicked = true;
        mGoogleApiHelper.connect();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mInSignInFlow && !mExplicitSignOut){
            // Auto sign-in
            mGoogleApiHelper.connect();
        }
        setButtonVisibility();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            signIn();
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        setButtonVisibility();
        super.onResume();
    }
}

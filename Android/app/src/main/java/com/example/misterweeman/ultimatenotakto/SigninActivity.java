package com.example.misterweeman.ultimatenotakto;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;

public class SigninActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mSignInClicked = false;
    // set true when you are in the middle of the sign in flow, to know you should not attempt to connect in OnStart()
    private boolean mInSignInFlow = false;
    private boolean mExplicitSignOut = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                // add others APIs and scopes here as needed
                .build();

        setContentView(R.layout.activity_signin);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mInSignInFlow && !mExplicitSignOut){
            // Auto sign-in
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // The player is signed in. Hide the sign-in button and allow the player to proceed
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);

        // Your code here: update UI, enable functionality that depends on sign in, etc
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
                if (!BaseGameUtils.resolveConnectionFailure(this,
                        mGoogleApiClient, connectionResult, RC_SIGN_IN,
                        R.string.sign_in_other_error)) {
                    mResolvingConnectionFailure = false;
                }
            }

            // Put code here to display the sign-in button
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that the sign-in failed
                BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.signin_failure);
            }
        }
    }

    private void signInClicked() {
        mSignInClicked = true;
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }
    }

    private void signOutClicked() {
        mSignInClicked = false;
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()){
            Games.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            mInSignInFlow = true;
            signInClicked();
        } else if (v.getId() == R.id.sign_out_button) {
            mExplicitSignOut = true;
            signOutClicked();
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }
}

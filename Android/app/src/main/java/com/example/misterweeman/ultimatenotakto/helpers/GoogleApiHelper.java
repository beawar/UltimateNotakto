package com.example.misterweeman.ultimatenotakto.helpers;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;


public class GoogleApiHelper implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private static final String TAG = "GoogleApiHelper";
    private Context context;
    private GoogleApiClient mGoogleApiClient;

    private ConnectionListener mConnectionListener;

    public static final int RC_SIGN_IN = 9001;



    public GoogleApiHelper(Context context) {
        this.context = context;
        buildGoogleApiClient();
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                // add others APIs and scopes here as needed
                .build();
    }

    public void connect() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void disconnect() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public GoogleApiClient getGoogleApiClient() {
        Log.d(TAG, "getGoogleApiClient: " + mGoogleApiClient.isConnected());
        return mGoogleApiClient;
    }

    public boolean isConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mConnectionListener != null) {
            mConnectionListener.onConnected(bundle);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (mConnectionListener != null) {
            mConnectionListener.onConnectionFailed(connectionResult);
        }
    }

    public void setConnectionListener (ConnectionListener connectionListener) {
        this.mConnectionListener = connectionListener;
    }

    public boolean isSetConnectionListener () {
        return mConnectionListener != null;
    }

    public interface ConnectionListener extends View.OnClickListener {
        void onConnected(Bundle bundle);
        void onConnectionFailed(ConnectionResult connectionResult);
    }
}

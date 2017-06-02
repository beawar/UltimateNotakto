package com.example.misterweeman.ultimatenotakto;

import android.app.Application;

public class App extends Application {
    private GoogleApiHelper mGoogleApiHelper;
    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        mGoogleApiHelper = new GoogleApiHelper(mInstance);
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public GoogleApiHelper getGoogleApiHelperInstace() {
        return mGoogleApiHelper;
    }

    public static GoogleApiHelper getGoogleApiHelper() {
        return getInstance().getGoogleApiHelperInstace();
    }
}

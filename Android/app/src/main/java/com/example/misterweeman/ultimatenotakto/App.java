package com.example.misterweeman.ultimatenotakto;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

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

    public static void setLayout(Activity activity, int layout) {
        LinearLayout container = (LinearLayout) activity.findViewById(R.id.layout_container);
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        container.addView(layoutInflater.inflate(layout, container, false));
    }

}

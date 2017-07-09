package com.example.misterweeman.ultimatenotakto.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

public class Utility {

    private static Locale myLocale;
    private static final String PREFS_NAME = "MySettingsFile";
    public static void loadLocale(Context context) {

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        String lang = settings.getString("lang", "en");
        myLocale = new Locale(lang);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }



}

package com.example.robmillaci.nytnews.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

//Helper class to store and retrieve shared preference data.
public class SharedPreferencesHelper {
    private final Context mContext;
    private final int mode;
    private final SharedPreferences.Editor sharedEditor;

    @SuppressLint("CommitPrefEdits")
    public SharedPreferencesHelper(Context context, String prefsName, int mode) {
        mContext = context;
        this.mode = mode;
        this.sharedEditor = context.getSharedPreferences(prefsName, mode).edit();
    }

    public void stringToSharedPreferences(String storeName, String s) {
        sharedEditor.putString(storeName, s).commit();
    }

    public void intToSharedPreferences(String storeName, int s) {
        sharedEditor.putInt(storeName, s).commit();
    }

    public void booleanToSharedPreferences(String storeName, boolean s) {
        sharedEditor.putBoolean(storeName, s).commit();
    }

    public boolean getboolean(String prefsName, String storedName, boolean defaultVal) {
        SharedPreferences prefs = mContext.getSharedPreferences(prefsName, mode);
        return prefs.getBoolean(storedName, defaultVal);
    }


    public String getString(String prefsName, String storedName, String defaultVal) {
        SharedPreferences prefs = mContext.getSharedPreferences(prefsName, mode);
        return prefs.getString(storedName, defaultVal);
    }
}

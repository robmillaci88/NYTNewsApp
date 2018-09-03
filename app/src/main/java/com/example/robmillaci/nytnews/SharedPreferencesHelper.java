package com.example.robmillaci.nytnews;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesHelper {
    Context mContext;
    String prefsName;
    int mode;
    SharedPreferences.Editor sharedEditor;
    private static final String TAG = "SharedPreferencesHelper";

    public SharedPreferencesHelper(Context context, String prefsName, int mode) {
        mContext = context;
        this.prefsName = prefsName;
        this.mode = mode;
        this.sharedEditor = context.getSharedPreferences(prefsName,mode).edit();
    }

    public void stringToSharedPreferences(String storeName,String s){
        sharedEditor.putString(storeName,s).commit();
    }

    public void intToSharedPreferences(String storeName,int s){
        sharedEditor.putInt(storeName,s).commit();
        Log.d(TAG, "intToSharedPreferences: the stored tab is " + s );
    }

    public void booleanToSharedPreferences(String storeName,boolean s){
        sharedEditor.putBoolean(storeName,s).commit();
    }

    public boolean getboolean(String prefsName,String storedName, boolean defaultVal){
        SharedPreferences prefs = mContext.getSharedPreferences(prefsName, mode);
       return prefs.getBoolean(storedName,defaultVal);
    }


    public String getString(String prefsName,String storedName, String defaultVal){
        SharedPreferences prefs = mContext.getSharedPreferences(prefsName, mode);
        return prefs.getString(storedName,defaultVal);
    }


    public int getInt(String prefsName, String storedName, int defaultVal){
        SharedPreferences prefs = mContext.getSharedPreferences(prefsName, mode);
        Log.d(TAG, "getInt: The restored int is " + prefs.getInt(storedName,defaultVal));
        return prefs.getInt(storedName ,defaultVal);
    }


}

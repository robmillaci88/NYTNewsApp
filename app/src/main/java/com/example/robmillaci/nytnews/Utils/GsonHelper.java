package com.example.robmillaci.nytnews.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class GsonHelper {
//helper class to store and retrieve arrays as Json

    public static void storeMyArray(Context mContext, String arrayName, ArrayList<?> arrayList ){
        SharedPreferences.Editor sharedEditor = mContext.getSharedPreferences("myPrefs", MODE_PRIVATE).edit();

        Gson gson = new Gson();
        String  jSon = gson.toJson(arrayList);
        sharedEditor.putString(arrayName, jSon);
        sharedEditor.commit();
    }

    public static String stringMyArrayList(ArrayList arrayList){
        Gson gson = new Gson();
        String jSon = gson.toJson(arrayList);
        return  jSon;
    }

    public static ArrayList arrayMyString(String s){
        Gson gson = new Gson();
        Type type = new TypeToken<List<?>>() {
        }.getType();

        return gson.fromJson(s, type);
    }

    public static ArrayList getMyArray (Context mContext, String sharePreferenceName) throws Exception{
        Gson gson = new Gson();

        SharedPreferences prefs = mContext.getSharedPreferences("myPrefs", MODE_PRIVATE);

        String jSon = prefs.getString(sharePreferenceName, ""); //String to hold the retrieved JSon data

        if (jSon.equals("")){
            throw new Exception("Retrieved JSon string is empty, did you get the preference name correct?");
        } else {
            Type type = new TypeToken<List<?>>() {
            }.getType();
            return gson.fromJson(jSon, type);
        }
    }
}

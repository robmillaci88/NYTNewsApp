package com.example.robmillaci.nytnews.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.CheckBox;

import com.example.robmillaci.nytnews.Activities.SettingsActivity;
import com.example.robmillaci.nytnews.Data.MostPopulareNewsAysnchTask;

import java.util.ArrayList;

public class ScheduleBroadcastReciever extends BroadcastReceiver implements MostPopulareNewsAysnchTask.DownloadMostPopularDataCallback {
    ArrayList foodData;
    ArrayList moviesData;
    ArrayList scienceData;
    ArrayList sportsData;
    ArrayList entrepreneursData;
    ArrayList travelData;
    ArrayList<CheckBox> settingsCheckBoxes;
    static boolean notifcationHasBeenSent;
    Context mContext;
    SharedPreferencesHelper mSharedPreferencesHelper;
    final String BASE_URL = Constants.ARTICLE_SEARCH_URL + Constants.API_KEY + Constants.ARTICLE_SEARCH_PARAMS;


    @Override
    public void onReceive(Context context, Intent intent) {
        notifcationHasBeenSent = false;
        mContext = context;
        mSharedPreferencesHelper = new SharedPreferencesHelper(mContext,"myPrefs",0);
        Bundle intentExtras = intent.getExtras();

        try {
            foodData = GsonHelper.getMyArray(mContext, "foodData");
            moviesData = GsonHelper.getMyArray(mContext, "scienceData");
            scienceData = GsonHelper.getMyArray(mContext, "sportsData");
            sportsData = GsonHelper.getMyArray(mContext, "entrepreneursData");
            entrepreneursData = GsonHelper.getMyArray(mContext, "travelData");
            travelData = GsonHelper.getMyArray(mContext, "moviesData");
        } catch (Exception e) {
            e.printStackTrace();
        }

        settingsCheckBoxes = SettingsActivity.settingsCheckBoxes;

        for (CheckBox c : settingsCheckBoxes) {
            if (c.isChecked() && !notifcationHasBeenSent) {
                switch (c.getTag().toString()) {
                    case "food":
                        MostPopulareNewsAysnchTask downloadMostPopularDataFood = new MostPopulareNewsAysnchTask(this, "food");
                        assert intentExtras != null;
                        downloadMostPopularDataFood.execute(BASE_URL + intentExtras.getString("searchTerm") + "fq=news_desk:(\"Food\")");
                        break;

                    case "science":
                        MostPopulareNewsAysnchTask downloadMostPopularDataScience = new MostPopulareNewsAysnchTask(this, "science");
                        assert intentExtras != null;
                        downloadMostPopularDataScience.execute(BASE_URL + intentExtras.getString("searchTerm") + "fq=news_desk:(\"Science\")");
                        break;

                    case "entre":
                        MostPopulareNewsAysnchTask downloadMostPopularDataEntre = new MostPopulareNewsAysnchTask(this, "entrepreneur");
                        assert intentExtras != null;
                        downloadMostPopularDataEntre.execute(BASE_URL + intentExtras.getString("searchTerm") + "fq=news_desk:(\"Entrepreneur\")");
                        break;

                    case "movies":
                        MostPopulareNewsAysnchTask downloadMostPopularDataMovie = new MostPopulareNewsAysnchTask(this, "movies");
                        assert intentExtras != null;
                        downloadMostPopularDataMovie.execute(BASE_URL + intentExtras.getString("searchTerm") + "fq=news_desk:(\"Movies\")");
                        break;

                    case "sport":
                        MostPopulareNewsAysnchTask downloadMostPopularDataSport = new MostPopulareNewsAysnchTask(this, "sport");
                        assert intentExtras != null;
                        downloadMostPopularDataSport.execute(BASE_URL + intentExtras.getString("searchTerm") + "fq=news_desk:(\"Sport\")");

                        break;

                    case "travel":
                        MostPopulareNewsAysnchTask downloadMostPopularDataTravel = new MostPopulareNewsAysnchTask(this, "travel");
                        assert intentExtras != null;
                        downloadMostPopularDataTravel.execute(BASE_URL + intentExtras.getString("searchTerm") + "fq=news_desk:(\"Travel\")");
                        break;
                }
            }
        }

    }

    @Override
    public void popularDataDownloadFinished(ArrayList data, String category) {
        boolean dataDownloaded = false;
        if (!notifcationHasBeenSent) {
            switch (category) {
                case "food":
                    Log.d("alarm", "popularDataDownloadFinished: reached food switch");
                    if (data != foodData) {
                        //fire notification - data has changed
                        tryToFireNotification();
                        foodData = data;
                        Log.d("data", "popularDataDownloadFinished: data " + foodData);
                        dataDownloaded = true;
                    }

                case "movies":
                    if (data != moviesData) {
                        //fire notification - data has changed
                        tryToFireNotification();
                        moviesData = data;
                        dataDownloaded = true;
                    }

                case "science":
                    if (data != scienceData) {
                        //fire notification - data has changed
                        tryToFireNotification();
                        scienceData = data;
                        dataDownloaded = true;
                    }

                case "entrepreneur":
                    if (data != entrepreneursData) {
                        //fire notification - data has changed
                        tryToFireNotification();
                        entrepreneursData = data;
                        dataDownloaded = true;
                    }
                case "sport":
                    if (data != sportsData) {
                        //fire notification - data has changed
                        tryToFireNotification();
                        sportsData = data;
                        dataDownloaded = true;
                    }
                case "travel":
                    if (data != travelData) {
                        //fire notification - data has changed
                        tryToFireNotification();
                        travelData = data;
                        dataDownloaded = true;
                    }
            }
            saveDataToPreferences(category, dataDownloaded);
        }


    }

    private void saveDataToPreferences(String category, boolean dataDownloaded) {
        if (dataDownloaded) {
            switch (category) {
                case "food":
                    mSharedPreferencesHelper.stringToSharedPreferences("foodData", GsonHelper.stringMyArrayList(foodData));
                    break;

                case "movies":
                    mSharedPreferencesHelper.stringToSharedPreferences("scienceData", GsonHelper.stringMyArrayList(scienceData));
                    break;

                case "science":
                    mSharedPreferencesHelper.stringToSharedPreferences("sportsData", GsonHelper.stringMyArrayList(sportsData));
                    break;

                case "entrepreneur":
                    mSharedPreferencesHelper.stringToSharedPreferences("entrepreneursData", GsonHelper.stringMyArrayList(entrepreneursData));
                    break;

                case "sport":
                    mSharedPreferencesHelper.stringToSharedPreferences("travelData", GsonHelper.stringMyArrayList(travelData));
                    break;

                case "travel":
                    mSharedPreferencesHelper.stringToSharedPreferences("moviesData", GsonHelper.stringMyArrayList(moviesData));
                    break;
            }
        }

    }

    @Override
    public void progressUpdateCallback(Integer... values) {

    }

    public void tryToFireNotification() {
        if (SettingsActivity.mBuilder != null) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(1, SettingsActivity.mBuilder.build());
            notifcationHasBeenSent = true;
        }
    }

    public static void setNotifcationHasBeenSent(boolean hasnotificationBeenSent) {
        notifcationHasBeenSent = hasnotificationBeenSent;
    }
}

package com.example.robmillaci.nytnews;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.CheckBox;

import java.util.ArrayList;

public class Schedule extends BroadcastReceiver implements DownloadMostPopularData.DownloadMostPopularDataCallback {
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
    final String BASE_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=166a1190cb80486a87ead710d48139ae&hl=true&q=";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NOTIFICATIONSCHEDULE", "onReceive: schedule called");
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

        settingsCheckBoxes = Settings.settingsCheckBoxes;

        for (CheckBox c : settingsCheckBoxes) {
            if (c.isChecked() && !notifcationHasBeenSent) {
                switch (c.getTag().toString()) {
                    case "food":
                        DownloadMostPopularData downloadMostPopularDataFood = new DownloadMostPopularData(this, "food");
                        downloadMostPopularDataFood.execute(BASE_URL + intentExtras.getString("searchTerm") + "fq=news_desk:(\"Food\")");
                        break;

                    case "science":
                        DownloadMostPopularData downloadMostPopularDataScience = new DownloadMostPopularData(this, "science");
                        downloadMostPopularDataScience.execute(BASE_URL + intentExtras.getString("searchTerm") + "fq=news_desk:(\"Science\")");
                        break;

                    case "entre":
                        DownloadMostPopularData downloadMostPopularDataEntre = new DownloadMostPopularData(this, "entrepreneur");
                        downloadMostPopularDataEntre.execute(BASE_URL + intentExtras.getString("searchTerm") + "fq=news_desk:(\"Entrepreneur\")");
                        break;

                    case "movies":
                        DownloadMostPopularData downloadMostPopularDataMovie = new DownloadMostPopularData(this, "movies");
                        downloadMostPopularDataMovie.execute(BASE_URL + intentExtras.getString("searchTerm") + "fq=news_desk:(\"Movies\")");
                        break;

                    case "sport":
                        DownloadMostPopularData downloadMostPopularDataSport = new DownloadMostPopularData(this, "sport");
                        downloadMostPopularDataSport.execute(BASE_URL + intentExtras.getString("searchTerm") + "fq=news_desk:(\"Sport\")");

                        break;

                    case "travel":
                        DownloadMostPopularData downloadMostPopularDataTravel = new DownloadMostPopularData(this, "travel");
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
        if (Settings.mBuilder != null) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(1, Settings.mBuilder.build());
            notifcationHasBeenSent = true;
        }
    }

    public static void setNotifcationHasBeenSent(boolean hasnotificationBeenSent) {
        notifcationHasBeenSent = hasnotificationBeenSent;
    }
}

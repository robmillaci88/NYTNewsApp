package com.example.robmillaci.nytnews.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.CheckBox;

import com.example.robmillaci.nytnews.Activities.SettingsActivity;
import com.example.robmillaci.nytnews.Data.MostPopularNewsAysnchTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ScheduleBroadcastReciever extends BroadcastReceiver implements MostPopularNewsAysnchTask.DownloadMostPopularDataCallback {
    ArrayList foodData;
    ArrayList moviesData;
    ArrayList scienceData;
    ArrayList sportsData;
    ArrayList entrepreneursData;
    ArrayList travelData;
    ArrayList<CheckBox> settingsCheckBoxes;
    private boolean notifcationHasBeenSent;
    Context mContext;
    SharedPreferencesHelper mSharedPreferencesHelper;
    final String BASE_URL = Constants.ARTICLE_SEARCH_URL + Constants.ARTICLE_SEARCH_PARAMS;


    @Override
    public void onReceive(Context context, Intent intent) {
        notifcationHasBeenSent = false;
        mContext = context;
        mSharedPreferencesHelper = new SharedPreferencesHelper(mContext, "myPrefs", 0);
        Bundle intentExtras = intent.getExtras();

        try {
            foodData = GsonHelper.getMyArray(mContext, "foodData");
            Log.d("restoredata", "onReceive: got food data");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Log.d("restoredata", "onReceive: got scienceData");
            moviesData = GsonHelper.getMyArray(mContext, "scienceData");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            scienceData = GsonHelper.getMyArray(mContext, "sportsData");
            Log.d("restoredata", "onReceive: got sportsData");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            sportsData = GsonHelper.getMyArray(mContext, "entrepreneursData");
            Log.d("restoredata", "onReceive: got entrepreneursData");

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            entrepreneursData = GsonHelper.getMyArray(mContext, "travelData");
            Log.d("restoredata", "onReceive: got travelData");

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            travelData = GsonHelper.getMyArray(mContext, "moviesData");
            Log.d("restoredata", "onReceive: got moviesData");

        } catch (Exception e) {
            e.printStackTrace();
        }

        settingsCheckBoxes = SettingsActivity.settingsCheckBoxes;

        for (CheckBox c : settingsCheckBoxes) {
            if (c.isChecked() && !notifcationHasBeenSent) {
                switch (c.getTag().toString()) {
                    case "food":
                        MostPopularNewsAysnchTask downloadMostPopularDataFood = new MostPopularNewsAysnchTask(this, "food");
                        assert intentExtras != null;
                        downloadMostPopularDataFood.execute(BASE_URL + intentExtras.getString("searchTerm") + "fq=news_desk:(\"Food\")");
                        break;

                    case "science":
                        MostPopularNewsAysnchTask downloadMostPopularDataScience = new MostPopularNewsAysnchTask(this, "science");
                        assert intentExtras != null;
                        downloadMostPopularDataScience.execute(BASE_URL + intentExtras.getString("searchTerm") + "fq=news_desk:(\"Science\")");
                        break;

                    case "entre":
                        MostPopularNewsAysnchTask downloadMostPopularDataEntre = new MostPopularNewsAysnchTask(this, "entrepreneur");
                        assert intentExtras != null;
                        downloadMostPopularDataEntre.execute(BASE_URL + intentExtras.getString("searchTerm") + "fq=news_desk:(\"Entrepreneur\")");
                        break;

                    case "movies":
                        MostPopularNewsAysnchTask downloadMostPopularDataMovie = new MostPopularNewsAysnchTask(this, "movies");
                        assert intentExtras != null;
                        downloadMostPopularDataMovie.execute(BASE_URL + intentExtras.getString("searchTerm") + "fq=news_desk:(\"Movies\")");
                        break;

                    case "sport":
                        MostPopularNewsAysnchTask downloadMostPopularDataSport = new MostPopularNewsAysnchTask(this, "sport");
                        assert intentExtras != null;
                        downloadMostPopularDataSport.execute(BASE_URL + intentExtras.getString("searchTerm") + "fq=news_desk:(\"Sport\")");

                        break;

                    case "travel":
                        MostPopularNewsAysnchTask downloadMostPopularDataTravel = new MostPopularNewsAysnchTask(this, "travel");
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
                    if (!listEqualsIgnoreOrder(data, foodData)) {
                        //fire notification - data has changed
                        tryToFireNotification();
                        foodData = data;
                        dataDownloaded = true;
                    }
                    break;
                case "movies":
                    if (!listEqualsIgnoreOrder(data, moviesData)) {
                        //fire notification - data has changed
                        tryToFireNotification();
                        moviesData = data;
                        dataDownloaded = true;
                    }
                    break;

                case "science":
                    if (!listEqualsIgnoreOrder(data, scienceData)) {
                        //fire notification - data has changed
                        tryToFireNotification();
                        scienceData = data;
                        dataDownloaded = true;
                    }
                    break;

                case "entrepreneur":
                    if (!listEqualsIgnoreOrder(data, entrepreneursData)) {
                        //fire notification - data has changed
                        tryToFireNotification();
                        entrepreneursData = data;
                        dataDownloaded = true;
                    }
                    break;

                case "sport":
                    if (!listEqualsIgnoreOrder(data, sportsData)) {
                        //fire notification - data has changed
                        tryToFireNotification();
                        sportsData = data;
                        dataDownloaded = true;
                    }
                    break;

                case "travel":
                    if (!listEqualsIgnoreOrder(data, travelData)) {
                        //fire notification - data has changed
                        tryToFireNotification();
                        travelData = data;
                        dataDownloaded = true;
                    }
                    break;
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
        return (list1 != null && list2 != null) && new HashSet<>(list1).equals(new HashSet<>(list2));
    }
}

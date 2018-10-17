package com.example.robmillaci.nytnews.Activities;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.robmillaci.nytnews.Adaptors.MostPopularAdaptor;
import com.example.robmillaci.nytnews.Adaptors.RecyclerViewAdaptor;
import com.example.robmillaci.nytnews.Data.MostPopulareNewsAysnchTask;
import com.example.robmillaci.nytnews.Data.NewsListAsynchTask;
import com.example.robmillaci.nytnews.R;
import com.example.robmillaci.nytnews.Utils.Constants;
import com.example.robmillaci.nytnews.Utils.GsonHelper;
import com.example.robmillaci.nytnews.Utils.SharedPreferencesHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NewsListAsynchTask.DownloadDataCallback, MostPopulareNewsAysnchTask.DownloadMostPopularDataCallback {
    RecyclerView newsItemsRecyclerView;
    ArrayList data;
    TabLayout mTabLayout;
    TabLayout popularTabs;
    String category;
    int selectedTab;
    SharedPreferencesHelper prefsHelper;
    ProgressBar loadProgressBar;

    private static final String CHANNEL_ID = "NYTNotification";
    private final String API_KEY = Constants.API_KEY;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //noinspection ConstantConditions
        getSupportActionBar().setElevation(0);
        createNotificationChannel();
        prefsHelper = new SharedPreferencesHelper(this, "myPrefs", MODE_PRIVATE);

        //restore settings and notification when app is reCreated
        Intent i = new Intent(this, SettingsActivity.class);
        i.putExtra("display", false);
        startActivity(i);

        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        if (prefs != null) {
            try {
                RecyclerViewAdaptor.articlesReadArray = GsonHelper.getMyArray(this, "RecyclerViewReadArray");
                MostPopularAdaptor.articlesReadArray = GsonHelper.getMyArray(this, "PopularRecycleViewReadArray");
                selectedTab = prefsHelper.getInt("myPrefs", "selectedTab", 0);
            } catch (Exception e) {
                e.getMessage();
                e.printStackTrace();
            }
        }

        newsItemsRecyclerView = findViewById(R.id.recyclerview);
        newsItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mTabLayout = findViewById(R.id.tabLayout);
        popularTabs = findViewById(R.id.popularTabs);
        loadProgressBar = findViewById(R.id.progressBar);
        mTabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                selectedTab = tab.getPosition();
                switch (selectedTab) {
                    case 0:
                        //selected top stories
                        popularTabs.setVisibility(View.GONE);
                        new NewsListAsynchTask(MainActivity.this).execute(Constants.POPULAR_NEWS_URL + API_KEY);
                        break;

                    case 1:
                        //selected most popular
                        popularTabs.setVisibility(View.VISIBLE);
                        getData(Constants.MOST_POPULAR_FOOD_URL + API_KEY);
                        break;

                    case 2:
                        //selected business
                        popularTabs.setVisibility(View.GONE);
                        getData(Constants.MOST_POPULAR_BUSINESS_URL + API_KEY);
                        break;
                }
            }

            @Override
            public void onTabUnselected(Tab tab) {

            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });


        switch (selectedTab) {
            case 0:
                Log.d("downloadFinished", "onTabSelected: case 0 called");
                mTabLayout.getTabAt(0).select();
                new NewsListAsynchTask(MainActivity.this).execute("http://api.nytimes.com/svc/topstories/v2/home.json?api-key=166a1190cb80486a87ead710d48139ae");
                break;

            case 1:
                mTabLayout.getTabAt(1).select();
                //          getData("https://api.nytimes.com/svc/mostpopular/v2/mostviewed/Food/30.json?api-key=166a1190cb80486a87ead710d48139ae");
                break;

            case 2:
                mTabLayout.getTabAt(2).select();
//                getData("https://api.nytimes.com/svc/mostpopular/v2/mostviewed/Business%20Day/30.json?api-key=166a1190cb80486a87ead710d48139ae");
                break;
        }


        popularTabs.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                switch (tab.getPosition()) {

                    case 0:
                        getData("https://api.nytimes.com/svc/mostpopular/v2/mostviewed/Food/30.json?api-key=166a1190cb80486a87ead710d48139ae");
                        category = "food";
                        break;

                    case 1:
                        getData("https://api.nytimes.com/svc/mostpopular/v2/mostviewed/Movies/30.json?api-key=166a1190cb80486a87ead710d48139ae");
                        category = "movies";
                        break;

                    case 2:
                        getData("https://api.nytimes.com/svc/mostpopular/v2/mostviewed/Science/30.json?api-key=166a1190cb80486a87ead710d48139ae");
                        category = "science";
                        break;
                }

            }

            @Override
            public void onTabUnselected(Tab tab) {

            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });
    }

    @Override
    public void downloadFinished(ArrayList downloadData) {
        Log.d("downloadFinished", "downloadFinished: reached here");
        data = downloadData;
        newsItemsRecyclerView.setAdapter(new RecyclerViewAdaptor(data, getApplicationContext()));
        loadProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void popularDataDownloadFinished(ArrayList downloadData, String category) {
        data = downloadData;
        newsItemsRecyclerView.setAdapter(new MostPopularAdaptor(data, getApplicationContext()));
        loadProgressBar.setVisibility(View.GONE);

    }

    @Override
    public void progressUpdateCallback(Integer... values) {
        loadProgressBar.setVisibility(View.VISIBLE);
        loadProgressBar.setMax(values[1]);
        loadProgressBar.setProgress(values[0] + 1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                startActivity(new Intent(this, SearchActivity.class));
                break;

            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }

        return true;
    }

    public void getData(String url) {
        try {
            new MostPopulareNewsAysnchTask(this, category).execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @SuppressLint("ApplySharedPref")
    public void saveState() {
        GsonHelper.storeMyArray(this, "RecyclerViewReadArray", RecyclerViewAdaptor.articlesReadArray);
        GsonHelper.storeMyArray(this, "PopularRecycleViewReadArray", MostPopularAdaptor.articlesReadArray);
        prefsHelper.intToSharedPreferences("selectedTab", selectedTab);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //check the settings and ensure alarms are set

    }

    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system;
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            if (notificationManager != null) notificationManager.createNotificationChannel(channel);
        }
    }


}

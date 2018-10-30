package com.example.robmillaci.nytnews.Activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.robmillaci.nytnews.Adapters.MostPopularAdapter;
import com.example.robmillaci.nytnews.Adapters.RecyclerViewAdapter;
import com.example.robmillaci.nytnews.Data.MostPopulareNewsAysnchTask;
import com.example.robmillaci.nytnews.Data.NewsListAsynchTask;
import com.example.robmillaci.nytnews.R;
import com.example.robmillaci.nytnews.Utils.Constants;
import com.example.robmillaci.nytnews.Utils.SharedPreferencesHelper;
import com.example.robmillaci.nytnews.Utils.TypeFaceSpan;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NewsListAsynchTask.DownloadDataCallback, MostPopulareNewsAysnchTask.DownloadMostPopularDataCallback {
    private RecyclerView newsItemsRecyclerView; //to display the downloaded news objects
    private ArrayList data; //the data that is downloaded - this is passed into the recyclerview adapter
    private TabLayout popularTabs; //the sub tabs of the 'popular' tab
    private String category; //the download category used in switch statments to determine which data has been downloaded
    private int selectedTab; //the current selected tab
    private SharedPreferencesHelper prefsHelper; //shared preferences used in this activity to keep a record of the selected tab
    private ProgressBar loadProgressBar; //progress bar that is displayed as data is downloaded and removed once the download is complete

    private static final String CHANNEL_ID = "NYTNotification"; //final static string used when creating the notifications

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //noinspection ConstantConditions
        getSupportActionBar().setElevation(0);

        SpannableString s = new SpannableString("NYT News");
        s.setSpan(new TypeFaceSpan(this, "titlefont.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);


        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        createNotificationChannel();


        prefsHelper = new SharedPreferencesHelper(this, "myPrefs", MODE_PRIVATE);

        //restore settings and notification when app is reCreated
        Intent i = new Intent(this, SettingsActivity.class);
        i.putExtra("display", false);
        startActivity(i);

        //define and create the news items recycler view
        newsItemsRecyclerView = findViewById(R.id.recyclerview);
        newsItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //assign the view references and add onTabSelectedListener for the tab layout
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        popularTabs = findViewById(R.id.popularTabs);
        loadProgressBar = findViewById(R.id.progressBar);
        tabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                selectedTab = tab.getPosition();
                switch (selectedTab) {
                    case 0:
                        //selected top stories
                        popularTabs.setVisibility(View.GONE);
                        new NewsListAsynchTask(MainActivity.this).execute(Constants.POPULAR_NEWS_URL);
                        break;

                    case 1:
                        //selected most popular
                        popularTabs.setVisibility(View.VISIBLE);
                        getData(Constants.MOST_POPULAR_FOOD_URL);
                        break;

                    case 2:
                        //selected business
                        popularTabs.setVisibility(View.GONE);
                        getData(Constants.MOST_POPULAR_BUSINESS_URL);
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

        //initially select tab at index 0 (first tab) and download the data for that tab
        tabLayout.getTabAt(0).select();
        new NewsListAsynchTask(MainActivity.this).execute(Constants.POPULAR_NEWS_URL);

        //add on tab selected listener to the popular sub tabs
        popularTabs.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                switch (tab.getPosition()) {

                    case 0:
                        getData(Constants.MOST_POPULAR_FOOD_URL);
                        category = "food";
                        break;

                    case 1:
                        getData(Constants.MOST_POPULAR_MOVIES_URL);
                        category = "movies";
                        break;

                    case 2:
                        getData(Constants.MOST_POPULAR_SCIENCE_URL);
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

    //callback from asynch data download which sets the downloaded data, sets the recyclerview adaptor and removes the progress bar
    @Override
    public void downloadFinished(ArrayList downloadData) {
        data = downloadData;
        newsItemsRecyclerView.setAdapter(new RecyclerViewAdapter(data, getApplicationContext()));
        loadProgressBar.setVisibility(View.GONE);
    }

    //callback from asynch popular data download which sets the downloaded data, sets the recyclerview adaptor and removes the progress bar
    @Override
    public void popularDataDownloadFinished(ArrayList downloadData, String category) {
        data = downloadData;
        newsItemsRecyclerView.setAdapter(new MostPopularAdapter(data, getApplicationContext()));
        loadProgressBar.setVisibility(View.GONE);

    }

    //callback for the progress bar, updating as the asynch task is running
    @Override
    public void progressUpdateCallback(Integer... values) {
        loadProgressBar.setVisibility(View.VISIBLE);
        loadProgressBar.setMax(values[1]);
        loadProgressBar.setProgress(values[0] + 1);

    }

    //creates the options menu for the app
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    //defines what happens when an options menu item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                //starts the Search activity
                startActivity(new Intent(this, SearchActivity.class));
                break;

            case R.id.settings:
                //starts the settings activity
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }

        return true;
    }


    //method called with a URL to download 'most popular data'
    public void getData(String url) {
        try {
            new MostPopulareNewsAysnchTask(this, category).execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //save the state when the onPause lifecycle event is called
    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    //stores the selected tab into shared preferences. This method is called from onPause
    @SuppressLint("ApplySharedPref")
    public void saveState() {
        prefsHelper.intToSharedPreferences("selectedTab", selectedTab);
    }


    //Method to create the notification channel. This is only used on API 26 +
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

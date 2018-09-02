package com.example.robmillaci.nytnews;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DownloadData.DownloadDataCallback, DownloadMostPopularData.DownloadMostPopularDataCallback {
    RecyclerView newsItemsRecyclerView;
    ArrayList data;
    TabLayout mTabLayout;
    TabLayout popularTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);


        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        if (prefs != null) {
            Gson gson = new Gson();
            String recyclerViewReadArray = prefs.getString("RecyclerViewReadArray", null); //String to hold the retrieved JSon data
            String popularRecycleViewReadArray = prefs.getString("PopularRecycleViewReadArray", null);
            Type type = new TypeToken<List<?>>() {
            }.getType();
            RecyclerViewAdaptor.articlesReadArray = gson.fromJson(recyclerViewReadArray, type);
            MostPopularAdaptor.articlesReadArray = gson.fromJson(popularRecycleViewReadArray, type);
        }

        newsItemsRecyclerView = findViewById(R.id.recyclerview);
        newsItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mTabLayout = findViewById(R.id.tabLayout);
        popularTabs = findViewById(R.id.popularTabs);
        try {
            data = new DownloadData(MainActivity.this).execute("http://api.nytimes.com/svc/topstories/v2/home.json?api-key=166a1190cb80486a87ead710d48139ae").get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        newsItemsRecyclerView.setAdapter(new RecyclerViewAdaptor(data, getApplicationContext()));

        mTabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        //selected top stories
                        popularTabs.setVisibility(View.GONE);
                        new DownloadData(MainActivity.this).execute("http://api.nytimes.com/svc/topstories/v2/home.json?api-key=166a1190cb80486a87ead710d48139ae");
                        break;

                    case 1:
                        //selected most popular
                        popularTabs.setVisibility(View.VISIBLE);
                        getData("https://api.nytimes.com/svc/mostpopular/v2/mostviewed/Food/30.json?api-key=166a1190cb80486a87ead710d48139ae");
                        break;

                    case 2:
                        //selected business
                        popularTabs.setVisibility(View.GONE);
                        getData("https://api.nytimes.com/svc/mostpopular/v2/mostviewed/Business%20Day/30.json?api-key=166a1190cb80486a87ead710d48139ae");
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

        popularTabs.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                switch (tab.getPosition()) {

                    case 0:
                        getData("https://api.nytimes.com/svc/mostpopular/v2/mostviewed/Food/30.json?api-key=166a1190cb80486a87ead710d48139ae");
                        break;

                    case 1:
                        getData("https://api.nytimes.com/svc/mostpopular/v2/mostviewed/Movies/30.json?api-key=166a1190cb80486a87ead710d48139ae");
                        break;

                    case 2:
                        getData("https://api.nytimes.com/svc/mostpopular/v2/mostviewed/Science/30.json?api-key=166a1190cb80486a87ead710d48139ae");
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
        data = downloadData;
        newsItemsRecyclerView.setAdapter(new RecyclerViewAdaptor(data, getApplicationContext()));
    }

    @Override
    public void popularDataDownloadFinished(ArrayList downloadData) {
        data = downloadData;
        newsItemsRecyclerView.setAdapter(new MostPopularAdaptor(data, getApplicationContext()));

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
                startActivity(new Intent(this, Search.class));
                break;
        }

        return true;
    }

    public void getData(String url) {
        try {
           new DownloadMostPopularData(this).execute(url);
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
        SharedPreferences.Editor sharedEditor = getSharedPreferences("myPrefs", MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String recyclerViewReadArray = gson.toJson(RecyclerViewAdaptor.articlesReadArray);
        String mostPopularReadArray = gson.toJson(MostPopularAdaptor.articlesReadArray);
        sharedEditor.putString("RecyclerViewReadArray", recyclerViewReadArray);
        sharedEditor.putString("PopularRecycleViewReadArray", mostPopularReadArray);
        sharedEditor.commit();
    }
}

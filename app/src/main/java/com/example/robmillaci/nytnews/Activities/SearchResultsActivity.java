package com.example.robmillaci.nytnews.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import com.example.robmillaci.nytnews.Adaptors.SearchAdaptor;
import com.example.robmillaci.nytnews.Models.SearchNewsObjectModel;
import com.example.robmillaci.nytnews.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    RecyclerView searchResultsRecyclerView;
    ArrayList<SearchNewsObjectModel> data;
    ImageView noResultsImage;
    ConstraintLayout mLayout;
    SearchAdaptor mAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchresults);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //adds a home button to the support action bar
        setTitle("SearchActivity Results");

        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        if (prefs != null) {
            Gson gson = new Gson();
            String searchReadArray = prefs.getString("searchReadArray", null);
            Type type = new TypeToken<List<?>>() {
            }.getType();
            SearchAdaptor.articlesReadArray = gson.fromJson(searchReadArray, type);
        }

        noResultsImage = findViewById(R.id.noResultsImage);
        mLayout = findViewById(R.id.constraintLayout);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        Intent searchIntent = getIntent();
        String dataString = searchIntent.getStringExtra("dataArray");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<SearchNewsObjectModel>>() {
        }.getType();

        data = gson.fromJson(dataString, type);

        if (data.size() == 0) {
            //no data found
            mLayout.setBackgroundColor(Color.WHITE);
            noResultsImage.setVisibility(View.VISIBLE);
        } else {
            mAdaptor = new SearchAdaptor(data, SearchResultsActivity.this);
            searchResultsRecyclerView.setAdapter(mAdaptor);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    public void saveState() {
        SharedPreferences.Editor sharedEditor = getSharedPreferences("myPrefs", MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String searchReadArray = gson.toJson(SearchAdaptor.articlesReadArray);
        sharedEditor.putString("searchReadArray", searchReadArray);
        sharedEditor.apply();
    }
}
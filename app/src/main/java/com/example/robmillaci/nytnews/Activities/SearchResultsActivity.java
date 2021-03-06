package com.example.robmillaci.nytnews.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.robmillaci.nytnews.Adapters.SearchAdapter;
import com.example.robmillaci.nytnews.Models.SearchNewsObjectModel;
import com.example.robmillaci.nytnews.R;
import com.example.robmillaci.nytnews.Utils.TypeFaceSpan;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchresults);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //adds a home button to the support action bar

        SpannableString s = new SpannableString("Search Results");
        s.setSpan(new TypeFaceSpan(this, "titlefont.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);

        //Restores any previously read articles into the articlesReadArray from shared preferences
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        if (prefs != null) {
            Gson gson = new Gson();
            String searchReadArray = prefs.getString("searchReadArray", null);
            Type type = new TypeToken<List<?>>() {
            }.getType();
            SearchAdapter.articlesReadArray = gson.fromJson(searchReadArray, type);
        }


        //assigns the view references to their IDs
        ImageView noResultsImage = findViewById(R.id.noResultsImage);
        ConstraintLayout layout = findViewById(R.id.constraintLayout);
        RecyclerView searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //retrieves the downloaded data that was passed in the intent to create this activities (the downloaded search data)
        Intent searchIntent = getIntent();
        String dataString = searchIntent.getStringExtra("dataArray");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<SearchNewsObjectModel>>() {
        }.getType();

        ArrayList<SearchNewsObjectModel> data = gson.fromJson(dataString, type);

        //if not data is found, the background is set to white and the 'no results image' is displayed, else
        //the recyclerview adaptor is created passing the downloaded data
        if (data.size() == 0) {
            //no data found
            layout.setBackgroundColor(Color.WHITE);
            noResultsImage.setVisibility(View.VISIBLE);
        } else {
            SearchAdapter adapter = new SearchAdapter(data, SearchResultsActivity.this);
            searchResultsRecyclerView.setAdapter(adapter);
        }
    }

    //defines what happens when the user presses the back button - navigates back to main activity
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

    //when the onPause lifecycle event is called, the saveState method is called saving the read articles
    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    private void saveState() {
        SharedPreferences.Editor sharedEditor = getSharedPreferences("myPrefs", MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String searchReadArray = gson.toJson(SearchAdapter.articlesReadArray);
        sharedEditor.putString("searchReadArray", searchReadArray);
        sharedEditor.apply();
    }
}

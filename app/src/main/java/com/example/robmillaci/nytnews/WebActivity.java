package com.example.robmillaci.nytnews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //adds a home button to the support action bar


        WebView wv = findViewById(R.id.webview);
        Bundle intentBundle = getIntent().getExtras();
        String url = intentBundle.getString("url");
        wv.loadUrl(url);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("back", "onOptionsItemSelected: called");
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }
}

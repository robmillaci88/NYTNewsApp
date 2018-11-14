package com.example.robmillaci.nytnews.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.webkit.WebView;

import com.example.robmillaci.nytnews.R;
import com.example.robmillaci.nytnews.Utils.TypeFaceSpan;

/*
This class simple loads the URL passed in the intent into a webview
 */
public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        SpannableString s = new SpannableString("My News");
        s.setSpan(new TypeFaceSpan(this, "titlefont.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(s);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //adds a home button to the support action bar


        WebView wv = findViewById(R.id.webview);
        Bundle intentBundle = getIntent().getExtras();
        assert intentBundle != null;
        String url = intentBundle.getString("url");
        wv.loadUrl(url);
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
}

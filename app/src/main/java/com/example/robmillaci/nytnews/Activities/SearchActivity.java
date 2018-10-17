package com.example.robmillaci.nytnews.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robmillaci.nytnews.Data.SearchNewsItemsAsynchTask;
import com.example.robmillaci.nytnews.R;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements SearchNewsItemsAsynchTask.downloadcallback {
    TextView fromDate;
    TextView toDate;
    Button searchButton;
    CheckBox artsCheckBox;
    CheckBox businessCheckBox;
    CheckBox entrepreneursCheckBox;
    CheckBox politicsCheckBox;
    CheckBox sportsCheckBox;
    CheckBox travelCheckBox;
    EditText searchTermText;
    Switch sortSwitch;
    ProgressBar mProgressBar;
    ArrayList data;
    ArrayList<CheckBox> searchSubjectsArray;

    ArrayList<CheckBox> checkBoxArray;
    com.takisoft.datetimepicker.DatePickerDialog mDatePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("SearchActivity Articles");

        data = new ArrayList();
        mDatePickerDialog = new com.takisoft.datetimepicker.DatePickerDialog(SearchActivity.this);

        searchButton = findViewById(R.id.searchButton);
        fromDate = findViewById(R.id.fromDate);
        toDate = findViewById(R.id.toDate);
        artsCheckBox = findViewById(R.id.artsCheckBox);
        businessCheckBox = findViewById(R.id.businessCheckBox);
        entrepreneursCheckBox = findViewById(R.id.entrepreneursCheckBox);
        politicsCheckBox = findViewById(R.id.politicsCheckBox);
        sportsCheckBox = findViewById(R.id.sportsCheckBox);
        travelCheckBox = findViewById(R.id.travelCheckBox);
        searchTermText = findViewById(R.id.notificationSearchTerm);
        sortSwitch = findViewById(R.id.sortSwitch);
        mProgressBar = findViewById(R.id.progressBar);

        searchSubjectsArray= new ArrayList<>();
        searchSubjectsArray.add(artsCheckBox);
        searchSubjectsArray.add(businessCheckBox);
        searchSubjectsArray.add(entrepreneursCheckBox);
        searchSubjectsArray.add(politicsCheckBox);
        searchSubjectsArray.add(sportsCheckBox);
        searchSubjectsArray.add(travelCheckBox);

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerDialog.setOnDateSetListener(new com.takisoft.datetimepicker.DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(com.takisoft.datetimepicker.widget.DatePicker view, int year, int month, int dayOfMonth) {
                        fromDate.setText(String.valueOf(dayOfMonth + "/" + (month+1) + "/" + year));
                    }
                });
                mDatePickerDialog.show();
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerDialog.setOnDateSetListener(new com.takisoft.datetimepicker.DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(com.takisoft.datetimepicker.widget.DatePicker view, int year, int month, int dayOfMonth) {
                        toDate.setText(String.valueOf(dayOfMonth + "/" + (month+1) + "/" + year));
                    }
                });
                mDatePickerDialog.show();
            }
        });


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isReadyTosearch()){
                    try {
                        buildSearchUrl();
                        searchButton.setText(R.string.searchBtnSearching);
                        searchButton.setClickable(false);
                        searchButton.setBackgroundResource(R.drawable.search_button_style_clicked);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void buildSearchUrl() throws ParseException {
        StringBuilder url = new StringBuilder();
        String searchURL = "https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=166a1190cb80486a87ead710d48139ae&q=";
        url.append(searchURL);
        url.append(searchTermText.getText());
        url.append("&fq=news_desk:(");

        DateFormat outputFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        String fDate = fromDate.getText().toString();
        String tDate = toDate.getText().toString();


        int checkedCount = 0;
        for (CheckBox c : searchSubjectsArray){
            if (c.isChecked()){
                if (checkedCount>=1){
                    url.append(" ");
                }
                url.append("\"").append(c.getText().toString()).append("\"");
                checkedCount ++;
            }
        }

        url.append(")");

        if (!fDate.equals("")){
            Date date = inputFormat.parse(fDate);
            url.append("&begin_date=").append(outputFormat.format(date));
        }
        if (!tDate.equals("")){
            Date date = inputFormat.parse(tDate);
            url.append("&end_date=").append(outputFormat.format(date));
        }

        Log.d("switch", "buildSearchUrl: " + "sortSwitch val " + sortSwitch.isChecked());
        if (sortSwitch.isChecked()) {
            url.append("&sort=newest");
        } else {
            url.append("&sort=oldest");
        }

        try {
            Log.d("url", "buildSearchUrl: URL IS " + url.toString());
          new SearchNewsItemsAsynchTask(this).execute(url.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isReadyTosearch(){
        boolean readyToSearch = false;

        checkBoxArray = new ArrayList<>();
        checkBoxArray.add(artsCheckBox);
        checkBoxArray.add(businessCheckBox);
        checkBoxArray.add(entrepreneursCheckBox);
        checkBoxArray.add(politicsCheckBox);
        checkBoxArray.add(sportsCheckBox);
        checkBoxArray.add(travelCheckBox);

        String searchText = searchTermText.getText().toString();

        for (CheckBox c : checkBoxArray){
            Log.d("checked", "isReadyTosearch: checkbox" + c.isChecked());
            if (c.isChecked() && !searchText.equals("")){
                readyToSearch = true;
            }
        }

        if (!readyToSearch){
            Toast.makeText(this,"A search term and at least one category must be selected",Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void mcallback(int progress) {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setMax(100);
        mProgressBar.setProgress(progress);

    }

    @Override
    public void downloadFinished(ArrayList resultData) {
        data = resultData;

        searchButton.setText(R.string.SearchBtnSearch);
        searchButton.setClickable(true);
        searchButton.setBackgroundResource(R.drawable.search_button_style);

        Intent searchIntent = new Intent(this,SearchResultsActivity.class);
        Gson gson = new Gson();
        String dataArray =gson.toJson(data);
        searchIntent.putExtra("dataArray",dataArray);

        mProgressBar.setProgress(0);
        mProgressBar.setVisibility(View.INVISIBLE);

        startActivity(searchIntent);
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }



}

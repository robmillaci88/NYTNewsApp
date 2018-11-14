package com.example.robmillaci.nytnews.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
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
import com.example.robmillaci.nytnews.Utils.Constants;
import com.example.robmillaci.nytnews.Utils.TypeFaceSpan;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements SearchNewsItemsAsynchTask.downloadcallback {
    private TextView fromDate; //the from date text view of the search activity
    private TextView toDate; //the to date text view of the search activity
    private Button searchButton; //the search button of the search activity
    private EditText searchTermText; //the search term text view of the search activity
    private Switch sortSwitch; //the switch to determine sorting order
    private ProgressBar mProgressBar; //the progress bar to be displayed as data is being asynchronously downloaded
    private ArrayList data; //array list to hold the downloaded data
    private ArrayList<CheckBox> searchSubjectCheckBoxes; //an arraylist to hold all the search activities check boxes

    private com.takisoft.datetimepicker.DatePickerDialog mDatePickerDialog; //custom date time picker API used for selecting to and from date.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SpannableString s = new SpannableString("Search Articles");
        s.setSpan(new TypeFaceSpan(this, "titlefont.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);

        data = new ArrayList();
        mDatePickerDialog = new com.takisoft.datetimepicker.DatePickerDialog(SearchActivity.this);


        //assign the view references to the ids
        searchButton = findViewById(R.id.searchButton);
        fromDate = findViewById(R.id.fromDate);
        toDate = findViewById(R.id.toDate);
        CheckBox artsCheckBox = findViewById(R.id.artsCheckBox);
        CheckBox businessCheckBox = findViewById(R.id.businessCheckBox);
        CheckBox entrepreneursCheckBox = findViewById(R.id.entrepreneursCheckBox);
        CheckBox politicsCheckBox = findViewById(R.id.politicsCheckBox);
        CheckBox sportsCheckBox = findViewById(R.id.sportsCheckBox);
        CheckBox travelCheckBox = findViewById(R.id.travelCheckBox);
        searchTermText = findViewById(R.id.notificationSearchTerm);
        sortSwitch = findViewById(R.id.sortSwitch);
        mProgressBar = findViewById(R.id.progressBar);

        //create a new array list to store all the check boxes in the Search activity and add all the checkboxes to facilitate easy checking of state
        searchSubjectCheckBoxes = new ArrayList<>();
        searchSubjectCheckBoxes.add(artsCheckBox);
        searchSubjectCheckBoxes.add(businessCheckBox);
        searchSubjectCheckBoxes.add(entrepreneursCheckBox);
        searchSubjectCheckBoxes.add(politicsCheckBox);
        searchSubjectCheckBoxes.add(sportsCheckBox);
        searchSubjectCheckBoxes.add(travelCheckBox);

        //assign an on click listener to the from date
        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerDialog.setOnDateSetListener(new com.takisoft.datetimepicker.DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(com.takisoft.datetimepicker.widget.DatePicker view, int year, int month, int dayOfMonth) {
                        fromDate.setText(String.valueOf(dayOfMonth + "/" + (month + 1) + "/" + year));
                    }
                });
                mDatePickerDialog.show();
            }
        });

        //assign an on click listener to the to date
        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerDialog.setOnDateSetListener(new com.takisoft.datetimepicker.DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(com.takisoft.datetimepicker.widget.DatePicker view, int year, int month, int dayOfMonth) {
                        toDate.setText(String.valueOf(dayOfMonth + "/" + (month + 1) + "/" + year));
                    }
                });
                mDatePickerDialog.show();
            }
        });


        //assign on click listener to the search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReadyTosearch()) {
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

    //this method builds the search URL using the Constants class to retrieve the base URL with API key and appending
    //the to and from date if supplied , and the categories if selected from the checkboxes.
    //also appends to and from date if they are supplied by the user
    private void buildSearchUrl() throws ParseException {
        StringBuilder url = new StringBuilder();
        String searchURL = Constants.ARTICLE_SEARCH_URL;
        url.append(searchURL);
        url.append("&q=");
        url.append(searchTermText.getText());
        url.append("&fq=news_desk:(");

        DateFormat outputFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        String fDate = fromDate.getText().toString();
        String tDate = toDate.getText().toString();


        int checkedCount = 0;
        for (CheckBox c : searchSubjectCheckBoxes) {
            if (c.isChecked()) {
                if (checkedCount >= 1) {
                    url.append(" ");
                }
                url.append("\"").append(c.getText().toString()).append("\"");
                checkedCount++;
            }
        }

        url.append(")");

        if (!fDate.equals("")) {
            Date date = inputFormat.parse(fDate);
            url.append("&begin_date=").append(outputFormat.format(date));
        }
        if (!tDate.equals("")) {
            Date date = inputFormat.parse(tDate);
            url.append("&end_date=").append(outputFormat.format(date));
        }

        if (sortSwitch.isChecked()) {
            url.append("&sort=newest");
        } else {
            url.append("&sort=oldest");
        }

        try {
            new SearchNewsItemsAsynchTask(this).execute(url.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //method that returns whether the application is ready to search or not.
    //if the user doesn't provide a search term and at least one checkbox is ticked, then ready to search is false and a toast message
    //is displayed. Otherwise readyToSearch = true;
    private boolean isReadyTosearch() {
        boolean readyToSearch = false;

        String searchText = searchTermText.getText().toString();

        for (CheckBox c : searchSubjectCheckBoxes) {
            if (c.isChecked() && !searchText.equals("")) {
                readyToSearch = true;
            }
        }

        if (!readyToSearch) {
            Toast.makeText(this, "A search term and at least one category must be selected", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    //progress callback for the searching and downloading of the data used to display the progress bar
    @Override
    public void mcallback(int progress) {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setMax(100);
        mProgressBar.setProgress(progress);

    }

    //call back for the download data being finished.
    //this resets the search button text and enables it to be clickable again
    //It also starts the intent to navigate the users to the search results activity to display the results
    @Override
    public void downloadFinished(ArrayList resultData) {
        data = resultData;

        searchButton.setText(R.string.SearchBtnSearch);
        searchButton.setClickable(true);
        searchButton.setBackgroundResource(R.drawable.search_button_style);

        Intent searchIntent = new Intent(this, SearchResultsActivity.class);
        Gson gson = new Gson();
        String dataArray = gson.toJson(data);
        Log.d("url", "downloadFinished: data array is " + dataArray);
        searchIntent.putExtra("dataArray", dataArray);

        mProgressBar.setProgress(0);
        mProgressBar.setVisibility(View.INVISIBLE);

        startActivity(searchIntent);
    }


}

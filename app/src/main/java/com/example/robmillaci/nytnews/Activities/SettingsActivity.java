package com.example.robmillaci.nytnews.Activities;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robmillaci.nytnews.R;
import com.example.robmillaci.nytnews.Utils.GsonHelper;
import com.example.robmillaci.nytnews.Utils.ScheduleBroadcastReciever;
import com.example.robmillaci.nytnews.Utils.SharedPreferencesHelper;
import com.example.robmillaci.nytnews.Utils.TypeFaceSpan;

import java.util.ArrayList;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {
    public static NotificationCompat.Builder mBuilder; //the builder used to create the notification
    private static final String CHANNEL_ID = "NYTNotification"; //the channel ID users to create the notification
    Switch notificationSwitch; //the switch used to turn then notifications on or off
    public static ArrayList<CheckBox> settingsCheckBoxes; //arraylist that holds all the settings check boxes
    public static AlarmManager alarmManager; //the alarm manager that is used to send a broadcast to the ScheduleBroadcastReciever class
    ArrayList<String> checkedArrayList; //


    //refernce setup to the checkboxes, notification search term textview and shared preferences
    CheckBox foodSettingsCheckBox;
    CheckBox scienceSettingsCheckBox;
    CheckBox entreSettingsCheckBox;
    CheckBox moviesSettingsCheckBox;
    CheckBox sportsSettingsCheckBox;
    CheckBox travelSettingsCheckBox;
    TextView notificationSearchTerm;
    SharedPreferencesHelper sharedPreferencesHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //determines whether we are restoring settings or we want to actually display this activity to the user
        boolean display = getIntent().getBooleanExtra("display", true);
        setContentView(R.layout.activity_settings);

        SpannableString s = new SpannableString("Settings");
        s.setSpan(new TypeFaceSpan(this, "titlefont.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //method called to set up the checkboxes
        checkBoxSetUp();

        //if we intend to not display the setting activity
        if (!display) {
            onBackPressed();
        }
    }


    void checkBoxSetUp() {
        sharedPreferencesHelper = new SharedPreferencesHelper(this, "myPrefs", 0);

        notificationSearchTerm = findViewById(R.id.notificationSearchTerm);

        foodSettingsCheckBox = findViewById(R.id.foodSettingsCheckBox);
        foodSettingsCheckBox.setTag("food");

        scienceSettingsCheckBox = findViewById(R.id.scienceSettingCheckBox);
        scienceSettingsCheckBox.setTag("science");

        entreSettingsCheckBox = findViewById(R.id.entrepreneursSettingCheck);
        entreSettingsCheckBox.setTag("entre");

        moviesSettingsCheckBox = findViewById(R.id.moviesSettingsCheckBox);
        moviesSettingsCheckBox.setTag("movies");

        sportsSettingsCheckBox = findViewById(R.id.sportsSettingCheckBox);
        sportsSettingsCheckBox.setTag("sport");

        travelSettingsCheckBox = findViewById(R.id.travelSettingCheck);
        travelSettingsCheckBox.setTag("travel");

        //add all the checkboxes defined above into an arraylist that is used by this class and the broadcast reciever class
        settingsCheckBoxes = new ArrayList<>();
        settingsCheckBoxes.add(foodSettingsCheckBox);
        settingsCheckBoxes.add(scienceSettingsCheckBox);
        settingsCheckBoxes.add(entreSettingsCheckBox);
        settingsCheckBoxes.add(moviesSettingsCheckBox);
        settingsCheckBoxes.add(sportsSettingsCheckBox);
        settingsCheckBoxes.add(travelSettingsCheckBox);

        notificationSwitch = findViewById(R.id.notificationSwitch);

        //the below logic retrieves any previously saved checkbox state and applies this state to the checkboxes (i.e checked or not)
        try {
            //noinspection unchecked
            checkedArrayList = GsonHelper.getMyArray(this, "checkBoxVals");
            for (String s : checkedArrayList) {
                switch (s) {
                    case "food":
                        foodSettingsCheckBox.setChecked(true);
                        break;

                    case "science":
                        scienceSettingsCheckBox.setChecked(true);
                        break;

                    case "entre":
                        entreSettingsCheckBox.setChecked(true);
                        break;

                    case "movies":
                        moviesSettingsCheckBox.setChecked(true);
                        break;

                    case "sport":
                        sportsSettingsCheckBox.setChecked(true);
                        break;

                    case "travel":
                        travelSettingsCheckBox.setChecked(true);
                        break;
                }
            }
        } catch (Exception e) {
            checkedArrayList = new ArrayList<>();
        }

        //restores any previously set search term text
        notificationSearchTerm.setText(sharedPreferencesHelper.getString("myPrefs", "searchTerm", ""));

        //on change listener that determines if notifications can be sent. If at least one checkbo is selected, readyToGo is set to true and the notification
        //intent can be created.
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    boolean readyToGo = false;
                    for (CheckBox c : settingsCheckBoxes) {
                        if (c.isChecked()) {
                            readyToGo = true;
                        }
                    }

                    if (readyToGo) {
                        //create a pending intent for when the user clicks on the recieved notification to launch the app
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                        //created the notification builder to set the title, text and content intent as well as the icon to display to the user
                        mBuilder = new NotificationCompat.Builder(SettingsActivity.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.read)
                                .setContentTitle("NYT news items")
                                .setContentText("There are new news items you might be interested in")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);

                        //this method creates the alarm which will fire the notification at the set time each day as long as there is a change in data
                        createNotificationAlarm();
                    } else {
                        Toast.makeText(getApplicationContext(), "You need to select at least one category", Toast.LENGTH_LONG).show();
                        notificationSwitch.setChecked(false);
                    }
                } else {
                    //if the checkboxes are not checked (or unchecked) remove the notification builder and cancel the alarm
                    mBuilder = null;

                    //cancel the alarm
                    if (alarmManager != null) {
                        Intent intentAlarm = new Intent(getApplicationContext(), ScheduleBroadcastReciever.class);
                        alarmManager.cancel(PendingIntent.getBroadcast(getApplicationContext(), 1, intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT));
                        alarmManager = null;

                    }

                }
            }
        });

        //restores any previous checked state of the notification switched from shared preferences
        notificationSwitch.setChecked(sharedPreferencesHelper.getboolean("myPrefs", "notificationSwitch", false));

    }

    private void createNotificationAlarm() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 9);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long scheduleTime = c.getTimeInMillis(); //the first alarm is set to run at 9am the next day

        // Create an Intent and set the class that will execute when the Alarm triggers. Here we have
        // specified ScheduleBroadcastReciever class in the Intent. The onReceive() method of this class will execute when the broadcast from the alarm is received.
        Intent intentAlarm = new Intent(getApplicationContext(), ScheduleBroadcastReciever.class);

        // Get the Alarm Service.
        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        // Set the reocurance of the alarm to be 24 hours 1000*60*60*24
        long alarmRecurranceTime = 1000 * 60* 60 * 24;

        alarmManager.setRepeating(AlarmManager.RTC, scheduleTime, alarmRecurranceTime, PendingIntent.getBroadcast(getApplicationContext(), 1, intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT));

    }

    public TextView getNotificationSearchTerm() {
        return notificationSearchTerm;
    }

    @Override
    protected void onPause() {
        super.onPause();

        //save the state of the notification check switch
        boolean isChecked = notificationSwitch.isChecked();
        sharedPreferencesHelper.booleanToSharedPreferences("notificationSwitch", isChecked);

        //loop through each settings check box and if they box is checked, add it to the checked array list and then store in shared preferences along with
        //the search term text
        checkedArrayList = new ArrayList<>();
        for (CheckBox c : settingsCheckBoxes) {
            if (c.isChecked()) {
                checkedArrayList.add(c.getTag().toString());
            }
        }
        sharedPreferencesHelper.stringToSharedPreferences("checkBoxVals", GsonHelper.stringMyArrayList(checkedArrayList));
        sharedPreferencesHelper.stringToSharedPreferences("searchTerm", notificationSearchTerm.getText().toString());

    }
}



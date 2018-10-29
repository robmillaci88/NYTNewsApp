package com.example.robmillaci.nytnews.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robmillaci.nytnews.R;
import com.example.robmillaci.nytnews.Utils.GsonHelper;
import com.example.robmillaci.nytnews.Utils.ScheduleBroadcastReciever;
import com.example.robmillaci.nytnews.Utils.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {
    public static NotificationCompat.Builder mBuilder;
    private static final String CHANNEL_ID = "NYTNotification";
    Switch notificationSwitch;
    Context mContext;
    public static ArrayList<CheckBox> settingsCheckBoxes;
    public static AlarmManager alarmManager;
    ArrayList<String> checkedArrayList;

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

        boolean display = getIntent().getBooleanExtra("display", true);
        setContentView(R.layout.activity_settings);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = this;

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

        settingsCheckBoxes = new ArrayList<>();
        settingsCheckBoxes.add(foodSettingsCheckBox);
        settingsCheckBoxes.add(scienceSettingsCheckBox);
        settingsCheckBoxes.add(entreSettingsCheckBox);
        settingsCheckBoxes.add(moviesSettingsCheckBox);
        settingsCheckBoxes.add(sportsSettingsCheckBox);
        settingsCheckBoxes.add(travelSettingsCheckBox);

        notificationSwitch = findViewById(R.id.notificationSwitch);

        try {
            //noinspection unchecked
            checkedArrayList = GsonHelper.getMyArray(mContext, "checkBoxVals");
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

        notificationSearchTerm.setText(sharedPreferencesHelper.getString("myPrefs", "searchTerm", ""));

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
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

                        mBuilder = new NotificationCompat.Builder(SettingsActivity.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.read)
                                .setContentTitle("NYT news items")
                                .setContentText("There are new news items you might be interested in")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);

                        createNotificationAlarm();
                    } else {
                        Toast.makeText(mContext, "You need to select at least one category", Toast.LENGTH_LONG).show();
                        notificationSwitch.setChecked(false);
                    }
                } else {
                    mBuilder = null;

                    //cancel the alarm
                    if (alarmManager != null) {
                        Intent intentAlarm = new Intent(mContext, ScheduleBroadcastReciever.class);
                        alarmManager.cancel(PendingIntent.getBroadcast(mContext, 1, intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT));
                        alarmManager = null;

                    }

                }
            }
        });


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
        Intent intentAlarm = new Intent(mContext, ScheduleBroadcastReciever.class);

        // Get the Alarm Service.
        alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        // Set the reocurance of the alarm to be 24 hours 1000*60*60*24
        long alarmRecurranceTime = 1000*60*60*24;

        alarmManager.setRepeating(AlarmManager.RTC, scheduleTime, alarmRecurranceTime, PendingIntent.getBroadcast(mContext, 1, intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT));

    }

    public TextView getNotificationSearchTerm() {
        return notificationSearchTerm;
    }

    @Override
    protected void onPause() {
        super.onPause();
        boolean isChecked = notificationSwitch.isChecked();
        sharedPreferencesHelper.booleanToSharedPreferences("notificationSwitch", isChecked);

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



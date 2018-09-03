package com.example.robmillaci.nytnews;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationRecurrance extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Schedule.setNotifcationHasBeenSent(false);
    }
}

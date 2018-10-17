package com.example.robmillaci.nytnews.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationsReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ScheduleBroadcastReciever.setNotifcationHasBeenSent(false);
    }
}

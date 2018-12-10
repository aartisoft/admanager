package com.admanager.periodicnotification;

import android.content.Context;
import android.support.annotation.Keep;

@Keep
public class PeriodicNotification {
    public static final String TAG = "PeriodicNotification";

    @Keep
    public static void init(Context context) {
        // save last launch date
        Helper.with(context).increaseLaunchTimes();

        // set alarm
        ReminderService.setAlarm(context);
    }

}
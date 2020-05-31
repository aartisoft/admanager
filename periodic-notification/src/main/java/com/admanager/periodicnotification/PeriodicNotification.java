package com.admanager.periodicnotification;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Keep;

import com.admanager.config.RemoteConfigHelper;

@Keep
public class PeriodicNotification {
    static final String TAG = "PeriodicNotification";

    @Keep
    public static void init(Context context) {
        RemoteConfigHelper.init(context);

        // save last launch date
        if (context instanceof Activity) {
            Helper.with(context).increaseLaunchTimes();
        }

        // set alarm
        ReminderReceiver.setAlarm(context);
    }

}
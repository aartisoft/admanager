package com.admanager.periodicnotification;

import android.content.Context;
import android.support.annotation.Keep;

import com.admanager.config.RemoteConfigHelper;

@Keep
public class PeriodicNotification {
    static final String TAG = "PeriodicNotification";

    @Keep
    public static void init(Context context) {
        RemoteConfigHelper.init(context);

        // save last launch date
        Helper.with(context).increaseLaunchTimes();

        // set alarm
        ReminderService.setAlarm(context);
    }

}
package com.admanager.periodicnotification;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.admanager.config.RemoteConfigHelper;

import static com.admanager.periodicnotification.PeriodicNotification.TAG;

/**
 * Created by a on 30.01.2017.
 */
public class Helper {
    private static final String PREF_FILE_NAME = "adman_periodic_notif";

    private static final String PREF_KEY_NOTIFICATION = "notification";
    private static final String PREF_KEY_INSTALL_DATE = "install_date";
    private static final String PREF_KEY_LAUNCH_TIMES = "launch_times";
    private static final String PREF_KEY_LAST_LAUNCH_DATE = "last_launch_date";

    private static Helper instance;
    private final SharedPreferences sharedPreferences;

    private Helper(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static Helper with(Context context) {
        if (instance == null) {
            instance = new Helper(context);
        }
        return instance;
    }



    @Nullable
    static Notif getNotif(Context context) {
        if (context == null) {
            return null;
        }

        RemoteConfigHelper.init(context);

        PeriodicNotificationApp pna = PeriodicNotificationApp.getInstance();
        if (pna == null) {
            return null;
        }

        // get keys from application context
        PeriodicNotificationKeys keys = pna.getKeys();

        // return null keys
        if (keys == null) {
            keys = new PeriodicNotificationKeys();
            Log.w(TAG, "Since PeriodicNotificationApp returns null keys, default remote config keys was used.");
        }
        Log.d(TAG, "Remote config keys: " + keys.toString());

        // convert keys to notif object
        Notif notif = new Notif(keys);
        if (!notif.isValid()) {
            notif = with(context).getNotif();
            if (notif == null || !notif.isValid()) {
                Log.d(TAG, "Notification values are not valid yet.");
                return null;
            }
        } else {
            with(context).setNotif(notif);
        }

        return notif;
    }

    private Notif getNotif() {
        String string = sharedPreferences.getString(PREF_KEY_NOTIFICATION, null);
        if (string == null) {
            return null;
        }
        return Notif.deserialize(string);
    }

    private void setNotif(Notif notif) {
        if (notif == null) {
            return;
        }
        sharedPreferences
                .edit()
                .putString(PREF_KEY_NOTIFICATION, notif.serialize())
                .apply();
    }

    private void setInstallDate() {
        sharedPreferences
                .edit()
                .putLong(PREF_KEY_INSTALL_DATE, System.currentTimeMillis())
                .apply();
    }

    public long getInstallDate() {
        return sharedPreferences
                .getLong(PREF_KEY_INSTALL_DATE, 0);
    }


    long getLastLaunchDate() {
        return sharedPreferences
                .getLong(PREF_KEY_LAST_LAUNCH_DATE, 0);
    }

    void increaseLaunchTimes() {
        int launchTimes = getLaunchTimes();
        if (launchTimes == 0) {
            setInstallDate();
        }
        sharedPreferences
                .edit()
                .putInt(PREF_KEY_LAUNCH_TIMES, ++launchTimes)
                .putLong(PREF_KEY_LAST_LAUNCH_DATE, System.currentTimeMillis())
                .apply();
    }

    private int getLaunchTimes() {
        return sharedPreferences
                .getInt(PREF_KEY_LAUNCH_TIMES, 0);
    }

}
package com.admanager.periodicnotification;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;

import com.admanager.config.RemoteConfigHelper;

import java.util.List;

import static com.admanager.periodicnotification.PeriodicNotification.TAG;

/**
 * Created by a on 30.01.2017.
 */
public class Helper {
    private static final String PREF_FILE_NAME = "adman_periodic_notif";

    private static final String PREF_KEY_CURRENT_NOTIFICATION = "current_notification";
    private static final String PREF_KEY_NOTIFICATION = "notification_";
    private static final String PREF_KEY_REPEAT_TIME = "notification_repeat_";
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
    static Notif getNextAvailableNotif(Context context) {
        if (context == null) {
            Log.e(TAG, "null context");
            return null;
        }

        RemoteConfigHelper.init(context);

        PeriodicNotificationApp pna = PeriodicNotificationApp.getInstance();
        if (pna == null) {
            Log.e(TAG, "null pna");
            return null;
        }

        // get keys from application context
        PeriodicNotificationKeys keys = pna.getKeys();


        List<Notif> notifs = Notif.getAsList(context, keys);


        // return next available
        long lastLaunchDate = Helper.with(context).getLastLaunchDate();
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - lastLaunchDate;

        Notif earliestNotif = null;
        long earliestNotifTime = Long.MAX_VALUE;
        for (Notif notif : notifs) {
            if (notif.getDelayInMillis() > timePassed && (notif.getDelayInMillis() + lastLaunchDate < earliestNotifTime)) {
                earliestNotif = notif;
                earliestNotifTime = lastLaunchDate + notif.getDelayInMillis();
            } else if (notif.repeat > 1) {
                long times = 1 + (timePassed / notif.getDelayInMillis());
                if (times <= notif.repeat) {
                    if (times * notif.getDelayInMillis() > timePassed && (lastLaunchDate + times * notif.getDelayInMillis() < earliestNotifTime)) {
                        earliestNotif = notif;
                        earliestNotifTime = lastLaunchDate + times * notif.getDelayInMillis();
                    }
                }
            }
        }

        if (earliestNotif != null) {
            Log.d(TAG, "Remote config keys: " + PeriodicNotificationKeys.copyWithSuffix(keys, earliestNotif.suffix));
        }

        return earliestNotif;
    }

    Notif getNotif(String suffix) {
        String string = sharedPreferences.getString(PREF_KEY_NOTIFICATION + suffix, null);
        if (string == null) {
            return null;
        }
        return Notif.deserialize(string);
    }

    protected void setNotif(String suffix, Notif notif) {
        if (notif == null) {
            return;
        }
        sharedPreferences
                .edit()
                .putString(PREF_KEY_NOTIFICATION + suffix, notif.serialize())
                .apply();
    }


    Notif getCurrentNotif() {
        String string = sharedPreferences.getString(PREF_KEY_CURRENT_NOTIFICATION, null);
        if (string == null) {
            return null;
        }
        return Notif.deserialize(string);
    }

    protected void setCurrentNotif(Notif notif) {
        if (notif == null) {
            return;
        }
        sharedPreferences
                .edit()
                .putString(PREF_KEY_CURRENT_NOTIFICATION, notif.serialize())
                .apply();
    }

    long getRepeatTime(Notif notif) {
        if (notif == null) {
            return 0;
        }
        return sharedPreferences.getLong(PREF_KEY_REPEAT_TIME + notif.suffix, 0);
    }

    long increaseRepeatTime(Notif notif) {
        if (notif == null) {
            return 0;
        }
        long l = getRepeatTime(notif) + 1;
        sharedPreferences
                .edit()
                .putLong(PREF_KEY_REPEAT_TIME + notif.suffix, l)
                .apply();
        return l;
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
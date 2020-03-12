package com.admanager.unseen.utils;

import android.content.Context;
import android.provider.Settings;

import com.admanager.unseen.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by a on 28.01.2017.
 */

public class Utils {

    private static String TAG = "Utils";

    public static String getStringFormattedDate(Context context, long time) {
        if (time == 0L) {
            return "";
        }
        Calendar tc = Calendar.getInstance();
        tc.setTime(new Date(time));
        Calendar now = Calendar.getInstance();

        long delta = Math.abs(now.getTimeInMillis() - tc.getTimeInMillis()) / 1000;

        if (delta < 86400) // 24 * 60 * 60
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            return simpleDateFormat.format(tc.getTime());
        } else {
            delta /= (60.0 * 60.0 * 24.0); // convert seconds to days
            return (int) delta + " " + context.getResources().getString(R.string.timeago_days);
        }
    }

    public static String shortenText(String txt) {
        if (txt == null || "".equals(txt.trim())) {
            return null;
        }

        if (txt.length() < 36) {
            return txt;
        }

        return String.format("%s...", txt.substring(0, 36));
    }

    public static boolean checkNotificationEnabled(Context context) {
        try {
            String string = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
            CharSequence packageName = context.getPackageName();
            return !(string == null || !string.contains(packageName));
        } catch (Exception e) {
            return false;
        }
    }

}
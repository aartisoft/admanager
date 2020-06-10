package com.admanager.periodicnotification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.admanager.core.Consts;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ReminderReceiver extends BroadcastReceiver {
    public static final String TAG = PeriodicNotification.TAG;

    public static final int ALARM_ID = 778;
    public static final int NOTIF_ID = 779;

    public static void setAlarm(Context context) {
        Notif notif = Helper.getNextAvailableNotif(context);

        if (notif == null) {
            Log.e(TAG, "setAlarm notif is null");
            return;
        }

        long delay;

        if (notif.isEnabled(context)) {
            delay = notif.getDelayInMillis();
        } else {
            delay = TimeUnit.HOURS.toMillis(1); // try 1 hours later
        }

        Helper.with(context).setCurrentNotif(notif);

        AlarmManager am = getAlarmManager(context);
        PendingIntent pi = getPendingIntent(context);
        am.cancel(pi);
        long lastLaunchDate = Helper.with(context).getLastLaunchDate();
        if (lastLaunchDate + delay < System.currentTimeMillis())
            lastLaunchDate = System.currentTimeMillis();
        Log.i(TAG, "Next Alarm Time:" + new Date(lastLaunchDate + delay) + " SUFFIX:" + notif.suffix + " NOTIF:" + notif.serialize());
        am.set(AlarmManager.RTC_WAKEUP, lastLaunchDate + delay, pi);
    }

    private static AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private static PendingIntent getPendingIntent(Context context) {
        Intent serviceIntent = new Intent(context, ReminderReceiver.class);
        return PendingIntent.getBroadcast(context, ALARM_ID, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public static void sendNotification(Context context, Notif notif) {
        PeriodicNotificationApp app = PeriodicNotificationApp.getInstance();
        if (app == null) {
            Log.e(TAG, "sendNotification null app");
            return;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(app.getChannelId(), app.getChannelName(), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
        long[] VIBRATE = new long[]{1000, 1000, 1000, 1000, 1000};
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, app.getChannelId())
                .setContentTitle(notif.title)
                .setContentText(notif.content)
                .setTicker(notif.ticker)
                .setVibrate(VIBRATE)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        if (app.getIconSmall() != 0) {
            mBuilder = mBuilder.setSmallIcon(app.getIconSmall());
        }

        if (app.getIconLarge() != 0) {
            mBuilder = mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), app.getIconLarge()));
        }

        if (app.getIntent() != null) {
            app.getIntent().putExtra(Consts.IntentClickParam.PERIODIC_NOTIFICATION_SUFFIX, notif.suffix);
            mBuilder = mBuilder.setContentIntent(PendingIntent.getActivity(context, 0, app.getIntent(), PendingIntent.FLAG_UPDATE_CURRENT));
        }

        notificationManager.notify(NOTIF_ID, mBuilder.build());

        // log firebase event
        long count = Helper.with(context).increaseRepeatTime(notif);
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putLong("period", notif.getDelayInMillis() / 1000);
        bundle.putLong("repeat", notif.repeat);
        bundle.putLong("count", count);
        firebaseAnalytics.logEvent("perinotif_sent_" + notif.suffix, bundle);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Notif notif = Helper.with(context).getCurrentNotif();
        if (notif == null) {
            Log.e(TAG, "null notif");
            return;
        }

        if (notif.isEnabled(context)) {
            sendNotification(context, notif);
        }

        setAlarm(context);


    }
}
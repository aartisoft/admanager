package com.admanager.periodicnotification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ReminderReceiver extends BroadcastReceiver {
    public static final String TAG = PeriodicNotification.TAG;

    public static final int ALARM_ID = 778;
    public static final int NOTIF_ID = 779;

    public static void setAlarm(Context context) {
        Notif notif = Helper.getNotif(context);
        if (notif == null) {
            Log.e(TAG, "setAlarm notif is null");
            return;
        }

        setAlarm(context, notif);
    }

    private static void setAlarm(Context context, @NonNull Notif notif) {
        long delay;

        if (notif.isEnabled()) {
            delay = TimeUnit.DAYS.toMillis(1) * notif.days;
        } else {
            delay = TimeUnit.HOURS.toMillis(1); // try 1 hours later
        }

        AlarmManager am = getAlarmManager(context);
        PendingIntent pi = getPendingIntent(context);
        am.cancel(pi);
        long lastLaunchDate = Helper.with(context).getLastLaunchDate();
        if (lastLaunchDate + delay < System.currentTimeMillis())
            lastLaunchDate = System.currentTimeMillis();
        Log.i(TAG, "Next Alarm Time:" + new Date(lastLaunchDate + delay));
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
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(app.getIntent());
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            mBuilder = mBuilder.setContentIntent(resultPendingIntent);
        }


        notificationManager.notify(NOTIF_ID, mBuilder.build());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Notif notif = Helper.getNotif(context);
        if (notif == null) {
            Log.e(TAG, "null notif");
            return;
        }

        if (notif.isEnabled()) {
            sendNotification(context, notif);
        }

        setAlarm(context, notif);
    }
}
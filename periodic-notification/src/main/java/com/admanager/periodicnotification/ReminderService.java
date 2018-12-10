package com.admanager.periodicnotification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class ReminderService extends JobIntentService {
    public static final String TAG = PeriodicNotification.TAG;

    public static final int ALARM_ID = 778;
    public static final int NOTIF_ID = 779;

    public static void setAlarm(Context context) {
        Notif notif = Helper.getNotif(context);
        if (notif == null) {
            return;
        }

        setAlarm(context, notif);
    }

    private static void setAlarm(Context context, @NonNull Notif notif) {
        long delay;

        if (notif.isEnabled())
            delay = TimeUnit.DAYS.toMillis(1) * notif.days;
        else {
            delay = TimeUnit.HOURS.toMillis(1); // try 1 hours later
        }

        AlarmManager am = getAlarmManager(context);
        PendingIntent pi = getPendingIntent(context);
        am.cancel(pi);
        long lastLaunchDate = Helper.with(context).getLastLaunchDate();
        if (lastLaunchDate + delay < System.currentTimeMillis())
            lastLaunchDate = System.currentTimeMillis();
        Log.i(TAG, " time:" + (lastLaunchDate + delay));
        am.set(AlarmManager.RTC_WAKEUP, lastLaunchDate + delay, pi);
    }

    private static AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private static PendingIntent getPendingIntent(Context context) {
        Intent serviceIntent = new Intent(context, ReminderService.class);
        return PendingIntent.getService(context, ALARM_ID, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public static void sendNotification(Context context, Notif notif) {
        PeriodicNotificationApp app = Helper.getPeriodicNotificationApp(context);
        NotificationConfigs configs = app == null ? null : app.withConfigs();


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "remind_me";
        String channelName = "Keeps you updated.";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
        long[] VIBRATE = new long[]{1000, 1000, 1000, 1000, 1000};
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(notif.title)
                .setContentText(notif.content)
                .setTicker(notif.ticker)
                .setVibrate(VIBRATE)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));


        if (configs != null) {
            if (configs.smallIcon != 0) {
                mBuilder = mBuilder.setSmallIcon(configs.smallIcon);
            }


            if (configs.largeIcon != 0) {
                mBuilder = mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), configs.largeIcon));
            }

            if (configs.clickToGo != null) {
                Intent clickIntent = new Intent(context, configs.clickToGo);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntent(clickIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
                mBuilder = mBuilder.setContentIntent(resultPendingIntent);
            }
        }


        notificationManager.notify(NOTIF_ID, mBuilder.build());
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Notif notif = Helper.getNotif(getApplicationContext());
        if (notif == null) {
            return;
        }

        if (notif.isEnabled()) {
            sendNotification(getApplicationContext(), notif);
        }

        setAlarm(getApplicationContext(), notif);
    }
}
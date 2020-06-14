package com.admanager.equalizer.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.admanager.equalizer.R;
import com.admanager.equalizer.activities.EqoVolActivity;

public class NotificationUtils {
    public static void createNotification(Context context) {
        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(context, EqoVolActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Send data to NotificationView Class
        intent.putExtra("title", "title");
        intent.putExtra("text", "text");
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.app_icon, "Previous", pIntent).build();
        // Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context, Constants.NOTIFICATION_START_CHANNEL_ID);

        nBuilder.setDefaults(0)
                .setSmallIcon(R.drawable.notification_logo)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.notification_logo))
                .setTicker(context.getResources().getString(R.string.app_name))
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(context.getResources().getString(R.string.notification_text))
                .setContentIntent(pIntent)
                .setOngoing(true)
                .setAutoCancel(false);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_START_CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null, null);
            channel.enableLights(false);
            channel.enableVibration(false);
            if (notificationmanager != null) notificationmanager.createNotificationChannel(channel);
        }
        // Build Notification with Notification Manager
        if (notificationmanager != null)
            notificationmanager.notify(Constants.NOTIFICATION_START_ID, nBuilder.build());
    }

    public static void cancelNotification(Context context) {
        NotificationManager oldNotification = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (oldNotification != null) oldNotification.cancel(Constants.NOTIFICATION_START_ID);
    }
}

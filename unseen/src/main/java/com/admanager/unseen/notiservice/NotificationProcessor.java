package com.admanager.unseen.notiservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.admanager.unseen.R;
import com.admanager.unseen.activities.UnseenActivity;
import com.admanager.unseen.notiservice.converters.BaseConverter;
import com.admanager.unseen.notiservice.models.AppNotification;
import com.admanager.unseen.notiservice.models.Conversation;

/**
 * Created by a on 28.03.2017.
 */
public class NotificationProcessor {
    private static final String TAG = "NotificationProcessor";
    private String title;
    private int hashCode;
    private BaseConverter converter;

    public NotificationProcessor(Class<? extends BaseConverter> converterClazz) {
        try {
            converter = converterClazz.newInstance();
        } catch (Throwable e) {
            Log.i(TAG, "converter class could not created.");
            return;
        }
        this.title = converter.getData().getTitle();
        this.hashCode = this.title.hashCode();
    }

    public void posted(Context context, Notification notification) {
        if (notification == null || (notification.category != null && "service".equals(notification.category))) {
            return;
        }

        AppNotification a = NotifExtractorManager.extract(context, notification);
        if (a != null && a.isValid()) {
            Log.i(TAG, a.toString());
            Conversation m = converter.convert(a);
            showNotification(context, m);
        }
    }

    private void showNotification(Context context, Conversation m) {
        if (m != null) {

            //notification click intent
//            Intent clickIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            Intent clickIntent = new Intent(context, UnseenActivity.class);
            Bundle b = new Bundle();
            b.putString("conversation", m.getTitle());
            clickIntent.putExtras(b);
            PendingIntent aIntent = PendingIntent.getActivity(context, 0, clickIntent, 0);

            String channelID = "unseen_notif_channel";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel chan = new NotificationChannel(channelID, context.getString(R.string.unseen_title), NotificationManager.IMPORTANCE_NONE);
                chan.setLightColor(Color.BLUE);
                chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                NotificationManager service = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (service != null) {
                    service.createNotificationChannel(chan);
                }
            }

            Notification build = new NotificationCompat.Builder(context, channelID)
                    .setContentTitle(context.getString(R.string.unseen_new_message, this.title))
                    .setContentText(m.getTitle() + ": " + m.getLastMessageText())
                    .setSmallIcon(android.R.drawable.star_off)//todo logo small
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.circle_white)) // todo logo
//                    .setOngoing(true)
                    .setAutoCancel(false)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_LIGHTS)
                    .setWhen(0)//see notif @ top
                    .build();
            build.contentIntent = aIntent;
            getNotificationManager(context).notify(hashCode, build);

            return;
        }
    }

    private NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void removed(Context context) {
        getNotificationManager(context).cancel(this.hashCode);
    }
}

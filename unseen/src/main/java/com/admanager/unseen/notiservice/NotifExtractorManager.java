package com.admanager.unseen.notiservice;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import com.admanager.unseen.notiservice.models.AppNotification;

/**
 * Created by a on 28.03.2017.
 */
public class NotifExtractorManager {
    private static final String TAG = "WhatsAppNotificationEM";

    public static AppNotification extract(Context context, Notification notification) {
        AppNotification a = extract(context, notification, new R2D2NotificationExtractor());
        if (a == null || !a.isValid()) {
            return extract(context, notification, new JediNotificationExtractor());
        }
        return a;
    }

    private static AppNotification extract(Context context, Notification notification, INotificationExtractor extractor) {
        new StringBuilder("START extracting ").append(extractor.getName());
        try {
            AppNotification wan = extractor.extract(context, notification);
            return wan;
        } catch (Throwable t) {
            Log.w(TAG, "We could not posted the notification " + extractor.getName(), t);
            return null;
        }
    }

}

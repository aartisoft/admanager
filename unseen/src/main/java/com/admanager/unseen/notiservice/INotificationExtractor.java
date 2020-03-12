package com.admanager.unseen.notiservice;

import android.app.Notification;
import android.content.Context;

import com.admanager.unseen.notiservice.models.AppNotification;

/**
 * Created by a on 28.03.2017.
 */
public interface INotificationExtractor {
    AppNotification extract(Context context, Notification notification);

    String getName();
}

package com.admanager.unseen.notiservice;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.admanager.unseen.notiservice.converters.BaseConverter;
import com.admanager.unseen.notiservice.converters.FBookConverter;
import com.admanager.unseen.notiservice.converters.FBookLiteConverter;
import com.admanager.unseen.notiservice.converters.SkypeConverter;
import com.admanager.unseen.notiservice.converters.SkypeLiteConverter;
import com.admanager.unseen.notiservice.converters.TeleConverter;
import com.admanager.unseen.notiservice.converters.VibeConverter;
import com.admanager.unseen.notiservice.converters.WeChatConverter;
import com.admanager.unseen.notiservice.converters.WhatsAppConverter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by a on 28.03.2017.
 */
public class NotiListenerService extends NotificationListenerService {
    private static final String TAG = "NotiListenerService";
    public static boolean isConnected;
    private static LinkedHashMap<String, Class<? extends BaseConverter>> hm;

    public static ArrayList<String> getPackageFromType(String shortHand) {
        ArrayList<String> list = new ArrayList<>();
        for (Map.Entry<String, Class<? extends BaseConverter>> entry : getPackageMap().entrySet()) {
            try {
                if (entry.getValue().newInstance().getType().equals(shortHand)) {
                    list.add(entry.getKey());
                }
            } catch (Throwable e) {
            }
        }
        return list;
    }

    public static LinkedHashMap<String, Class<? extends BaseConverter>> getPackageMap() {
        if (hm == null) {
            hm = new LinkedHashMap<>();
            hm.put("com.whatsapp", WhatsAppConverter.class);
            hm.put("com.facebook.orca", FBookConverter.class);
            hm.put("com.facebook.mlite", FBookLiteConverter.class);
            hm.put("com.tencent.mm", WeChatConverter.class);
            hm.put("com.viber.voip", VibeConverter.class);
            hm.put("org.telegram.messenger", TeleConverter.class);

            hm.put("com.skype.raider", SkypeConverter.class);
            hm.put("com.skype.m2", SkypeLiteConverter.class);
        }
        return hm;
    }

    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        isConnected = true;
        return super.onBind(intent);
    }

    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind");
        isConnected = false;
        return super.onUnbind(intent);
    }

    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        Log.i(TAG, "posted: " + packageName);
//        if (packageName == null || (sbn.getTag() != null && Build.VERSION.SDK_INT >= 21)) {
        if (packageName == null) {
//            Log.i(TAG, "Detected \"ghost\" notification from: " + packageName + ", eat it! ;)");
            return;
        }
        NotificationProcessor processor = getProcessor(packageName);
        if (processor != null) {
            processor.posted(this, sbn.getNotification());
        }

    }

    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (sbn == null || sbn.getPackageName() == null) {
            return;
        }
        String packageName = sbn.getPackageName();
        Log.i(TAG, "removed: " + packageName);
        NotificationProcessor processor = getProcessor(packageName);
        if (processor != null) {
            processor.removed(this);
        }
    }

    private NotificationProcessor getProcessor(String packageName) {
        Class<? extends BaseConverter> converterClazz = getPackageMap().get(packageName);
        if (converterClazz == null) {
            return null;
        }
        return new NotificationProcessor(converterClazz);
    }

}

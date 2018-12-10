package com.admanager.sample;

import android.app.Application;

import com.admanager.config.RemoteConfigApp;
import com.admanager.periodicnotification.NotificationConfigs;
import com.admanager.periodicnotification.PeriodicNotificationApp;
import com.admanager.periodicnotification.PeriodicNotificationKeys;
import com.admanager.sample.activity.Splash1Activity;

import java.util.Map;

public class MyApplication extends Application implements RemoteConfigApp, PeriodicNotificationApp {

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public Map<String, Object> getDefaults() {
        return RCUtils.getDefaults();
    }

    @Override
    public PeriodicNotificationKeys withRemoteConfigKeys() {
        return new PeriodicNotificationKeys(RCUtils.NOTIF_ENABLE, RCUtils.NOTIF_DAYS, RCUtils.NOTIF_TITLE, RCUtils.NOTIF_TICKER, RCUtils.NOTIF_CONTENT);
    }

    @Override
    public NotificationConfigs withConfigs() {
        return new NotificationConfigs(Splash1Activity.class, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background);
    }
}

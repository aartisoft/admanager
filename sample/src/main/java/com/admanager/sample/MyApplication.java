package com.admanager.sample;

import android.support.multidex.MultiDexApplication;

import com.admanager.applocker.AppLockerApp;
import com.admanager.boosternotification.BoosterNotificationApp;
import com.admanager.config.RemoteConfigApp;
import com.admanager.periodicnotification.PeriodicNotificationApp;

public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        //        new AdmStaticNotification.Builder(this, R.string.easy_access_title, R.string.easy_access_text)
        //                .build();

        new PeriodicNotificationApp.Builder(this)
                .build();

        new RemoteConfigApp.Builder(RCUtils.getDefaults())
                .build();

        new BoosterNotificationApp.Builder(this)
                .build();

        new AppLockerApp.Builder(this)
                .build();
    }

}

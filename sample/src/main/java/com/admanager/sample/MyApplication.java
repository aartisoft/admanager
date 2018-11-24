package com.admanager.sample;

import android.app.Application;

import com.admanager.config.RemoteConfigApp;

import java.util.Map;

public class MyApplication extends Application implements RemoteConfigApp {

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public Map<String, Object> getDefaults() {
        return RCUtils.getDefaults();
    }
}

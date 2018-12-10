package com.admanager.periodicnotification;

import android.text.TextUtils;

import com.admanager.config.RemoteConfigHelper;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class Notif {
    private static String SPLITTER = "!_--_!";
    public boolean enabled;
    public long days;
    public String title;
    public String ticker;
    public String content;


    private Notif(String[] serialized) {
        int i = 0;
        this.days = Long.parseLong(serialized[i++]);
        this.enabled = Boolean.parseBoolean(serialized[i++]);
        this.title = serialized[i++];
        this.ticker = serialized[i++];
        this.content = serialized[i];
    }

    Notif(PeriodicNotificationKeys keys) {
        FirebaseRemoteConfig configs = RemoteConfigHelper.getConfigs();
        this.enabled = configs.getBoolean(keys.enabled);
        this.days = configs.getLong(keys.days);
        this.title = configs.getString(keys.title);
        this.ticker = configs.getString(keys.ticker);
        this.content = configs.getString(keys.content);
    }

    static Notif deserialize(String string) {
        if (TextUtils.isEmpty(string)) {
            return null;
        }

        String[] split = string.split(SPLITTER);
        if (split.length != 5) {
            return null;
        }

        return new Notif(split);
    }

    public boolean isValid() {
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(ticker) || TextUtils.isEmpty(content)) {
            return false;
        }
        return days >= 1;
    }

    public boolean isEnabled() {
        return isValid() && enabled;
    }

    String serialize() {
        return days + SPLITTER + enabled + SPLITTER + title + SPLITTER + ticker + SPLITTER + content;
    }
}
package com.admanager.periodicnotification;

import android.text.TextUtils;

import com.admanager.config.RemoteConfigHelper;

public class Notif {
    public boolean enabled;
    public long days;
    public String title;
    public String ticker;
    public String content;

    public Notif(PeriodicNotificationKeys keys) {
        this.enabled = RemoteConfigHelper.getConfigs().getBoolean(keys.enabled);
        this.days = RemoteConfigHelper.getConfigs().getLong(keys.days);
        this.title = RemoteConfigHelper.getConfigs().getString(keys.title);
        this.ticker = RemoteConfigHelper.getConfigs().getString(keys.ticker);
        this.content = RemoteConfigHelper.getConfigs().getString(keys.content);
    }

    public static Notif deserialize(String string) {
        return null;
    }

    public boolean isValid() {
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(ticker) || TextUtils.isEmpty(content)) {
            return false;
        }
        if (days < 1) {
            return false;
        }
        return true;
    }

    public boolean isEnabled() {
        return isValid() && enabled;
    }

    public String serialize() {
        return null;
    }
}
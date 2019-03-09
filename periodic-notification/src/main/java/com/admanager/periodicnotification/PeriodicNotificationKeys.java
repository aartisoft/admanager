package com.admanager.periodicnotification;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;
import android.util.Log;

import com.admanager.core.Consts;

public class PeriodicNotificationKeys {
    String enabledKey;
    String daysKey;
    String titleKey;
    String tickerKey;
    String contentKey;

    PeriodicNotificationKeys() {
    }

    public PeriodicNotificationKeys(String enabledKey, String daysKey, String titleKey, String tickerKey, String contentKey) {
        this.enabledKey = enabledKey;
        this.daysKey = daysKey;
        this.titleKey = titleKey;
        this.tickerKey = tickerKey;
        this.contentKey = contentKey;
    }

    public void validate() {
        if (TextUtils.isEmpty(enabledKey)) {
            throw new IllegalArgumentException("enabledKey value is null.");
        }
        if (TextUtils.isEmpty(daysKey)) {
            throw new IllegalArgumentException("daysKey value is null.");
        }
        if (TextUtils.isEmpty(titleKey)) {
            throw new IllegalArgumentException("titleKey value is null.");
        }
        if (TextUtils.isEmpty(tickerKey)) {
            throw new IllegalArgumentException("tickerKey value is null.");
        }
        if (TextUtils.isEmpty(contentKey)) {
            throw new IllegalArgumentException("contentKey value is null.");
        }
    }

    void setDefaultValues(Context context) {
        boolean debug = (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));

        this.enabledKey = checkAndSetDefaultKey(debug, this.enabledKey, Consts.PeriodicNotification.DEFAULT_ENABLE_KEY);
        this.daysKey = checkAndSetDefaultKey(debug, this.daysKey, Consts.PeriodicNotification.DEFAULT_DAYS_KEY);
        this.titleKey = checkAndSetDefaultKey(debug, this.titleKey, Consts.PeriodicNotification.DEFAULT_TITLE_KEY);
        this.tickerKey = checkAndSetDefaultKey(debug, this.tickerKey, Consts.PeriodicNotification.DEFAULT_TICKER_KEY);
        this.contentKey = checkAndSetDefaultKey(debug, this.contentKey, Consts.PeriodicNotification.DEFAULT_CONTENT_KEY);
    }

    private String checkAndSetDefaultKey(boolean debug, String firebaseKey, String defaultKey) {
        if (firebaseKey == null) {
            if (debug) {
                Log.i(Consts.TAG, "Periodic Notification is initialized with default firebase keys: " + defaultKey);
            }
            return defaultKey;
        }
        return firebaseKey;
    }
    @Override
    public String toString() {
        return "PeriodicNotificationKeys{" +
                "enabledKey='" + enabledKey + '\'' +
                ", daysKey='" + daysKey + '\'' +
                ", titleKey='" + titleKey + '\'' +
                ", tickerKey='" + tickerKey + '\'' +
                ", contentKey='" + contentKey + '\'' +
                '}';
    }
}
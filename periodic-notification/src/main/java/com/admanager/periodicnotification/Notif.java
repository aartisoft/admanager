package com.admanager.periodicnotification;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.admanager.config.RemoteConfigHelper;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.admanager.periodicnotification.PeriodicNotification.TAG;

public class Notif {
    @SerializedName("enabled")
    public boolean enabled;
    @SerializedName("repeat")
    public long repeat;
    @SerializedName("title")
    public String title;
    @SerializedName("ticker")
    public String ticker;
    @SerializedName("content")
    public String content;
    @SerializedName("suffix")
    public String suffix;
    @SerializedName("days")
    private long days;
    @SerializedName("mins")
    private long mins;

    private Notif(PeriodicNotificationKeys keys) {
        FirebaseRemoteConfig configs = RemoteConfigHelper.getConfigs();
        this.enabled = configs.getBoolean(keys.enabledKey);
        this.days = configs.getLong(keys.daysKey);
        this.title = configs.getString(keys.titleKey);
        this.ticker = configs.getString(keys.tickerKey);
        this.content = configs.getString(keys.contentKey);
        this.mins = configs.getLong(keys.minsKey);
        this.repeat = configs.getLong(keys.repeatKey);
        if (this.mins > 0 && this.days == 0 && this.repeat == 0) {
            // set default repeat count 0 for minute metrics
            this.repeat = 1;
        }
        if (this.mins == 0 && this.days > 0 && this.repeat == 0) {
            // set default repeat count 0 for minute metrics
            this.repeat = Long.MAX_VALUE;
        }
        this.suffix = keys.getSuffix();
    }

    public static List<Notif> getAsList(Context context, PeriodicNotificationKeys keys) {
        List<Notif> o = new ArrayList<>();
        FirebaseRemoteConfig configs = RemoteConfigHelper.getConfigs();

        Set<String> keysByPrefix = configs.getKeysByPrefix(keys.enabledKey);
        for (String byPrefix : keysByPrefix) {
            String suffix = byPrefix.substring(keys.enabledKey.length());
            PeriodicNotificationKeys k = PeriodicNotificationKeys.copyWithSuffix(keys, suffix);
            Notif n = Notif.getCachedValuesWithKeys(context, k);
            if (n != null && n.isEnabled(context)) {
                o.add(n);
            }
        }

        // sort
        Collections.sort(o, new Comparator<Notif>() {
            @Override
            public int compare(Notif n0, Notif n1) {
                long l0 = n0.getDelayInMillis();
                long l1 = n1.getDelayInMillis();
                if (l0 > l1) {
                    return 1;
                }
                if (l0 < l1) {
                    return -1;
                }
                return 0;
            }
        });

        return o;
    }

    static Notif deserialize(String string) {
        if (TextUtils.isEmpty(string)) {
            return null;
        }

        return new Gson().fromJson(string, Notif.class);
    }

    private static Notif getCachedValuesWithKeys(Context context, PeriodicNotificationKeys keys) {
        String suffix = keys.getSuffix();
        Notif notif = new Notif(keys);
        if (!notif.isValid()) {
            notif = Helper.with(context).getNotif(suffix);
            if (notif == null || !notif.isValid()) {
                Log.d(TAG, "Notification values are not valid for suffix: " + suffix);
                return null;
            }
        } else {
            Helper.with(context).setNotif(suffix, notif);
        }
        return notif;

    }

    public long getDelayInMillis() {
        if (days > 0) {
            return TimeUnit.DAYS.toMillis(1) * days;
        }
        return TimeUnit.MINUTES.toMillis(1) * mins;
    }

    public boolean isValid() {
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(ticker) || TextUtils.isEmpty(content)) {
            return false;
        }
        return days >= 1 || mins >= 1;
    }

    public boolean isEnabled(Context context) {
        boolean b = isValid() && enabled;

        long repeatTime = Helper.with(context).getRepeatTime(this);
        if (repeatTime >= repeat) {
            Log.w(TAG, "Max repeat time occurred for this notif: " + suffix);
            return false;
        }
        return b;
    }

    String serialize() {
        return new Gson().toJson(this);
    }
}
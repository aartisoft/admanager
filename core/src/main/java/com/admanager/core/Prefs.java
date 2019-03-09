package com.admanager.core;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.TimeUnit;

class Prefs {
    private static final String PREF_FILE_NAME = "adm_rate_pref_file";

    private static final String USER_RATE = "user_rate";
    private static final String USER_RATE_COUNT = "user_rate_count";
    private static final String USER_RATE_DATE = "user_rate_date";
    private static final String USER_RATE_STORE_COUNT = "user_rate_store_count";
    private static final String OPEN_COUNT = "open_count";
    private static final String INSTALL_DATE = "install_date";
    private static final String LAST_OPEN_DATE = "last_open_date";

    private static Prefs instance;
    private final SharedPreferences sharedPreferences;

    Prefs(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    static Prefs with(Context context) {
        if (instance == null) {
            instance = new Prefs(context);
        }
        return instance;
    }

    private long getInstallDate() {
        return sharedPreferences.getLong(INSTALL_DATE, 0);
    }

    public void appOpened() {
        if (getInstallDate() == 0) {
            sharedPreferences.edit().putLong(INSTALL_DATE, System.currentTimeMillis()).apply();
        }
        sharedPreferences.edit().putLong(LAST_OPEN_DATE, System.currentTimeMillis()).apply();
        sharedPreferences.edit().putLong(OPEN_COUNT, getOpenCount() + 1).apply();

    }

    private long getOpenCount() {
        return sharedPreferences.getLong(OPEN_COUNT, 0);
    }

    private float getUserRate() {
        return sharedPreferences.getFloat(USER_RATE, 0);
    }

    void setUserRate(float rate) {
        float existingRate = getUserRate();
        if (existingRate < 5) {
            sharedPreferences.edit().putFloat(USER_RATE, rate).apply();
            sharedPreferences.edit().putLong(USER_RATE_DATE, System.currentTimeMillis()).apply();
            increaseUserRateCount();
        }
    }

    private long getUserRateDate() {
        return sharedPreferences.getLong(USER_RATE_DATE, 0);
    }

    private void increaseUserRateCount() {
        sharedPreferences.edit().putInt(USER_RATE_COUNT, getUserRateCount() + 1).apply();
    }

    private int getUserRateCount() {
        return sharedPreferences.getInt(USER_RATE_COUNT, 0);
    }

    void increaseUserRateStoreCount() {
        increaseUserRateStoreCount(false);
    }

    void increaseUserRateStoreCount(boolean goneToStore) {
        sharedPreferences.edit().putInt(USER_RATE_STORE_COUNT, getUserRateStoreCount() + (goneToStore ? 100 : 1)).apply();
    }

    private int getUserRateStoreCount() {
        return sharedPreferences.getInt(USER_RATE_STORE_COUNT, 0);
    }

    boolean remindRate(int firstAskAfterOpening, int askingRateDayInterval) {
        float rate = getUserRate();
        boolean multiOpened = getOpenCount() >= firstAskAfterOpening;
        boolean notRated = rate == 0;
        boolean lowRated = rate < 5;
        boolean highRated = rate == 5;
        boolean askingDayIntervalPassed = (System.currentTimeMillis() - getUserRateDate()) / TimeUnit.DAYS.toMillis(1) > askingRateDayInterval;
        int rateCount = getUserRateCount();

        if (!multiOpened || highRated) {
            return false;
        }
        return notRated || (lowRated && askingDayIntervalPassed && rateCount == 1);
    }

    boolean remindStore(int askingStoreDayInterval) {
        float rate = getUserRate();
        boolean highRated = rate == 5;
        boolean remindAfterRate = (System.currentTimeMillis() - getUserRateDate()) / TimeUnit.DAYS.toMillis(1) > askingStoreDayInterval;

        int storeCount = getUserRateStoreCount();
        if (highRated && (storeCount == 0 || (storeCount <= 2 && remindAfterRate))) {
            return true;
        } else {
            return false;
        }
    }

}

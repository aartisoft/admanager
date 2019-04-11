package com.admanager.applocker;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.widget.LinearLayout;

import com.admanager.applocker.activities.SplashActivity;
import com.admanager.applocker.utils.AppLockInitializer;

public class AppLockerApp {
    private static AppLockerApp INSTANCE;
    private final Ads ads;

    private AppLockerApp(Ads ads) {
        this.ads = ads;
    }

    public static AppLockerApp getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("You should initialize AppLockerApp!");
        }
        return INSTANCE;
    }

    private static AppLockerApp init(AppLockerApp appLockerApp) {
        INSTANCE = appLockerApp;
        return INSTANCE;
    }

    public static void showActivity(Activity activity) {
        activity.startActivity(new Intent(activity, SplashActivity.class));
    }

    public Ads getAds() {
        return ads;
    }

    public static class Builder {

        private Application application;
        private Ads ads;

        public Builder(Application application) {
            this.application = application;
        }

        public Builder ads(Ads ads) {
            this.ads = ads;
            return this;
        }

        public void build() {
            if (ads == null) {
                ads = new Ads();
            }
            AppLockInitializer.init(application);
            AppLockerApp.init(new AppLockerApp(ads));
        }

    }

    public static class Ads {
        public void loadTop(Activity activity, LinearLayout container) {

        }

        public void loadBottom(Activity activity, LinearLayout container) {

        }
    }
}
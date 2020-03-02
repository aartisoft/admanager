package com.admanager.applocker;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;

import com.admanager.applocker.activities.SplashActivity;
import com.admanager.applocker.utils.AppLockInitializer;
import com.admanager.core.Ads;
import com.admanager.core.AdsImp;

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
        if (!isDeviceSuitable()) {
            return;
        }
        activity.startActivity(new Intent(activity, SplashActivity.class));
    }

    public static boolean isDeviceSuitable() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.O;
    }

    public static void configureMenu(NavigationView navigationView, int menuId) {
        if (navigationView == null) {
            return;
        }

        MenuItem item = navigationView.getMenu().findItem(menuId);
        if (item == null) {
            throw new IllegalStateException("Given menuId couldn't found!");
        }

        item.setVisible(isDeviceSuitable());
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
                ads = new AdsImp();
            }
            AppLockInitializer.init(application);
            AppLockerApp.init(new AppLockerApp(ads));
        }

    }
}
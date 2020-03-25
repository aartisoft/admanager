package com.admanager.gpstimeaddresscoord;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.admanager.core.Ads;
import com.admanager.core.AdsImp;

import java.lang.ref.WeakReference;

public class GPSTimeApp {
    private static GPSTimeApp INSTANCE;
    public Ads ads;

    GPSTimeApp(Application app, Ads ads) {
        this.ads = ads;
    }

    public static GPSTimeApp getInstance() {
        return INSTANCE;
    }

    private static GPSTimeApp init(GPSTimeApp gpsTimeApp) {
        INSTANCE = gpsTimeApp;

        return INSTANCE;
    }

    public static class Builder {

        private final WeakReference<Context> context;
        private Ads ads;

        public Builder(@NonNull Application context) {
            this.context = new WeakReference<>(context.getApplicationContext());
        }

        public Builder ads(Ads ads) {
            this.ads = ads;
            return this;
        }

        public void build() {
            if (ads == null) {
                ads = new AdsImp();
            }
            Context context = this.context.get();

            Application app = (Application) context.getApplicationContext();
            GPSTimeApp.init(new GPSTimeApp(app, ads));

        }

    }
}
package com.admanager.gpstimeaddresscoord;

import android.app.Application;
import android.content.Context;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;

import com.admanager.core.Ads;
import com.admanager.core.AdsImp;

import java.lang.ref.WeakReference;

public class GPSTimeApp {
    private static GPSTimeApp INSTANCE;
    public Ads ads;
    public int bgColor;
    public int cardBgColor;
    public int textColor;

    GPSTimeApp(Application app, Ads ads, int bgColor, int cardBgColor, int textColor) {
        this.ads = ads;
        this.bgColor = bgColor;
        this.cardBgColor = cardBgColor;
        this.textColor = textColor;
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
        private int bgColor, cardBgColor, textColor;

        public Builder(@NonNull Application context) {
            this.context = new WeakReference<>(context.getApplicationContext());
        }

        public Builder ads(Ads ads) {
            this.ads = ads;
            return this;
        }

        public GPSTimeApp.Builder bgColor(@ColorRes int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public GPSTimeApp.Builder textColor(@ColorRes int textColor) {
            this.textColor = textColor;
            return this;
        }

        public GPSTimeApp.Builder cardBgColor(@ColorRes int cardBgColor) {
            this.cardBgColor = cardBgColor;
            return this;
        }

        public void build() {
            if (ads == null) {
                ads = new AdsImp();
            }
            Context context = this.context.get();

            Application app = (Application) context.getApplicationContext();
            GPSTimeApp.init(new GPSTimeApp(app, ads, bgColor, cardBgColor, textColor));

        }

    }
}
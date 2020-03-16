package com.admanager.weather;

import android.app.Application;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;

import com.admanager.core.Ads;
import com.admanager.core.AdsImp;

import java.lang.ref.WeakReference;

public class WeatherApp {
    private static WeatherApp INSTANCE;
    public Ads ads;
    public int bgColor;
    public int itemBg;
    public int textColor;

    WeatherApp(Application app, Ads ads, int bgColor, int itemBg, int textColor) {
        this.ads = ads;
        this.bgColor = bgColor;
        this.itemBg = itemBg;
        this.textColor = textColor;
    }

    public static WeatherApp getInstance() {
        return INSTANCE;
    }

    private static WeatherApp init(WeatherApp weatherApp) {
        INSTANCE = weatherApp;
        return INSTANCE;
    }

    public static class Builder {

        private final WeakReference<Context> context;
        private Ads ads;
        private int bgColor;
        private int itemBg;
        private int textColor;

        public Builder(@NonNull Application context) {
            this.context = new WeakReference<>(context.getApplicationContext());
        }

        public Builder ads(Ads ads) {
            this.ads = ads;
            return this;
        }

        public WeatherApp.Builder bgColor(@ColorRes int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public WeatherApp.Builder itemBg(@ColorRes int itemBg) {
            this.itemBg = itemBg;
            return this;
        }

        public WeatherApp.Builder textColor(@ColorRes int textColor) {
            this.textColor = textColor;
            return this;
        }

        public void build() {
            if (ads == null) {
                ads = new AdsImp();
            }
            Context context = this.context.get();

            Application app = (Application) context.getApplicationContext();
            WeatherApp.init(new WeatherApp(app, ads, bgColor, itemBg, textColor));

        }

    }
}
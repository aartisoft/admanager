package com.admanager.speedtest;

import android.app.Application;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.admanager.core.Ads;
import com.admanager.core.AdsImp;

import java.lang.ref.WeakReference;

public class SpeedTestApp {
    private static SpeedTestApp INSTANCE;
    public String title;
    public Ads ads;
    public int bgColor;
    public int textColor;
    public int buttonBgColor;
    public int buttonTextColor;

    SpeedTestApp(Application app, Ads ads, String title, int bgColor, int buttonBgColor, int buttonTextColor, int textColor) {
        this.title = title;
        this.ads = ads;
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.buttonBgColor = buttonBgColor;
        this.buttonTextColor = buttonTextColor;
    }

    public static SpeedTestApp getInstance() {
        return INSTANCE;
    }

    private static SpeedTestApp init(SpeedTestApp compassApp) {
        INSTANCE = compassApp;
        return INSTANCE;
    }

    public static class Builder {

        private final WeakReference<Context> context;
        private String title;
        private Ads ads;
        private int bgColor;
        private int textColor;
        private int buttonBgColor;
        private int buttonTextColor;

        public Builder(@NonNull Application context) {
            this.context = new WeakReference<>(context.getApplicationContext());
        }

        public Builder ads(Ads ads) {
            this.ads = ads;
            return this;
        }

        public SpeedTestApp.Builder title(String title) {
            if (title == null) {
                throw new IllegalArgumentException("null title is not allowed!");
            }
            this.title = title;
            return this;
        }

        public SpeedTestApp.Builder title(@StringRes int title) {
            Context c = context.get();
            if (c != null) {
                this.title = c.getString(title);
            }
            return this;
        }

        public SpeedTestApp.Builder buttonBgColor(@ColorRes int buttonBgColor) {
            Context c = context.get();
            if (c != null) this.buttonBgColor = buttonBgColor;
            return this;
        }

        public SpeedTestApp.Builder buttonTextColor(@ColorRes int buttonTextColor) {
            Context c = context.get();
            if (c != null) this.buttonTextColor = buttonTextColor;
            return this;
        }

        public SpeedTestApp.Builder bgColor(@ColorRes int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public SpeedTestApp.Builder textColor(@ColorRes int textColor) {
            this.textColor = textColor;
            return this;
        }

        public void build() {
            if (ads == null) {
                ads = new AdsImp();
            }
            Context context = this.context.get();

            Application app = (Application) context.getApplicationContext();
            SpeedTestApp.init(new SpeedTestApp(app, ads, title, bgColor, buttonBgColor, buttonTextColor, textColor));

        }

    }
}
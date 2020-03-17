package com.admanager.compass;

import android.app.Application;
import android.content.Context;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.admanager.core.Ads;
import com.admanager.core.AdsImp;

import java.lang.ref.WeakReference;

public class CompassApp {
    private static CompassApp INSTANCE;
    public String title;
    public Ads ads;
    public int bgColor;
    public int bgDrawable;
    public int textColor;
    public int compassNeedle;
    public int compassBg;
    public boolean qibla;
    public int kabeIcon;

    CompassApp(Application app, Ads ads, String title, int bgColor, int bgDrawable, int compassNeedle, int compassBg, int textColor, boolean qibla, int kabeIcon) {
        this.title = title;
        this.ads = ads;
        this.bgColor = bgColor;
        this.bgDrawable = bgDrawable;
        this.textColor = textColor;
        this.compassNeedle = compassNeedle;
        this.kabeIcon = kabeIcon;
        this.compassBg = compassBg;
        this.qibla = qibla;
    }

    public static CompassApp getInstance() {
        return INSTANCE;
    }

    private static CompassApp init(CompassApp compassApp) {
        INSTANCE = compassApp;
        return INSTANCE;
    }

    public static class Builder {

        private final WeakReference<Context> context;
        private String title;
        private Ads ads;
        private int bgColor;
        private int bgDrawable;
        private int textColor;
        private int compassNeedle;
        private int compassBg;
        private int kabeIcon;
        private boolean qibla;

        public Builder(@NonNull Application context) {
            this.context = new WeakReference<>(context.getApplicationContext());
        }

        public Builder ads(Ads ads) {
            this.ads = ads;
            return this;
        }

        public CompassApp.Builder title(String title) {
            if (title == null) {
                throw new IllegalArgumentException("null title is not allowed!");
            }
            this.title = title;
            return this;
        }

        public CompassApp.Builder showQibla() {
            this.qibla = true;
            return this;
        }

        public CompassApp.Builder showQibla(@DrawableRes int kabeIcon) {
            this.qibla = true;
            this.kabeIcon = kabeIcon;
            return this;
        }

        public CompassApp.Builder title(@StringRes int title) {
            Context c = context.get();
            if (c != null) {
                this.title = c.getString(title);
            }
            return this;
        }

        public CompassApp.Builder compassNeedle(@DrawableRes int compassNeedle) {
            Context c = context.get();
            if (c != null) this.compassNeedle = compassNeedle;
            return this;
        }

        public CompassApp.Builder compassBg(@DrawableRes int compassBg) {
            Context c = context.get();
            if (c != null) this.compassBg = compassBg;
            return this;
        }

        public CompassApp.Builder bgColor(@ColorRes int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public CompassApp.Builder bgDrawable(@DrawableRes int bgDrawable) {
            this.bgDrawable = bgDrawable;
            return this;
        }

        public CompassApp.Builder textColor(@ColorRes int textColor) {
            this.textColor = textColor;
            return this;
        }

        public void build() {
            if (ads == null) {
                ads = new AdsImp();
            }
            Context context = this.context.get();

            Application app = (Application) context.getApplicationContext();
            CompassApp.init(new CompassApp(app, ads, title, bgColor, bgDrawable, compassNeedle, compassBg, textColor, qibla, kabeIcon));

        }

    }
}
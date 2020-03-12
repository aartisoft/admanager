package com.admanager.unseen;

import android.app.Application;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.admanager.core.Ads;
import com.admanager.core.AdsImp;

import java.lang.ref.WeakReference;

public class UnseenApp {
    private static UnseenApp INSTANCE;
    public String title;
    public Ads ads;
    public int bgColor;
    public int bgDrawable;
    public int textColor;
    public int compassNeedle;
    public int compassBg;
    public boolean qibla;
    public int kabeIcon;

    UnseenApp(Application app, Ads ads, String title, int bgColor, int bgDrawable, int compassNeedle, int compassBg, int textColor, boolean qibla, int kabeIcon) {
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

    public static UnseenApp getInstance() {
        return INSTANCE;
    }

    private static UnseenApp init(UnseenApp compassApp) {
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

        public UnseenApp.Builder title(String title) {
            if (title == null) {
                throw new IllegalArgumentException("null title is not allowed!");
            }
            this.title = title;
            return this;
        }

        public UnseenApp.Builder showQibla() {
            this.qibla = true;
            return this;
        }

        public UnseenApp.Builder showQibla(@DrawableRes int kabeIcon) {
            this.qibla = true;
            this.kabeIcon = kabeIcon;
            return this;
        }

        public UnseenApp.Builder title(@StringRes int title) {
            Context c = context.get();
            if (c != null) {
                this.title = c.getString(title);
            }
            return this;
        }

        public UnseenApp.Builder compassNeedle(@DrawableRes int compassNeedle) {
            Context c = context.get();
            if (c != null) this.compassNeedle = compassNeedle;
            return this;
        }

        public UnseenApp.Builder compassBg(@DrawableRes int compassBg) {
            Context c = context.get();
            if (c != null) this.compassBg = compassBg;
            return this;
        }

        public UnseenApp.Builder bgColor(@ColorRes int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public UnseenApp.Builder bgDrawable(@DrawableRes int bgDrawable) {
            this.bgDrawable = bgDrawable;
            return this;
        }

        public UnseenApp.Builder textColor(@ColorRes int textColor) {
            this.textColor = textColor;
            return this;
        }

        public void build() {
            if (ads == null) {
                ads = new AdsImp();
            }
            Context context = this.context.get();

            Application app = (Application) context.getApplicationContext();
            UnseenApp.init(new UnseenApp(app, ads, title, bgColor, bgDrawable, compassNeedle, compassBg, textColor, qibla, kabeIcon));

        }

    }
}
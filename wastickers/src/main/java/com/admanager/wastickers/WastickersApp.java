package com.admanager.wastickers;

import android.app.Application;
import android.content.Context;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.admanager.core.Ads;
import com.admanager.core.AdsImp;

import java.lang.ref.WeakReference;

public class WastickersApp {
    private static com.admanager.wastickers.WastickersApp INSTANCE;
    public String title;
    public int iconWA;
    public int iconDownload;
    public Ads ads;
    public int bgColor;

    WastickersApp(Application app, Ads ads, String title, int iconWA, int iconDownload, int bgColor) {
        this.title = title;
        this.iconWA = iconWA;
        this.iconDownload = iconDownload;
        this.ads = ads;
        this.bgColor = bgColor;
    }

    public static com.admanager.wastickers.WastickersApp getInstance() {
        return INSTANCE;
    }

    private static com.admanager.wastickers.WastickersApp init(com.admanager.wastickers.WastickersApp wastickersApp) {
        INSTANCE = wastickersApp;
        return INSTANCE;
    }

    public static class Builder {

        private final WeakReference<Context> context;
        private String title;
        private int iconWA;
        private int iconDownload;
        private Ads ads;
        private int bgColor;

        public Builder(@NonNull Application context) {
            this.context = new WeakReference<>(context.getApplicationContext());
        }

        public Builder ads(Ads ads) {
            this.ads = ads;
            return this;
        }

        public WastickersApp.Builder title(String title) {
            if (title == null) {
                throw new IllegalArgumentException("null title is not allowed!");
            }
            this.title = title;
            return this;
        }

        public WastickersApp.Builder title(@StringRes int title) {
            Context c = context.get();
            if (c != null) {
                this.title = c.getString(title);
            }
            return this;
        }

        public WastickersApp.Builder iconWA(@DrawableRes int iconWA) {
            this.iconWA = iconWA;
            return this;
        }

        public WastickersApp.Builder iconDownload(@DrawableRes int iconDownload) {
            this.iconDownload = iconDownload;
            return this;
        }

        public WastickersApp.Builder bgColor(@ColorRes int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public void build() {
            if (ads == null) {
                ads = new AdsImp();
            }
            Context context = this.context.get();

            Application app = (Application) context.getApplicationContext();
            WastickersApp.init(new WastickersApp(app, ads, title, iconWA, iconDownload, bgColor));

        }

    }
}
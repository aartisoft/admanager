package com.admanager.gifs;

import android.app.Application;
import android.content.Context;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.admanager.core.Ads;
import com.admanager.core.AdsImp;

import java.lang.ref.WeakReference;

public class GifsApp {
    private static GifsApp INSTANCE;
    public int iconDownload;
    public Ads ads;
    public int bgColor;
    public boolean lightToolbarColor;
    public String api_key;

    GifsApp(Application app, Ads ads, String api_key, int iconDownload, int bgColor, boolean lightToolbarColor) {
        this.api_key = api_key;
        this.iconDownload = iconDownload;
        this.ads = ads;
        this.bgColor = bgColor;
        this.lightToolbarColor = lightToolbarColor;
    }

    public static GifsApp getInstance() {
        return INSTANCE;
    }

    private static GifsApp init(GifsApp gifsApp) {
        INSTANCE = gifsApp;
        return INSTANCE;
    }

    public static class Builder {

        private final WeakReference<Context> context;
        private String api_key;
        private int iconDownload;
        private Ads ads;
        private int bgColor;
        private boolean lightToolbarColor;

        public Builder(@NonNull Application context, String api_key) {
            this.context = new WeakReference<>(context.getApplicationContext());
            this.api_key = api_key;
        }

        public Builder ads(Ads ads) {
            this.ads = ads;
            return this;
        }

        public GifsApp.Builder iconDownload(@DrawableRes int iconDownload) {
            this.iconDownload = iconDownload;
            return this;
        }

        public GifsApp.Builder lightToolbarColor() {
            this.lightToolbarColor = true;
            return this;
        }

        public GifsApp.Builder bgColor(@ColorRes int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public void build() {
            if (ads == null) {
                ads = new AdsImp();
            }
            Context context = this.context.get();

            Application app = (Application) context.getApplicationContext();
            GifsApp.init(new GifsApp(app, ads, api_key, iconDownload, bgColor, lightToolbarColor));

        }

    }
}
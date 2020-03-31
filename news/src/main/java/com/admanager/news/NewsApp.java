package com.admanager.news;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.admanager.core.Ads;
import com.admanager.core.AdsImp;

import java.lang.ref.WeakReference;

public class NewsApp {
    private static NewsApp INSTANCE;
    public Ads ads;
    public String title;
    public String theStartMagazineKey;
    public String theStartMagazinePublisherId;

    NewsApp(Application app, Ads ads, String title, String theStartMagazineKey, String theStartMagazinePublisherId) {
        this.title = title;
        this.ads = ads;
        this.theStartMagazineKey = theStartMagazineKey;
        this.theStartMagazinePublisherId = theStartMagazinePublisherId;
    }

    public static NewsApp getInstance() {
        return INSTANCE;
    }

    private static NewsApp init(NewsApp NewsApp) {
        INSTANCE = NewsApp;
        return INSTANCE;
    }

    public static class Builder {

        private final WeakReference<Context> context;
        private String title;
        private String theStartMagazineKey;
        private String theStartMagazinePublisherId;
        private Ads ads;

        public Builder(@NonNull Application context, String theStartMagazineKey, String theStartMagazinePublisherId) {
            this.context = new WeakReference<>(context.getApplicationContext());
            if (theStartMagazineKey == null) {
                throw new IllegalArgumentException("null theStartMagazineKey is not allowed!");
            }
            if (theStartMagazinePublisherId == null) {
                throw new IllegalArgumentException("null theStartMagazinePublisherId is not allowed!");
            }
            this.theStartMagazineKey = theStartMagazineKey;
            this.theStartMagazinePublisherId = theStartMagazinePublisherId;
        }
/*
        public Builder ads(Ads ads) {
            this.ads = ads;
            return this;
        }*/

        public NewsApp.Builder title(String title) {
            if (title == null) {
                throw new IllegalArgumentException("null title is not allowed!");
            }
            this.title = title;
            return this;
        }

        public void build() {
            if (ads == null) {
                ads = new AdsImp();
            }
            Context context = this.context.get();

            Application app = (Application) context.getApplicationContext();
            NewsApp.init(new NewsApp(app, ads, title, theStartMagazineKey, theStartMagazinePublisherId));

        }

    }
}
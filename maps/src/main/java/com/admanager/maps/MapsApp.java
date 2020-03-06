package com.admanager.maps;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.admanager.core.Ads;
import com.admanager.core.AdsImp;

import java.lang.ref.WeakReference;

public class MapsApp {
    private static MapsApp INSTANCE;
    public Ads ads;

    MapsApp(Application app, Ads ads) {
        this.ads = ads;
    }

    public static MapsApp getInstance() {
        return INSTANCE;
    }

    private static MapsApp init(MapsApp mapsApp) {
        INSTANCE = mapsApp;

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
            MapsApp.init(new MapsApp(app, ads));

        }

    }
}
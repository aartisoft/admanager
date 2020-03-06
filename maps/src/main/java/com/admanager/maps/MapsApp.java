package com.admanager.maps;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

public class MapsApp {
    private static MapsApp INSTANCE;

    MapsApp(Application app) {

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

        public Builder(@NonNull Application context) {
            this.context = new WeakReference<>(context.getApplicationContext());
        }

        public void build() {
            Context context = this.context.get();

            Application app = (Application) context.getApplicationContext();
            MapsApp.init(new MapsApp(app));

        }

    }
}
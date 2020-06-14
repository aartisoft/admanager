package com.admanager.equalizer;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.admanager.core.Ads;
import com.admanager.core.AdsImp;

import java.lang.ref.WeakReference;

public class EqoVolApp {
    private static EqoVolApp INSTANCE;
    public Ads ads;
    public boolean canShowEqualizer;
    public boolean canShowVolumeBooster;

    EqoVolApp(Application app, Ads ads, boolean canShowEqualizer, boolean canShowVolumeBooster) {
        this.ads = ads;
        this.canShowEqualizer = canShowEqualizer;
        this.canShowVolumeBooster = canShowVolumeBooster;
    }

    public static EqoVolApp getInstance() {
        return INSTANCE;
    }

    private static EqoVolApp init(EqoVolApp eqoVolApp) {
        INSTANCE = eqoVolApp;

        return INSTANCE;
    }

    public static class Builder {

        private final WeakReference<Context> context;
        private Ads ads;
        private boolean canShowEqualizer, canShowVolumeBooster;

        public Builder(@NonNull Application context) {
            this.context = new WeakReference<>(context.getApplicationContext());
        }

        public Builder ads(Ads ads) {
            this.ads = ads;
            return this;
        }

        public Builder setEqualizer(boolean canShowEqualizer) {
            this.canShowEqualizer = canShowEqualizer;
            return this;
        }

        public Builder setVolumeBooster(boolean canShowVolumeBooster) {
            this.canShowVolumeBooster = canShowVolumeBooster;
            return this;
        }

        public void build() {
            if (ads == null) {
                ads = new AdsImp();
            }
            Context context = this.context.get();

            Application app = (Application) context.getApplicationContext();
            EqoVolApp.init(new EqoVolApp(app, ads, canShowEqualizer, canShowVolumeBooster));

        }
    }
}

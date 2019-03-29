package com.admanager.popupad;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.admanager.core.AdmUtils;

import java.lang.ref.WeakReference;

public class AdmPopupAd {
    private static final String TAG = "AdmPopupAd";
    private final AdSpecKeys keys;
    private final AppCompatActivity activity;

    private AdmPopupAd(AppCompatActivity activity, AdSpecKeys keys) {
        this.activity = activity;
        this.keys = keys;
    }

    public void show() {
        final AdSpecs adSpecs = new AdSpecs(this.keys);
        if (!adSpecs.isEnable() || AdmUtils.isContextInvalid(activity)) {
            return;
        }

        if (!adSpecs.isValid()) {
            return;
        }

        PopupAdFragment fragment = PopupAdFragment.createInstance(adSpecs);
        fragment.show(activity.getSupportFragmentManager(), "popup_ad");
    }

    public static class Builder {

        private final WeakReference<AppCompatActivity> activityWeakReference;
        private AdSpecKeys keys;

        public Builder(@NonNull AppCompatActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        public Builder withRemoteConfigKeys(@NonNull AdSpecKeys keys) {
            keys.validate();
            this.keys = keys;
            return this;
        }

        public AdmPopupAd build() {
            AppCompatActivity act = this.activityWeakReference.get();
            if (AdmUtils.isContextInvalid(act)) {
                return null;
            }
            if (keys == null) {
                keys = new AdSpecKeys();
            }
            keys.setDefaultValues(act);
            return new AdmPopupAd(act, keys);
        }


    }

}

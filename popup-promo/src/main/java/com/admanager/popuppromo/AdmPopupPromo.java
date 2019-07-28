package com.admanager.popuppromo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.admanager.core.AdmUtils;

import java.lang.ref.WeakReference;

public class AdmPopupPromo {
    private static final String TAG = "AdmPopupPromo";
    private final PromoSpecKeys keys;
    private final AppCompatActivity activity;

    private AdmPopupPromo(AppCompatActivity activity, PromoSpecKeys keys) {
        this.activity = activity;
        this.keys = keys;
    }

    public void show() {
        final PromoSpecs promoSpecs = new PromoSpecs(this.keys);
        if (!promoSpecs.isEnable() || AdmUtils.isContextInvalid(activity)) {
            return;
        }

        if (!promoSpecs.isValid()) {
            return;
        }

        PopupPromoFragment fragment = PopupPromoFragment.createInstance(promoSpecs);
        try {
            // for dismissing " Can not perform this action after onSaveInstanceState" error
            fragment.show(activity.getSupportFragmentManager(), "popup_ad");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Builder {

        private final WeakReference<AppCompatActivity> activityWeakReference;
        private PromoSpecKeys keys;

        public Builder(@NonNull AppCompatActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        public Builder withRemoteConfigKeys(@NonNull PromoSpecKeys keys) {
            keys.validate();
            this.keys = keys;
            return this;
        }

        public AdmPopupPromo build() {
            AppCompatActivity act = this.activityWeakReference.get();
            if (AdmUtils.isContextInvalid(act)) {
                return null;
            }
            if (keys == null) {
                keys = new PromoSpecKeys();
            }
            keys.setDefaultValues(act);
            return new AdmPopupPromo(act, keys);
        }


    }

}

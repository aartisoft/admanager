package com.admanager.popuppromo;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.admanager.core.AdmUtils;

import java.lang.ref.WeakReference;

public class AdmPopupPromo {
    private static final String TAG = "AdmPopupPromo";
    private final PromoSpecKeys keys;
    private final AppCompatActivity activity;
    private final Listener listener;

    private AdmPopupPromo(AppCompatActivity activity, PromoSpecKeys keys, Listener listener) {
        this.activity = activity;
        this.keys = keys;
        this.listener = listener;
    }

    public void show() {
        final PromoSpecs promoSpecs = new PromoSpecs(this.keys);
        if (!promoSpecs.isEnable() || AdmUtils.isContextInvalid(activity)) {
            if (listener != null) {
                listener.completed(false);
            }
            return;
        }

        if (!promoSpecs.isValid()) {
            if (listener != null) {
                listener.completed(false);
            }
            return;
        }

        final PopupPromoFragment fragment = PopupPromoFragment.createInstance(promoSpecs, listener);
        final String tag = "popup_ad";
        try {
            // for dismissing " Can not perform this action after onSaveInstanceState" error
            Fragment f = activity.getSupportFragmentManager().findFragmentByTag(tag);
            if (f != null && f.isAdded() && f instanceof DialogFragment) {
                ((DialogFragment) f).dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    fragment.show(activity.getSupportFragmentManager(), tag);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.completed(false);
                    }
                }
            }
        }, 500);

    }

    public interface Listener {
        void completed(boolean displayed);
    }

    public static class Builder {

        private final WeakReference<AppCompatActivity> activityWeakReference;
        private Listener listener;
        private PromoSpecKeys keys;

        public Builder(@NonNull AppCompatActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        public Builder withRemoteConfigKeys(@NonNull PromoSpecKeys keys) {
            keys.validate();
            this.keys = keys;
            return this;
        }

        public Builder listener(@NonNull Listener listener) {
            this.listener = listener;
            return this;
        }

        public AdmPopupPromo build() {
            AppCompatActivity act = this.activityWeakReference.get();
            if (keys == null) {
                keys = new PromoSpecKeys();
            }
            if (!AdmUtils.isContextInvalid(act)) {
                keys.setDefaultValues(act);
            }
            return new AdmPopupPromo(act, keys, listener);
        }


    }

}

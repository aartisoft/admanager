package com.admanager.popuppromo;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

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

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // for dismissing " Can not perform this action after onSaveInstanceState" error
                activity.runOnUiThread(new Runnable() {
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
                });
            }
        }).start();

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
            if (AdmUtils.isContextInvalid(act)) {
                return null;
            }
            if (keys == null) {
                keys = new PromoSpecKeys();
            }
            keys.setDefaultValues(act);
            return new AdmPopupPromo(act, keys, listener);
        }


    }

}

package com.admanager.popupenjoy;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.admanager.core.AdManagerBuilder;
import com.admanager.core.AdmUtils;

import java.io.Serializable;
import java.lang.ref.WeakReference;

public class AdmPopupEnjoy {
    private static final String TAG = "AdmPopupEnjoy";
    private final EnjoySpecKeys keys;
    private final Ads ads;
    private final AppCompatActivity activity;
    private final Listener listener;

    private AdmPopupEnjoy(AppCompatActivity activity, EnjoySpecKeys keys, Ads ads, Listener listener) {
        this.activity = activity;
        this.keys = keys;
        this.ads = ads;
        this.listener = listener;
    }

    public void show() {
        final EnjoySpecs enjoySpecs = new EnjoySpecs(this.keys);
        if (!enjoySpecs.isEnable() || AdmUtils.isContextInvalid(activity)) {
            if (listener != null) {
                listener.completed(false);
            }
            return;
        }

        final PopupEnjoyFragment fragment = PopupEnjoyFragment.createInstance(enjoySpecs, ads, listener);
        final String tag = "popup_enjoy";
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

    public interface Ads extends Serializable {
        AdManagerBuilder createAdManagerBuilder(Activity activity);

        void loadBottom(Activity activity, LinearLayout container);
    }

    public interface Listener {
        void completed(boolean displayed);
    }

    public static class Builder {

        private final WeakReference<AppCompatActivity> activityWeakReference;
        private Listener listener;
        private EnjoySpecKeys keys;
        private Ads ads;

        public Builder(@NonNull AppCompatActivity activity, Ads ads) {
            this.activityWeakReference = new WeakReference<>(activity);
            this.ads = ads;
        }

        public Builder listener(@NonNull Listener listener) {
            this.listener = listener;
            return this;
        }
        public Builder withRemoteConfigKeys(@NonNull EnjoySpecKeys keys) {
            keys.validate();
            this.keys = keys;
            return this;
        }

        public AdmPopupEnjoy build() {
            AppCompatActivity act = this.activityWeakReference.get();
            if (keys == null) {
                keys = new EnjoySpecKeys();
            }
            if (!AdmUtils.isContextInvalid(act)) {
                keys.setDefaultValues(act);
            }
            return new AdmPopupEnjoy(act, keys, ads, listener);
        }


    }

}

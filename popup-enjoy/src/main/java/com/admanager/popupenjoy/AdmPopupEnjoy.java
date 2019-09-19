package com.admanager.popupenjoy;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.admanager.core.AdManager;
import com.admanager.core.AdmUtils;

import java.io.Serializable;
import java.lang.ref.WeakReference;

public class AdmPopupEnjoy {
    private static final String TAG = "AdmPopupEnjoy";
    private final EnjoySpecKeys keys;
    private final Ads ads;
    private final AppCompatActivity activity;

    private AdmPopupEnjoy(AppCompatActivity activity, EnjoySpecKeys keys, Ads ads) {
        this.activity = activity;
        this.keys = keys;
        this.ads = ads;
    }

    public void show() {
        final EnjoySpecs enjoySpecs = new EnjoySpecs(this.keys);
        if (!enjoySpecs.isEnable() || AdmUtils.isContextInvalid(activity)) {
            return;
        }

        PopupEnjoyFragment fragment = PopupEnjoyFragment.createInstance(enjoySpecs, ads);
        String tag = "popup_enjoy";
        try {
            // for dismissing " Can not perform this action after onSaveInstanceState" error
            Fragment f = activity.getSupportFragmentManager().findFragmentByTag(tag);
            if (f != null && f.isAdded() && f instanceof DialogFragment) {
                ((DialogFragment) f).dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            // for dismissing " Can not perform this action after onSaveInstanceState" error
            fragment.show(activity.getSupportFragmentManager(), tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface Ads extends Serializable {
        AdManager createAdManager(Activity activity);

        void loadBottom(Activity activity, LinearLayout container);
    }

    public static class Builder {

        private final WeakReference<AppCompatActivity> activityWeakReference;
        private EnjoySpecKeys keys;
        private Ads ads;

        public Builder(@NonNull AppCompatActivity activity, Ads ads) {
            this.activityWeakReference = new WeakReference<>(activity);
            this.ads = ads;
        }

        public Builder withRemoteConfigKeys(@NonNull EnjoySpecKeys keys) {
            keys.validate();
            this.keys = keys;
            return this;
        }

        public AdmPopupEnjoy build() {
            AppCompatActivity act = this.activityWeakReference.get();
            if (AdmUtils.isContextInvalid(act)) {
                return null;
            }
            if (keys == null) {
                keys = new EnjoySpecKeys();
            }
            keys.setDefaultValues(act);
            return new AdmPopupEnjoy(act, keys, ads);
        }


    }

}

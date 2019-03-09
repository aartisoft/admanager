package com.admanager.popupad;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.admanager.core.Utils;

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
        if (!adSpecs.isEnable() || Utils.isContextInvalid(activity)) {
            return;
        }

        if (!adSpecs.isValid()) {
            return;
        }

        PopupAdFragment fragment = PopupAdFragment.createInstance(adSpecs);
        fragment.show(activity.getSupportFragmentManager(), "popup_ad");

        /*new AlertDialog.Builder(activity)
                .setTitle(adSpecs.getTitle())
                .setMessage(adSpecs.getMessage())
                .setNegativeButton(adSpecs.getNo(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(adSpecs.getYes(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.openLink(activity, adSpecs.getUrl());
                    }
                })
                .create()
                .show();*/
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
            if (Utils.isContextInvalid(act)) {
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

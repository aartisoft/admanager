package com.admanager.admob;

import android.util.Log;

import com.admanager.core.Adapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


public class AdmobAdapter extends Adapter {
    private final AdListener ADMOB_AD_LISTENER = new AdListener() {
        @Override
        public void onAdClosed() {
            closed();
        }

        @Override
        public void onAdFailedToLoad(int i) {
            error("code:" + i);
        }

        @Override
        public void onAdLoaded() {
            loaded();
        }
    };
    private final String key;
    private InterstitialAd adAdmob;


    public AdmobAdapter(String enableKey, String key) {
        super(enableKey);

        this.key = key;
    }

    @Override
    protected void init() {
        // admob
        adAdmob = new InterstitialAd(getActivity());
        adAdmob.setAdUnitId(key);
        adAdmob.setAdListener(ADMOB_AD_LISTENER);
        adAdmob.loadAd(new AdRequest.Builder().build());
    }

    @Override
    protected void destroy() {
        adAdmob = null;
    }

    @Override
    protected void show() {
        if (adAdmob.isLoaded()) {
            adAdmob.show();
        } else {
            closed();
            Log.e("AdManager1", "NOT LOADED");
        }
    }
}
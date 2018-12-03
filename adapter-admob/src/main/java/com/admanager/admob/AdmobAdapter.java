package com.admanager.admob;

import android.support.annotation.Size;
import android.text.TextUtils;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.Adapter;
import com.admanager.core.Consts;
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
    private String adUnitId;
    private InterstitialAd ad;
    private boolean anyIdMethodCalled;


    public AdmobAdapter(@Size(min = Consts.RC_KEY_SIZE) String rcEnableKey) {
        super(rcEnableKey);
    }

    public AdmobAdapter withRemoteConfigId(@Size(min = Consts.RC_KEY_SIZE) String rcAdUnitIdKey) {
        anyIdMethodCalled = true;
        if (this.adUnitId != null) {
            throw new IllegalStateException("You already set adUnitId with 'withId' method.");
        }
        this.adUnitId = RemoteConfigHelper.getConfigs().getString(rcAdUnitIdKey);
        return this;
    }

    public AdmobAdapter withId(@Size(min = 35, max = 40) String adUnitId) {
        anyIdMethodCalled = true;
        if (this.adUnitId != null) {
            throw new IllegalStateException("You already set adUnitId with 'withRemoteConfig' method");
        }
        this.adUnitId = adUnitId;
        return this;
    }

    @Override
    protected void init() {
        if (!anyIdMethodCalled) {
            throw new IllegalStateException("call 'withId' or 'withRemoteConfigId' method after adapter creation.");
        }
        if (TextUtils.isEmpty(this.adUnitId)) {
            error("NO AD_UNIT_ID FOUND!");
            return;
        }

        ad = new InterstitialAd(getActivity());
        ad.setAdUnitId(adUnitId);
        ad.setAdListener(ADMOB_AD_LISTENER);
        ad.loadAd(new AdRequest.Builder().build());
    }

    @Override
    protected void destroy() {
        ad = null;
    }

    @Override
    protected void show() {
        if (ad != null && ad.isLoaded()) {
            ad.show();
        } else {
            closed();
            loge("NOT LOADED");
        }
    }
}
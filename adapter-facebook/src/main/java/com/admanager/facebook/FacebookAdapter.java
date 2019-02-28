package com.admanager.facebook;

import android.support.annotation.Size;
import android.text.TextUtils;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.Adapter;
import com.admanager.core.Consts;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import java.util.concurrent.atomic.AtomicBoolean;


public class FacebookAdapter extends Adapter {
    private static final String FACEBOOK_INTERS_TEST_ID = "YOUR_PLACEMENT_ID";
    private String adUnitId;
    private InterstitialAd ad;
    private boolean anyIdMethodCalled;

    private AtomicBoolean mIsSdkInitialized = new AtomicBoolean(false);
    private InterstitialAdListener LISTENER = new InterstitialAdListener() {
        @Override
        public void onError(Ad ad, AdError adError) {
            error(adError.getErrorCode() + ":" + adError.getErrorMessage());
        }

        @Override
        public void onAdLoaded(Ad ad) {
            loaded();
        }

        @Override
        public void onInterstitialDismissed(Ad ad) {
            closed();
        }

        @Override
        public void onInterstitialDisplayed(Ad ad) {

        }

        @Override
        public void onAdClicked(Ad ad) {

        }

        @Override
        public void onLoggingImpression(Ad ad) {

        }
    };


    public FacebookAdapter(@Size(min = Consts.RC_KEY_SIZE) String rcEnableKey) {
        super(rcEnableKey);
    }

    @Override
    protected String getAdapterName() {
        return "Facebook";
    }

    public FacebookAdapter withRemoteConfigId(@Size(min = Consts.RC_KEY_SIZE) String rcAdUnitIdKey) {
        anyIdMethodCalled = true;
        if (this.adUnitId != null) {
            throw new IllegalStateException("You already set adUnitId with 'withId' method.");
        }
        this.adUnitId = RemoteConfigHelper.getConfigs().getString(rcAdUnitIdKey);
        return this;
    }

    public FacebookAdapter withId(@Size(min = com.admanager.facebook.Consts.AD_UNIT_SIZE_MIN, max = com.admanager.facebook.Consts.AD_UNIT_SIZE_MAX) String adUnitId) {
        anyIdMethodCalled = true;
        if (this.adUnitId != null) {
            throw new IllegalStateException("You already set adUnitId with 'withRemoteConfig' method");
        }
        this.adUnitId = adUnitId;
        return this;
    }


    public FacebookAdapter addTestDevice(@Size(min = 36, max = 36) String testDevice) {
        AdSettings.addTestDevice(testDevice);
        return this;
    }

    @Override
    protected void init() {
        if (isTestMode()) {
            this.adUnitId = FACEBOOK_INTERS_TEST_ID;
        }
        if (!anyIdMethodCalled) {
            throw new IllegalStateException("call 'withId' or 'withRemoteConfigId' method after adapter creation.");
        }
        if (TextUtils.isEmpty(this.adUnitId)) {
            error("NO AD_UNIT_ID FOUND!");
            return;
        }
        if (!mIsSdkInitialized.getAndSet(true)) {
            AudienceNetworkAds.initialize(getActivity());
        }

        ad = new com.facebook.ads.InterstitialAd(getActivity(), adUnitId);

        ad.setAdListener(LISTENER);
        ad.loadAd();

    }

    @Override
    protected void destroy() {
        if (ad != null) {
            ad.destroy();
        }
        ad = null;
    }

    @Override
    protected void show() {
        if (ad != null && ad.isAdLoaded()) {
            ad.show();
        } else {
            closed();
            loge("NOT LOADED");
        }
    }

}
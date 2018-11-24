package com.admanager.facebook;

import android.util.Log;

import com.admanager.core.Adapter;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import java.util.concurrent.atomic.AtomicBoolean;


public class FacebookAdapter extends Adapter implements InterstitialAdListener {
    private final String key;
    private InterstitialAd adFacebook;

    private AtomicBoolean mIsSdkInitialized = new AtomicBoolean(false);

    public FacebookAdapter(String enableKey, String key) {
        super(enableKey);
        this.key = key;

    }

    @Override
    protected void init() {
        if (!mIsSdkInitialized.getAndSet(true)) {
            AudienceNetworkAds.initialize(getActivity());
        }

        adFacebook = new com.facebook.ads.InterstitialAd(getActivity(), key);
        adFacebook.setAdListener(this);
        adFacebook.loadAd();

    }

    @Override
    protected void destroy() {
        if (adFacebook != null) {
            adFacebook.destroy();
        }
        adFacebook = null;
    }

    @Override
    protected void show() {
        if (adFacebook.isAdLoaded()) {
            adFacebook.show();
        } else {
            closed();
            Log.e("AdManager2", "NOT LOADED");
        }
    }

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
}
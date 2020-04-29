package com.admanager.admob;

import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.annotation.Size;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.AdapterWithNative;
import com.admanager.core.Consts;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

public class AdmobAdapterWithNative extends AdapterWithNative<AdmobAdapterWithNative> {

    private String adUnitId;
    private boolean anyIdMethodCalled;
    private UnifiedNativeAdView unifiedNativeAdView;

    public AdmobAdapterWithNative(@Size(min = Consts.RC_KEY_SIZE) String rcEnableKey) {
        super("AdmobWithNative", rcEnableKey);
    }

    public AdmobAdapterWithNative withRemoteConfigId(@Size(min = Consts.RC_KEY_SIZE) String rcAdUnitIdKey) {
        anyIdMethodCalled = true;
        if (this.adUnitId != null) {
            throw new IllegalStateException("You already set adUnitId with 'withId' method.");
        }
        this.adUnitId = RemoteConfigHelper.getConfigs().getString(rcAdUnitIdKey);
        return this;
    }

    public AdmobAdapterWithNative withId(@Size(min = com.admanager.admob.Consts.AD_UNIT_SIZE_MIN, max = com.admanager.admob.Consts.AD_UNIT_SIZE_MAX) String adUnitId) {
        anyIdMethodCalled = true;
        if (this.adUnitId != null) {
            throw new IllegalStateException("You already set adUnitId with 'withRemoteConfig' method");
        }
        this.adUnitId = adUnitId;
        return this;
    }

    @Override
    protected void init() {
        if (isTestMode()) {
            this.adUnitId = AdmobNativeLoader.ADMOB_NATIVE_TEST_ID;
        }
        if (!anyIdMethodCalled) {
            throw new IllegalStateException("call 'withId' or 'withRemoteConfigId' method after adapter creation.");
        }
        if (TextUtils.isEmpty(this.adUnitId)) {
            error("NO AD_UNIT_ID FOUND!");
            return;
        }

        // todo layout

        AdLoader mAdLoader = new AdLoader.Builder(getActivity(), this.adUnitId)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        logv("onUnifiedNativeAdLoaded");
                        unifiedNativeAdView = (UnifiedNativeAdView) getActivity().getLayoutInflater().inflate(R.layout.ad_native_unified_fullscreen, null);
                        AdmobAdHelper.populateUnifiedNativeAdView(unifiedNativeAd, unifiedNativeAdView);

                        AdmobAdapterWithNative.super.animateView(unifiedNativeAdView.getCallToActionView());

                        loaded();
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        error("onAdFailedToLoad: " + errorCode);
                    }

                    @Override
                    public void onAdClicked() {
                        logv("onAdClicked");
                        clicked();
                    }
                }).build();

        AdRequest.Builder builder = new AdRequest.Builder();
        mAdLoader.loadAd(builder.build());

    }

    @Override
    protected void loadAdsToContainer(ViewGroup container) {
        if (unifiedNativeAdView != null) {
            if (unifiedNativeAdView.getParent() != null) {
                ((ViewGroup) unifiedNativeAdView.getParent()).removeView(unifiedNativeAdView);
            }
            container.addView(unifiedNativeAdView);

            logv("impression");
        }
    }

    @Override
    protected void destroy() {
        super.destroy();
        if (unifiedNativeAdView != null) {
            unifiedNativeAdView.destroy();
        }
        unifiedNativeAdView = null;
    }

    @Override
    protected void show() {

        if (unifiedNativeAdView != null) {
            displayFullScreenNativeActivity();
        } else {
            closed();
            loge("NOT LOADED");
        }

    }

}
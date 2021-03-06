package com.admanager.applovin;

import android.text.TextUtils;

import androidx.annotation.Size;

import com.admanager.core.Adapter;
import com.admanager.core.Consts;
import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.adview.AppLovinInterstitialAdDialog;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdSize;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkSettings;

import java.util.LinkedList;
import java.util.Queue;

public class ApplovinAdapter extends Adapter {
    private static final Object GLOBAL_INTERSTITIAL_ADS_LOCK = new Object();
    private static Queue<AppLovinAd> GLOBAL_INTERSTITIAL_AD;
    private final AppLovinAdLoadListener LISTENER = new AppLovinAdLoadListener() {
        @Override
        public void adReceived(AppLovinAd appLovinAd) {
            logv("adReceived");
            enqueueAd(appLovinAd);
            loaded();
        }

        @Override
        public void failedToReceiveAd(int i) {
            error(":" + i);
        }
    };
    private final AppLovinAdClickListener CLICK_LISTENER = new AppLovinAdClickListener() {
        @Override
        public void adClicked(AppLovinAd appLovinAd) {
            logv("adClicked");
            clicked();
        }
    };
    private final AppLovinAdDisplayListener DISPLAY_LISTENER = new AppLovinAdDisplayListener() {
        @Override
        public void adDisplayed(AppLovinAd appLovinAd) {
            logv("adDisplayed");
        }

        @Override
        public void adHidden(AppLovinAd appLovinAd) {
            logv("adHidden");
            closed();
        }
    };
    private String sdkKey;
    private AppLovinSdk sdk;

    public ApplovinAdapter(@Size(min = Consts.RC_KEY_SIZE) String rcEnableKey) {
        super("Applovin", rcEnableKey);
    }

    private static AppLovinAd dequeueAd() {
        synchronized (GLOBAL_INTERSTITIAL_ADS_LOCK) {
            AppLovinAd preloadedAd = null;

            if (GLOBAL_INTERSTITIAL_AD != null && !GLOBAL_INTERSTITIAL_AD.isEmpty()) {
                preloadedAd = GLOBAL_INTERSTITIAL_AD.poll();
            }

            return preloadedAd;
        }
    }

    private static void enqueueAd(final AppLovinAd ad) {
        synchronized (GLOBAL_INTERSTITIAL_ADS_LOCK) {
            if (GLOBAL_INTERSTITIAL_AD == null) {
                GLOBAL_INTERSTITIAL_AD = new LinkedList<>();
            }

            GLOBAL_INTERSTITIAL_AD.offer(ad);
        }
    }

    public ApplovinAdapter withId(@Size(min = 75, max = 100) String sdkKey) {
        this.sdkKey = sdkKey;
        return this;
    }

    @Override
    protected void init() {
        if (TextUtils.isEmpty(sdkKey)) {
            throw new IllegalStateException("call 'withId(sdkKey)' method after adapter creation.");
        }
        if (sdk == null) {
            AppLovinSdkSettings appLovinSdkSettings = new AppLovinSdkSettings();
            appLovinSdkSettings.setTestAdsEnabled(isTestMode());
            sdk = AppLovinSdk.getInstance(sdkKey, appLovinSdkSettings, getActivity());
        }
        sdk.getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, LISTENER);
    }

    @Override
    protected void destroy() {
        super.destroy();
        sdk = null;
    }

    @Override
    protected void show() {
        if (sdk != null && isAdLoaded()) {
            final AppLovinAd preloadedAd = dequeueAd();
            if (preloadedAd != null) {
                final AppLovinInterstitialAdDialog interstitialAd = AppLovinInterstitialAd.create(sdk, getActivity());
                interstitialAd.setAdLoadListener(LISTENER);
                interstitialAd.setAdDisplayListener(DISPLAY_LISTENER);
                interstitialAd.setAdClickListener(CLICK_LISTENER);
                interstitialAd.showAndRender(preloadedAd);
            } else {
                super.closed();
            }
        } else {
            super.closed();
            loge("NOT LOADED");
        }
    }

    private boolean isAdLoaded() {
        synchronized (GLOBAL_INTERSTITIAL_ADS_LOCK) {
            if (GLOBAL_INTERSTITIAL_AD != null && !GLOBAL_INTERSTITIAL_AD.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
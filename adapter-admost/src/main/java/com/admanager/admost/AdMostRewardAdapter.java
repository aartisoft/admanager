package com.admanager.admost;

import android.text.TextUtils;

import androidx.annotation.Size;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.Consts;
import com.admanager.core.RewardedAdapter;

import admost.sdk.AdMostInterstitial;
import admost.sdk.listener.AdMostAdListener;

public class AdMostRewardAdapter extends RewardedAdapter {
    private Double revenue;

    private final AdMostAdListener AD_MOST_AD_LISTENER = new AdMostAdListener() {
        @Override
        public void onDismiss(String message) {
            closed();
            // It indicates that the interstitial ad is closed by clicking cross button/back button
        }

        @Override
        public void onFail(int errorCode) {
            error("code:" + errorCode + " " + com.admanager.admost.Consts.logError(errorCode));

        }

        @Override
        public void onReady(String network, int ecpm) {
            logv("onReady:" + network);
            revenue = com.admanager.admost.Consts.ecpmToRevenue(ecpm);
            loaded();
        }

        @Override
        public void onShown(String network) {
            // It indicates that the loaded interstitial ad is shown to the user.
        }

        @Override
        public void onClicked(String s) {
            // It indicates that the interstitial ad is clicked.
            clicked(s, revenue);
        }

        @Override
        public void onComplete(String s) {
            // If you are using interstitial, this callback will not be triggered.
            earnedReward(1);

        }

    };
    private String appId;
    private String zoneId;
    private String tag;
    private AdMostInterstitial ad;
    private boolean anyIdMethodCalled;

    public AdMostRewardAdapter(@Size(min = Consts.RC_KEY_SIZE) String rcEnableKey) {
        super("AdMostRewardAdapter", rcEnableKey);
    }

    public AdMostRewardAdapter withRemoteConfigId(@Size(min = Consts.RC_KEY_SIZE) String rcAppIdKey, @Size(min = Consts.RC_KEY_SIZE) String rcZoneIdKey) {
        anyIdMethodCalled = true;
        if (this.appId != null) {
            throw new IllegalStateException("You already set appId with 'withId' method.");
        }
        if (this.zoneId != null) {
            throw new IllegalStateException("You already set zoneId with 'withId' method.");
        }
        if (rcAppIdKey.matches(Consts.UUID_REGEX)) {
            throw new IllegalStateException("Use Remote Config KEY, NOT an ID!");
        }
        if (rcZoneIdKey.matches(Consts.UUID_REGEX)) {
            throw new IllegalStateException("Use Remote Config KEY, NOT an ID!");
        }
        this.appId = RemoteConfigHelper.getConfigs().getString(rcAppIdKey);
        this.zoneId = RemoteConfigHelper.getConfigs().getString(rcZoneIdKey);
        return this;
    }

    public AdMostRewardAdapter withId(@Size(min = com.admanager.admost.Consts.ID_SIZE, max = com.admanager.admost.Consts.ID_SIZE) String appId,
                                      @Size(min = com.admanager.admost.Consts.ID_SIZE, max = com.admanager.admost.Consts.ID_SIZE) String zoneId) {
        anyIdMethodCalled = true;
        if (this.appId != null) {
            throw new IllegalStateException("You already set appId with 'withRemoteConfig' method");
        }
        if (this.zoneId != null) {
            throw new IllegalStateException("You already set zoneId with 'withRemoteConfig' method");
        }
        this.appId = appId;
        this.zoneId = zoneId;
        return this;
    }

    public AdMostRewardAdapter tag(@Size(min = 1, max = 30) String tag) {
        this.tag = tag;
        return this;
    }


    @Override
    protected void init() {
        if (!anyIdMethodCalled) {
            throw new IllegalStateException("call 'withId' or 'withRemoteConfigId' method after adapter creation.");
        }
        if (isTestMode()) {
            this.appId = com.admanager.admost.Consts.TEST_APP_ID;
            this.zoneId = com.admanager.admost.Consts.TEST_VIDEO_ZONE;
        }
        if (TextUtils.isEmpty(this.appId)) {
            error("NO APP_ID FOUND!");
            return;
        }
        if (TextUtils.isEmpty(this.zoneId)) {
            error("NO ZONE FOUND!");
            return;
        }

        com.admanager.admost.Consts.initAdMost(getActivity(), appId);
        ad = new AdMostInterstitial(getActivity(), zoneId, AD_MOST_AD_LISTENER);
        ad.refreshAd(false);
    }

    @Override
    protected void destroy() {
        super.destroy();
        if (ad != null && !ad.isDestroyed()) {
            ad.destroy();
        }
        ad = null;
    }

    @Override
    protected void show() {
        if (ad != null && ad.isLoaded()) {
            if (this.tag == null) {
                loge("missing .tag()");
                ad.show();
            } else {
                ad.show(this.tag);
            }
        } else {
            closed();
            loge("NOT LOADED");
        }
    }
}
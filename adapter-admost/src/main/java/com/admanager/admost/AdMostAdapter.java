package com.admanager.admost;

import android.support.annotation.Size;
import android.text.TextUtils;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.Adapter;
import com.admanager.core.Consts;

import admost.sdk.AdMostInterstitial;
import admost.sdk.base.AdMost;
import admost.sdk.listener.AdMostAdListener;

public class AdMostAdapter extends Adapter {
    private final AdMostAdListener AD_MOST_AD_LISTENER = new AdMostAdListener() {
        @Override
        public void onDismiss(String message) {
            closed();
            // It indicates that the interstitial ad is closed by clicking cross button/back button
        }

        @Override
        public void onFail(int errorCode) {
            error("code:" + errorCode + " " + logError(errorCode));

        }

        @Override
        public void onReady(String network, int ecpm) {
            logv("onReady:" + network);
            loaded();
        }

        @Override
        public void onShown(String network) {
            // It indicates that the loaded interstitial ad is shown to the user.
        }

        @Override
        public void onClicked(String s) {
            // It indicates that the interstitial ad is clicked.
        }

        @Override
        public void onComplete(String s) {
            // If you are using interstitial, this callback will not be triggered.
        }

    };
    private String appId;
    private String zoneId;
    private String tag;
    private AdMostInterstitial ad;
    private boolean anyIdMethodCalled;

    public AdMostAdapter(@Size(min = Consts.RC_KEY_SIZE) String rcEnableKey) {
        super("AdMost", rcEnableKey);
    }

    protected static String logError(int errorCode) {
        String message;
        switch (errorCode) {
            case AdMost.AD_ERROR_NO_FILL:
                message = "AD_ERROR_NO_FILL";
                break;
            case AdMost.AD_ERROR_FREQ_CAP:
                message = "AD_ERROR_FREQ_CAP";
                break;
            case AdMost.AD_ERROR_CONNECTION:
                message = "AD_ERROR_CONNECTION";
                break;
            case AdMost.AD_ERROR_WATERFALL_EMPTY:
                message = "AD_ERROR_WATERFALL_EMPTY";
                break;
            case AdMost.AD_ERROR_FREQ_CAP_ON_SHOWN:
                message = "AD_ERROR_FREQ_CAP_ON_SHOWN";
                break;
            case AdMost.AD_ERROR_ZONE_PASSIVE:
                message = "AD_ERROR_ZONE_PASSIVE";
                break;
            case AdMost.AD_ERROR_TAG_PASSIVE:
                message = "AD_ERROR_TAG_PASSIVE";
                break;
            case AdMost.AD_ERROR_TOO_MANY_REQUEST:
                message = "AD_ERROR_TOO_MANY_REQUEST";
                break;
            default:
                message = "";
                break;
        }
        return message;
    }

    public AdMostAdapter withRemoteConfigId(@Size(min = Consts.RC_KEY_SIZE) String rcAppIdKey, @Size(min = Consts.RC_KEY_SIZE) String rcZoneIdKey) {
        anyIdMethodCalled = true;
        if (this.appId != null) {
            throw new IllegalStateException("You already set appId with 'withId' method.");
        }
        if (this.zoneId != null) {
            throw new IllegalStateException("You already set zoneId with 'withId' method.");
        }
        this.appId = RemoteConfigHelper.getConfigs().getString(rcAppIdKey);
        this.zoneId = RemoteConfigHelper.getConfigs().getString(rcZoneIdKey);
        return this;
    }

    public AdMostAdapter withId(@Size(min = com.admanager.admost.Consts.ID_SIZE, max = com.admanager.admost.Consts.ID_SIZE) String appId,
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

    public AdMostAdapter tag(@Size(min = 1, max = 30) String tag) {
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
            this.zoneId = com.admanager.admost.Consts.TEST_FULLSCREEN_ZONE;
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
        if (!ad.isDestroyed()) {
            ad.destroy();
        }
        ad = null;
    }

    @Override
    protected void show() {
        if (ad != null && ad.isLoaded()) {
            if (this.tag == null) {
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
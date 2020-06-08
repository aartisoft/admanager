package com.admanager.admost;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.IntRange;
import androidx.annotation.Size;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.BannerLoader;
import com.admanager.core.Consts;

import admost.sdk.AdMostManager;
import admost.sdk.AdMostView;
import admost.sdk.listener.AdMostViewListener;

public class AdMostBannerLoader extends BannerLoader<AdMostBannerLoader> {

    private String appId;
    private String zoneId;
    private String tag;
    private int size = AdMostManager.AD_BANNER;
    private AdMostView BANNER;

    public AdMostBannerLoader(Activity activity, LinearLayout adContainer, @Size(min = Consts.RC_KEY_SIZE) String rcEnableKey) {
        super(activity, "AdMost", adContainer, rcEnableKey);
    }

    public AdMostBannerLoader tag(@Size(min = 1, max = 30) String tag) {
        this.tag = tag;
        return this;
    }

    public AdMostBannerLoader size(@IntRange(from = 0, to = 1000) int size) {
        this.size = size;
        return this;
    }

    public void loadWithRemoteConfigId(@Size(min = Consts.RC_KEY_SIZE) String rcAppIdKey, @Size(min = Consts.RC_KEY_SIZE) String rcZoneIdKey) {
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
        load();
    }

    public void loadWithId(@Size(min = com.admanager.admost.Consts.ID_SIZE, max = com.admanager.admost.Consts.ID_SIZE) String appId,
                           @Size(min = com.admanager.admost.Consts.ID_SIZE, max = com.admanager.admost.Consts.ID_SIZE) String zoneId) {
        this.appId = appId;
        this.zoneId = zoneId;
        load();
    }

    private void load() {
        if (isTestMode()) {
            this.appId = com.admanager.admost.Consts.TEST_APP_ID;
            this.zoneId = com.admanager.admost.Consts.TEST_BANNER_ZONE;
        }

        if (TextUtils.isEmpty(this.appId)) {
            error("NO APP_ID FOUND!");
            return;
        }
        if (TextUtils.isEmpty(this.zoneId)) {
            error("NO ZONE FOUND!");
            return;
        }

        if (!super.isEnabled()) {
            return;
        }

        com.admanager.admost.Consts.initAdMost(getActivity(), appId);

        BANNER = new AdMostView(getActivity(), this.zoneId, this.size, new AdMostViewListener() {
            @Override
            public void onReady(String network, int ecpm, View adView) {
                logv("onReady:" + network);
                initContainer(adView);
            }

            @Override
            public void onFail(int errorCode) {
                error("onFail: " + errorCode);
            }

            @Override
            public void onClick(String network) {
                clicked(network);
                //Ad clicked
            }

        }, null);

        if (this.tag == null) {
            loge("missing .tag()");
            BANNER.load();
        } else {
            BANNER.load(this.tag);
        }
    }

    @Override
    public void onResume() {
        if (BANNER != null) {
            BANNER.resume();
        }
    }

    @Override
    public void onPause() {
        if (BANNER != null) {
            BANNER.pause();
        }
    }

    @Override
    public void destroy() {
        if (BANNER != null) {
            BANNER.destroy();
        }
    }
}

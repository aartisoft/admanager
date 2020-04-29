package com.admanager.admost;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Size;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.AdapterWithNative;
import com.admanager.core.Consts;

import admost.sdk.AdMostView;
import admost.sdk.AdMostViewBinder;
import admost.sdk.listener.AdMostViewListener;

public class AdMostAdapterWithNative extends AdapterWithNative<AdMostAdapterWithNative> {

    private String appId;
    private String zoneId;
    private String tag;
    private boolean anyIdMethodCalled;
    private AdMostViewBinder binder;
    private AdMostView NATIVE;
    private View adView;

    public AdMostAdapterWithNative(@Size(min = Consts.RC_KEY_SIZE) String rcEnableKey) {
        super("AdMostWithNative", rcEnableKey);
    }

    public AdMostAdapterWithNative withRemoteConfigId(@Size(min = Consts.RC_KEY_SIZE) String rcAppIdKey, @Size(min = Consts.RC_KEY_SIZE) String rcZoneIdKey) {
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

    public AdMostAdapterWithNative withId(@Size(min = com.admanager.admost.Consts.ID_SIZE, max = com.admanager.admost.Consts.ID_SIZE) String appId,
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

    public AdMostAdapterWithNative tag(@Size(min = 1, max = 30) String tag) {
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
            this.zoneId = com.admanager.admost.Consts.TEST_NATIVE_ZONE;
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
        if (binder == null) {
            binder = new AdMostViewBinder.Builder(R.layout.custom_layout_native_fullscreen)
                    .iconImageId(R.id.ad_app_icon)
                    .titleId(R.id.ad_headline)
                    .callToActionId(R.id.ad_call_to_action)
                    .textId(R.id.ad_body)
                    .attributionId(R.id.ad_attribution)
                    .mainImageId(R.id.ad_image)
                    .backImageId(R.id.ad_back)
                    .privacyIconId(R.id.ad_privacy_icon)
                    .isRoundedMode(true)
                    .build();
        }

        NATIVE = new AdMostView(getActivity(), this.zoneId, new AdMostViewListener() {
            @Override
            public void onReady(String network, int ecpm, View adView) {
                logv("onReady:" + network);
//                AdMostAdapterWithNative.super.animateView(adView);
                AdMostAdapterWithNative.this.adView = adView;
                loaded();
            }

            @Override
            public void onFail(int errorCode) {
                error("onFail: " + errorCode);
            }

            @Override
            public void onClick(String network) {
                logv("onClick:" + network);
                clicked(network);
            }

        }, binder);

        if (this.tag == null) {
            NATIVE.load();
        } else {
            NATIVE.load(this.tag);
        }
    }

    @Override
    protected void loadAdsToContainer(ViewGroup container) {
        if (adView != null) {
            if (adView.getParent() != null) {
                ((ViewGroup) adView.getParent()).removeView(adView);
            }

            if (binder.callToActionId != 0) {
                View action = adView.findViewById(binder.callToActionId);
                super.animateView(action);
            }
            container.addView(adView);

            logv("impression");
        }
    }

    @Override
    public void onResume() {
        if (NATIVE != null) {
            NATIVE.resume();
        }
    }

    @Override
    public void onPause() {
        if (NATIVE != null) {
            NATIVE.pause();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (NATIVE != null) {
            NATIVE.destroy();
        }
        NATIVE = null;
    }

    @Override
    protected void show() {
        if (binder != null && NATIVE != null && adView != null) {
            displayFullScreenNativeActivity();
        } else {
            closed();
            loge("NOT LOADED");
        }
    }
}
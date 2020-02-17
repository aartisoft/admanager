package com.admanager.admost;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.NativeLoader;

import admost.sdk.AdMostView;
import admost.sdk.AdMostViewBinder;
import admost.sdk.listener.AdMostViewListener;

public class AdMostNativeLoader extends NativeLoader<AdMostNativeLoader> {

    private String appId;
    private String zoneId;
    private String tag;
    private int layoutId = R.layout.custom_layout_native_250;
    private AdMostViewBinder binder;
    private AdMostView NATIVE;

    public AdMostNativeLoader(Activity activity, LinearLayout adContainer, @Size(min = com.admanager.core.Consts.RC_KEY_SIZE) String rcEnableKey) {
        super(activity, "AdMost", adContainer, rcEnableKey);
    }

    public AdMostNativeLoader tag(@Size(min = 1, max = 30) String tag) {
        this.tag = tag;
        return this;
    }

    public AdMostNativeLoader size(@NonNull NativeType type) {
        if (type == null) {
            throw new IllegalArgumentException("type is not allowed to be null!");
        }
        switch (type) {
            case NATIVE_BANNER:
                this.layoutId = R.layout.custom_layout_native_50;
                break;
            case NATIVE_LARGE:
                this.layoutId = R.layout.custom_layout_native_90;
                break;
            case NATIVE_XL:
            default:
                this.layoutId = R.layout.custom_layout_native_250;
                break;

        }
        return this;
    }

    public AdMostNativeLoader withCustomLayout(AdMostViewBinder binder) {
        this.binder = binder;
        return this;
    }

    public void loadWithRemoteConfigId(@Size(min = com.admanager.core.Consts.RC_KEY_SIZE) String rcAppIdKey, @Size(min = com.admanager.core.Consts.RC_KEY_SIZE) String rcZoneIdKey) {
        if (this.appId != null) {
            throw new IllegalStateException("You already set appId with 'withId' method.");
        }
        if (this.zoneId != null) {
            throw new IllegalStateException("You already set zoneId with 'withId' method.");
        }
        this.appId = RemoteConfigHelper.getConfigs().getString(rcAppIdKey);
        this.zoneId = RemoteConfigHelper.getConfigs().getString(rcZoneIdKey);
        load();
    }

    public void loadWithId(@Size(min = com.admanager.admost.Consts.ID_SIZE, max = com.admanager.admost.Consts.ID_SIZE) String appId,
                           @Size(min = com.admanager.admost.Consts.ID_SIZE, max = com.admanager.admost.Consts.ID_SIZE) String zoneId) {
        if (this.appId != null) {
            throw new IllegalStateException("You already set appId with 'withRemoteConfigId' method.");
        }
        if (this.zoneId != null) {
            throw new IllegalStateException("You already set zoneId with 'withRemoteConfigId' method.");
        }
        this.appId = appId;
        this.zoneId = zoneId;
        load();
    }

    private void load() {
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

        if (!super.isEnabled()) {
            return;
        }

        com.admanager.admost.Consts.initAdMost(getActivity(), appId);

        if (binder == null) {
            binder = new AdMostViewBinder.Builder(layoutId)
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
                initContainer(adView);
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
        if (NATIVE != null) {
            NATIVE.destroy();
        }
    }

    public enum NativeType {
        NATIVE_BANNER, NATIVE_LARGE, NATIVE_XL
    }
}

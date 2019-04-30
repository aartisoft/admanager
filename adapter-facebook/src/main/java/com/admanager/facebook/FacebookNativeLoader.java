package com.admanager.facebook;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.Consts;
import com.admanager.core.NativeLoader;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;

/**
 * Created by Gust on 20.11.2018.
 */
public class FacebookNativeLoader extends NativeLoader<FacebookNativeLoader> {
    public static final String FACEBOOK_BANNER_TEST_ID = "YOUR_PLACEMENT_ID";
    private String adUnitId;
    private NativeAd nativeAd;

    @LayoutRes
    private int layoutId = R.layout.item_face_native_ad;
    private LinearLayout linearLayout;

    public FacebookNativeLoader(Activity activity, LinearLayout adContainer, String enableKey) {
        super(activity, "Face", adContainer, enableKey);
    }

    public void loadWithRemoteConfigId(@Size(min = Consts.RC_KEY_SIZE) String rcAdUnitIdKey) {
        this.adUnitId = RemoteConfigHelper.getConfigs().getString(rcAdUnitIdKey);
        load();
    }

    public void loadWithId(@Size(min = com.admanager.facebook.Consts.AD_UNIT_SIZE_MIN, max = com.admanager.facebook.Consts.AD_UNIT_SIZE_MAX) String adUnitId) {
        this.adUnitId = adUnitId;
        load();
    }

    public FacebookNativeLoader withCustomLayout(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
        return this;
    }

    public FacebookNativeLoader size(@NonNull FacebookNativeLoader.NativeType type) {
        if (type == null) {
            throw new IllegalArgumentException("type is not allowed to be null!");
        }
        switch (type) {
            case NATIVE_BANNER:
                this.layoutId = R.layout.item_face_native_banner_ad;
                break;
            case NATIVE_LARGE:
            default:
                this.layoutId = R.layout.item_face_native_ad;
                break;
        }
        return this;
    }

    private void load() {
        if (isTestMode()) {
            this.adUnitId = FACEBOOK_BANNER_TEST_ID;
        }
        if (TextUtils.isEmpty(this.adUnitId)) {
            error("NO AD_UNIT_ID FOUND!");
            return;
        }

        linearLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(layoutId, null, false);

        if (!isEnabled()) {
            return;
        }

        nativeAd = new NativeAd(getActivity(), this.adUnitId);
        nativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                logv("onMediaDownloaded");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                error("onError: " + adError.getErrorCode() + ":" + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                logv("onAdLoaded");
                // Race condition, load() called again before last ad was displayed
                if (nativeAd == null || nativeAd != ad) {
                    error("load() called again before last ad was displayed");
                    return;
                }

                FacebookAdHelper.populateNativeAd(nativeAd, linearLayout);
                initContainer(linearLayout);
            }

            @Override
            public void onAdClicked(Ad ad) {
                logv("onAdClicked");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                logv("onLoggingImpression");
            }
        });
        nativeAd.loadAd();
    }

    public enum NativeType {
        NATIVE_BANNER, NATIVE_LARGE
    }
}

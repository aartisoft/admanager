package com.admanager.mopub;


import android.app.Activity;
import android.support.annotation.Size;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.BannerLoader;
import com.admanager.core.Consts;
import com.mopub.common.MediationSettings;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.mobileads.DefaultBannerAdListener;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

import java.util.ArrayList;
import java.util.List;

public class MopubBannerLoader extends BannerLoader {

    private static final String TAG = "MopubBannerLoader";


    private String adUnitId;
    private LinearLayout adContainer;
    private MoPubView moPubView;

    public MopubBannerLoader(Activity activity, LinearLayout adContainer, @Size(min = Consts.RC_KEY_SIZE) String rcEnableKey) {
        super(activity, rcEnableKey);
        this.adContainer = adContainer;
    }

    public void loadWithRemoteConfigId(@Size(min = Consts.RC_KEY_SIZE) String rcAdUnitIdKey) {
        this.adUnitId = RemoteConfigHelper.getConfigs().getString(rcAdUnitIdKey);
        load();
    }

    public void loadWithId(@Size(min = 32, max = 32) String adUnitId) {
        this.adUnitId = adUnitId;
        load();
    }

    private void load() {
        if (TextUtils.isEmpty(this.adUnitId)) {
            adContainer.setVisibility(View.GONE);
            error("NO AD_UNIT_ID FOUND!");
            return;
        }

        if (!super.isEnabled()) {
            adContainer.setVisibility(View.GONE);
            return;
        }

        if (!MoPub.isSdkInitialized()) {
            // A list of rewarded video adapters to initialize
            List<String> networksToInit = new ArrayList<>();
            networksToInit.add("com.mopub.mobileads.VungleRewardedVideo");

            SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(adUnitId)
                    .withMediationSettings(new Settings())
                    .withNetworksToInit(networksToInit)
                    .build();

            MoPub.initializeSdk(getActivity(), sdkConfiguration, new SdkInitializationListener() {
                @Override
                public void onInitializationFinished() {
                    _load();
                }
            });
        } else {
            _load();
        }
    }

    private void _load() {
        adContainer.setVisibility(View.VISIBLE);
        adContainer.removeAllViews();


        moPubView = new MoPubView(getActivity());
        moPubView.setAdUnitId(adUnitId);
        moPubView.setBannerAdListener(new DefaultBannerAdListener() {
            @Override
            public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
                Log.e(TAG, "onBannerFailed: " + errorCode);
                moPubView.setVisibility(View.GONE);
                adContainer.setVisibility(View.GONE);
            }

            @Override
            public void onBannerLoaded(MoPubView banner) {
                Log.d(TAG, "onBannerLoaded");
                moPubView.setVisibility(View.VISIBLE);
                adContainer.setVisibility(View.VISIBLE);
                adContainer.removeAllViews();
                if (banner.getParent() != null)
                    ((ViewGroup) banner.getParent()).removeView(banner);
                adContainer.addView(banner);
            }
        });
        moPubView.loadAd();
    }

    @Override
    public void destroy() {
        if (moPubView != null) {
            moPubView.destroy();
        }
    }

    private class Settings implements MediationSettings {

    }
}

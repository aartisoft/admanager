package com.admanager.mopub;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Size;

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

public class MopubBannerLoader extends BannerLoader<MopubBannerLoader> {

    public static final String MOPUB_BANNER_TEST_ID = "b195f8dd8ded45fe847ad89ed1d016da";

    private String adUnitId;
    private MoPubView moPubView;

    public MopubBannerLoader(Activity activity, LinearLayout adContainer, @Size(min = Consts.RC_KEY_SIZE) String rcEnableKey) {
        super(activity, "Mopub", adContainer, rcEnableKey);
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
        if (isTestMode()) {
            this.adUnitId = MOPUB_BANNER_TEST_ID;
        }
        if (TextUtils.isEmpty(this.adUnitId)) {
            error("NO AD_UNIT_ID FOUND!");
            return;
        }

        if (!super.isEnabled()) {
            return;
        }

        if (!MoPub.isSdkInitialized()) {
            // A list of rewarded video adapters to initialize
//            List<String> networksToInit = new ArrayList<>();
//            networksToInit.add("com.mopub.mobileads.VungleRewardedVideo");

            SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(adUnitId)
                    .withMediationSettings(new Settings())
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
        moPubView = new MoPubView(getActivity());
        if (isTestMode()) {
            moPubView.setTesting(true);
        }
        moPubView.setAdUnitId(adUnitId);
        moPubView.setBannerAdListener(new DefaultBannerAdListener() {
            @Override
            public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
                error("onBannerFailed: " + errorCode);
                moPubView.setVisibility(View.GONE);
            }

            @Override
            public void onBannerLoaded(MoPubView banner) {
                logv("onBannerLoaded");
                moPubView.setVisibility(View.VISIBLE);
                initContainer(banner);
            }

            @Override
            public void onBannerClicked(MoPubView banner) {
                logv("onBannerClicked");
                clicked();
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

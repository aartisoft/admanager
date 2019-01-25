package com.admanager.facebook;

import android.app.Activity;
import android.support.annotation.Size;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.BannerLoader;
import com.admanager.core.Consts;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;


/**
 * Created by Gust on 20.11.2018.
 */
public class FacebookBannerLoader extends BannerLoader<FacebookBannerLoader> {
    private static final String TAG = "MyFacebookHelper";
    private String adUnitId;
    private AdView adView;

    public FacebookBannerLoader(Activity activity, LinearLayout adContainer, String enableKey) {
        super(activity, adContainer, enableKey);
    }

    public void loadWithRemoteConfigId(@Size(min = Consts.RC_KEY_SIZE) String rcAdUnitIdKey) {
        this.adUnitId = RemoteConfigHelper.getConfigs().getString(rcAdUnitIdKey);
        load();
    }

    public void loadWithId(@Size(min = com.admanager.facebook.Consts.AD_UNIT_SIZE_MIN, max = com.admanager.facebook.Consts.AD_UNIT_SIZE_MAX) String adUnitId) {
        this.adUnitId = adUnitId;
        load();
    }


    private void load() {
        if (TextUtils.isEmpty(this.adUnitId)) {
            error("NO AD_UNIT_ID FOUND!");
            return;
        }

        if (!isEnabled()) {
            return;
        }


        com.facebook.ads.AdView adView = new com.facebook.ads.AdView(getActivity(), adUnitId, AdSize.BANNER_HEIGHT_50);
        initContainer(adView);

        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                error("onError: " + adError.getErrorCode() + ":" + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        adView.loadAd();
        this.adView = adView;
    }

    @Override
    public void destroy() {
        if (adView != null) {
            adView.destroy();
        }
        adView = null;
    }
}

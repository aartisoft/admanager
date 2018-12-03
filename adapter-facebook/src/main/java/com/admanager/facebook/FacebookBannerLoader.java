package com.admanager.facebook;

import android.app.Activity;
import android.support.annotation.Size;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
public class FacebookBannerLoader extends BannerLoader {
    private static final String TAG = "MyFacebookHelper";
    private String adUnitId;
    private LinearLayout adContainer;
    private AdView adView;

    public FacebookBannerLoader(Activity activity, LinearLayout adContainer, String enableKey) {
        super(activity, enableKey);
        this.adContainer = adContainer;
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
            adContainer.setVisibility(View.GONE);
//            if (placeholder != null) placeholder.setVisibility(View.GONE);
            error("NO AD_UNIT_ID FOUND!");
            return;
        }

        if (!isEnabled()) {
            adContainer.setVisibility(View.GONE);
//            if (placeholder != null) placeholder.setVisibility(View.GONE);
            return;
        }
        adContainer.setVisibility(View.VISIBLE);

//        if (placeholder != null) placeholder.setVisibility(View.VISIBLE);
//        if (placeholder != null)
//            placeholder.setMinimumHeight((int) Utils.convertDpToPixel(50, getActivity()));
//        if (placeholder != null) placeholder.setBackgroundColor(Color.TRANSPARENT);

        com.facebook.ads.AdView adView = new com.facebook.ads.AdView(getActivity(), adUnitId, AdSize.BANNER_HEIGHT_50);

        adContainer.removeAllViews();
        adContainer.addView(adView);

        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e(TAG, "onError: " + adError.getErrorCode() + ":" + adError.getErrorMessage());
                adContainer.setVisibility(View.GONE);
//                if (placeholder != null) placeholder.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded(Ad ad) {
//                if (placeholder != null) placeholder.setVisibility(View.GONE);
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

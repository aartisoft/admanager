package com.admanager.admob;


import android.app.Activity;
import android.support.annotation.Size;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.BannerLoader;
import com.admanager.core.Consts;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class AdmobBannerLoader extends BannerLoader {

    private static final String TAG = "AdmobBannerLoader";


    private String adUnitId;
    private LinearLayout adContainer;

    public AdmobBannerLoader(Activity activity, LinearLayout adContainer, @Size(min = Consts.RC_KEY_SIZE) String rcEnableKey) {
        super(activity, rcEnableKey);
        this.adContainer = adContainer;
    }

    public void loadWithRemoteConfigId(@Size(min = Consts.RC_KEY_SIZE) String rcAdUnitIdKey) {
        this.adUnitId = RemoteConfigHelper.getConfigs().getString(rcAdUnitIdKey);
        load();
    }

    public void loadWithId(@Size(min = com.admanager.admob.Consts.AD_UNIT_SIZE_MIN, max = com.admanager.admob.Consts.AD_UNIT_SIZE_MAX) String adUnitId) {
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
        adContainer.setVisibility(View.VISIBLE);
        adContainer.removeAllViews();


        AdView mAdView = new AdView(getActivity());
        mAdView.setAdUnitId(adUnitId);
        mAdView.setAdSize(AdSize.SMART_BANNER);

        AdRequest.Builder AdmobBanner = new AdRequest.Builder();
        AdRequest adRequest = AdmobBanner.build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                adContainer.setVisibility(View.GONE);
                Log.e(TAG, "onAdFailedToLoad: " + i);
            }

            @Override
            public void onAdLoaded() {

            }
        });
        mAdView.loadAd(adRequest);
        adContainer.addView(mAdView);
    }
}

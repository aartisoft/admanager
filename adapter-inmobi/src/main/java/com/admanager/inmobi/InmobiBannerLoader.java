package com.admanager.inmobi;


import android.app.Activity;
import android.support.annotation.Size;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.BannerLoader;
import com.admanager.core.Consts;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.ads.listeners.BannerAdEventListener;
import com.inmobi.sdk.InMobiSdk;

public class InmobiBannerLoader extends BannerLoader {

    private static final String TAG = "InmobiBannerLoader";


    private long adUnitId;
    private String accountId;
    private LinearLayout adContainer;
    private InMobiBanner inMobiBanner;

    public InmobiBannerLoader(Activity activity, LinearLayout adContainer, @Size(min = Consts.RC_KEY_SIZE) String rcEnableKey) {
        super(activity, rcEnableKey);
        this.adContainer = adContainer;
    }

    public void loadWithRemoteConfigId(@Size(min = Consts.RC_KEY_SIZE) String rcAccountIdKey, @Size(min = Consts.RC_KEY_SIZE) String rcAdUnitIdKey) {
        this.adUnitId = RemoteConfigHelper.getConfigs().getLong(rcAdUnitIdKey);
        this.accountId = RemoteConfigHelper.getConfigs().getString(rcAccountIdKey);
        load();
    }

    public void loadWithId(@Size(min = 32, max = 32) String accountId, @Size(min = 100000000000L, max = 99999999999999L) long adUnitId) {
        this.adUnitId = adUnitId;
        this.accountId = accountId;
        load();
    }

    private void load() {
        if (adUnitId == 0) {
            adContainer.setVisibility(View.GONE);
            error("NO AD_UNIT_ID FOUND!");
            return;
        }

        if (!super.isEnabled()) {
            adContainer.setVisibility(View.GONE);
            return;
        }

        InMobiSdk.init(getActivity(), accountId);

        adContainer.setVisibility(View.VISIBLE);
        adContainer.removeAllViews();

        ViewGroup.LayoutParams params = adContainer.getLayoutParams();
        params.height = (int) (50 * ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        adContainer.setLayoutParams(params);

        inMobiBanner = new InMobiBanner(getActivity(), this.adUnitId);
        inMobiBanner.setBannerSize(320, 50);
        inMobiBanner.setListener(new BannerAdEventListener() {
            @Override
            public void onAdLoadSucceeded(InMobiBanner inMobiBanner) {
                Log.d(TAG, "onAdLoadSucceeded");
                adContainer.setVisibility(View.VISIBLE);
                adContainer.removeAllViews();
                if (inMobiBanner.getParent() != null)
                    ((ViewGroup) inMobiBanner.getParent()).removeView(inMobiBanner);
                adContainer.addView(inMobiBanner);
            }

            @Override
            public void onAdLoadFailed(InMobiBanner inMobiBanner, InMobiAdRequestStatus inMobiAdRequestStatus) {
                adContainer.setVisibility(View.GONE);
                Log.e(TAG, "onAdLoadFailed: " + (inMobiAdRequestStatus != null ? inMobiAdRequestStatus.getMessage() : ""));
            }
        });
        inMobiBanner.load();
    }

}

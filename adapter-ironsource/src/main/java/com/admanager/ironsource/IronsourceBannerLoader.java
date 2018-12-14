package com.admanager.ironsource;

import android.app.Activity;
import android.support.annotation.Size;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.admanager.core.BannerLoader;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.BannerListener;


/**
 * Created by Gust on 20.11.2018.
 */
public class IronsourceBannerLoader extends BannerLoader {
    private static final String TAG = "IronsourceBannerLoader";
    private String placement;
    private String appKey;
    private LinearLayout adContainer;
    private IronSourceBannerLayout banner;

    public IronsourceBannerLoader(Activity activity, LinearLayout adContainer, String enableKey) {
        super(activity, enableKey);
        this.adContainer = adContainer;
    }

    public IronsourceBannerLoader withId(@Size(min = 5, max = 10) String appKey, @Size(min = 2, max = 20) String placement) {
        this.appKey = appKey;
        this.placement = placement;
        load();
        return this;
    }


    private void load() {
        if (TextUtils.isEmpty(this.appKey)) {
            adContainer.setVisibility(View.GONE);
            error("NO AD_UNIT_ID FOUND!");
            return;
        }

        if (!isEnabled()) {
            adContainer.setVisibility(View.GONE);
            return;
        }

        IronSource.init(getActivity(), this.appKey, IronSource.AD_UNIT.BANNER);


        adContainer.setVisibility(View.VISIBLE);

        banner = IronSource.createBanner(getActivity(), ISBannerSize.SMART);

        banner.setBannerListener(new BannerListener() {
            @Override
            public void onBannerAdLoaded() {

            }

            @Override
            public void onBannerAdLoadFailed(IronSourceError ironSourceError) {
                Log.e(TAG, "onError: " + ironSourceError.getErrorCode() + ":" + ironSourceError.getErrorMessage());
                if (getActivity() != null && !getActivity().isFinishing()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adContainer.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onBannerAdClicked() {

            }

            @Override
            public void onBannerAdScreenPresented() {

            }

            @Override
            public void onBannerAdScreenDismissed() {

            }

            @Override
            public void onBannerAdLeftApplication() {

            }
        });

        adContainer.removeAllViews();
        adContainer.addView(banner);

        if (placement == null) {
            IronSource.loadBanner(banner);
        } else {
            IronSource.loadBanner(banner, placement);
        }
    }

    @Override
    public void destroy() {
        if (banner != null) {
            IronSource.destroyBanner(banner);
        }
        banner = null;
    }
}

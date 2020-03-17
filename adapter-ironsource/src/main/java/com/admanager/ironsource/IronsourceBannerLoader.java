package com.admanager.ironsource;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.LinearLayout;

import androidx.annotation.Size;

import com.admanager.core.BannerLoader;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.BannerListener;


/**
 * Created by Gust on 20.11.2018.
 */
public class IronsourceBannerLoader extends BannerLoader<IronsourceBannerLoader> {
    private String placement;
    private String appKey;
    private IronSourceBannerLayout banner;

    public IronsourceBannerLoader(Activity activity, LinearLayout adContainer, String enableKey) {
        super(activity, "Ironsrc", adContainer, enableKey);
    }

    public void withId(@Size(min = 5, max = 10) String appKey, @Size(min = 2, max = 20) String placement) {
        this.appKey = appKey;
        this.placement = placement;
        load();
    }


    private void load() {
        if (TextUtils.isEmpty(this.appKey)) {
            error("NO AD_UNIT_ID FOUND!");
            return;
        }

        if (!isEnabled()) {
            return;
        }

        IronSource.init(getActivity(), this.appKey, IronSource.AD_UNIT.BANNER);

        banner = IronSource.createBanner(getActivity(), ISBannerSize.SMART);
        banner.setBannerListener(new BannerListener() {
            @Override
            public void onBannerAdLoaded() {
                logv("onBannerAdLoaded");
                initContainer(banner);
            }

            @Override
            public void onBannerAdLoadFailed(IronSourceError ironSourceError) {
                error("onError: " + ironSourceError.getErrorCode() + ":" + ironSourceError.getErrorMessage());
            }

            @Override
            public void onBannerAdClicked() {
                logv("onBannerAdClicked");
                clicked();
            }

            @Override
            public void onBannerAdScreenPresented() {
                logv("onBannerAdScreenPresented");
            }

            @Override
            public void onBannerAdScreenDismissed() {
                logv("onBannerAdScreenDismissed");
            }

            @Override
            public void onBannerAdLeftApplication() {
                logv("onBannerAdLeftApplication");
            }
        });

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

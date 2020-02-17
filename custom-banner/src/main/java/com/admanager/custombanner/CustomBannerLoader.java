package com.admanager.custombanner;

import android.app.Activity;
import android.support.annotation.Size;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.BannerLoader;
import com.admanager.core.Consts;
import com.admanager.custombanner.view.CustomBanner;

/**
 * Created by Gust on 20.11.2018.
 */
public class CustomBannerLoader extends BannerLoader<CustomBannerLoader> {

    public static final String CUSTOM_BANNER_TEST_TARGET_URL = "https://play.google.com/store/apps/details?id=com.whatsapp";
    public static final String CUSTOM_BANNER_TEST_IMAGE_URL = "https://image.oaking.tk/raw/47q05krqsp.gif";

    private String targetUrl;
    private String imageUrl;

    public CustomBannerLoader(Activity activity, LinearLayout adContainer, @Size(min = Consts.RC_KEY_SIZE) String rcEnableKey) {
        super(activity, "Custom", adContainer, rcEnableKey);
    }

    public void loadWithRemoteConfigId(@Size(min = Consts.RC_KEY_SIZE) String rcTargetUrlKey, @Size(min = Consts.RC_KEY_SIZE) String rcImageUrlKey) {
        this.targetUrl = RemoteConfigHelper.getConfigs().getString(rcTargetUrlKey);
        this.imageUrl = RemoteConfigHelper.getConfigs().getString(rcImageUrlKey);
        load();
    }

    public void loadWithId(@Size(min = 8) String url, @Size(min = 8) String image) {
        this.targetUrl = url;
        this.imageUrl = image;
        load();
    }

    private void load() {
        if (isTestMode() && TextUtils.isEmpty(this.targetUrl)) {
            this.targetUrl = CUSTOM_BANNER_TEST_TARGET_URL;
        }
        if (isTestMode() && TextUtils.isEmpty(this.imageUrl)) {
            this.imageUrl = CUSTOM_BANNER_TEST_IMAGE_URL;
        }
        if (TextUtils.isEmpty(this.targetUrl) || TextUtils.isEmpty(this.imageUrl)) {
            error("NO targetUrl or IMAGE FOUND!");
            return;
        }

        if (!super.isEnabled()) {
            return;
        }

        final CustomBanner customBanner = new CustomBanner(getActivity());
        initContainer(customBanner);
        customBanner.setEnable(true);
        customBanner.setImageUrl(this.imageUrl);
        customBanner.setTargetUrl(this.targetUrl);
        customBanner.setAdListener(new CustomBanner.AdListener() {
            @Override
            public void onClick(String url) {
                logv("onClick: " + url);
                clicked();
            }

            @Override
            public void onLoaded(String imageUrl) {
                logv("onLoaded: " + imageUrl);
            }

            @Override
            public void onError(String error) {
                error("onError: " + error);
            }
        });
        customBanner.load();
    }
}

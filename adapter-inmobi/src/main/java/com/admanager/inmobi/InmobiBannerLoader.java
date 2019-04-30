package com.admanager.inmobi;

import android.app.Activity;
import android.support.annotation.Size;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.BannerLoader;
import com.admanager.core.Consts;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.ads.listeners.BannerAdEventListener;
import com.inmobi.sdk.InMobiSdk;

public class InmobiBannerLoader extends BannerLoader<InmobiBannerLoader> {
    public static final long INMOBI_BANNER_TEST_ID = 1547483178070L;
    public static final String INMOBI_ACCOUNT_TEST_ID = "0ebbbd7c75be47feb316d4dde2017b89";

    private long adUnitId;
    private String accountId;
    private InMobiBanner inMobiBanner;

    public InmobiBannerLoader(Activity activity, LinearLayout adContainer, @Size(min = Consts.RC_KEY_SIZE) String rcEnableKey) {
        super(activity, "Inmobi", adContainer, rcEnableKey);
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
        if (isTestMode()) {
            this.adUnitId = INMOBI_BANNER_TEST_ID;
            this.accountId = INMOBI_ACCOUNT_TEST_ID;
        }
        if (adUnitId == 0) {
            error("NO AD_UNIT_ID FOUND!");
            return;
        }

        if (!super.isEnabled()) {
            return;
        }

        InMobiSdk.init(getActivity(), accountId);

        initContainer();

        ViewGroup.LayoutParams params = getAdContainer().getLayoutParams();
        params.height = (int) (50 * ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getAdContainer().setLayoutParams(params);

        inMobiBanner = new InMobiBanner(getActivity(), this.adUnitId);
        inMobiBanner.setBannerSize(320, 50);
        inMobiBanner.setListener(new BannerAdEventListener() {
            @Override
            public void onAdLoadSucceeded(InMobiBanner inMobiBanner) {
                logv("onAdLoadSucceeded");
                initContainer(inMobiBanner);
            }

            @Override
            public void onAdLoadFailed(InMobiBanner inMobiBanner, InMobiAdRequestStatus inMobiAdRequestStatus) {
                error("onAdLoadFailed: " + (inMobiAdRequestStatus != null ? inMobiAdRequestStatus.getMessage() : ""));
            }
        });
        inMobiBanner.load();
    }

}

package com.admanager.admob;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.RewardedAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class AdmobRewardedAdapter extends RewardedAdapter {
    public static final String ADMOB_REWARDED_TEST_ID = "ca-app-pub-3940256099942544/5224354917";
    private final String TAG = "ADM_REWARDED";

    private String adUnitId;
    private boolean anyIdMethodCalled;
    private RewardedAd rewardedAd;
    private RewardedAdLoadCallback callback = new RewardedAdLoadCallback() {
        @Override
        public void onRewardedAdFailedToLoad(int i) {
            logv("onRewardedAdFailedToLoad");
            error("code: " + i);
        }

        @Override
        public void onRewardedAdLoaded() {
            logv("onRewardedAdLoaded");
            loaded();
        }
    };
    private RewardedAdCallback rewardedAdCallback = new RewardedAdCallback() {
        @Override
        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
            logv("onUserEarnedReward");
            earnedReward(rewardItem.getAmount());
        }

        @Override
        public void onRewardedAdOpened() {
            logv("onRewardedAdOpened");
        }

        @Override
        public void onRewardedAdClosed() {
            logv("onRewardedAdClosed");
            closed();
        }

        @Override
        public void onRewardedAdFailedToShow(int i) {
            // Ad failed to display.
            loge("onRewardedAdFailedToShow");
            closed();
        }
    };

    public AdmobRewardedAdapter(@Size(min = com.admanager.core.Consts.RC_KEY_SIZE) String enableKey) {
        super("RewardedAdmob", enableKey);
    }

    public RewardedAdapter withRemoteConfigId(@Size(min = com.admanager.core.Consts.RC_KEY_SIZE) String rcAdUnitIdKey) {
        anyIdMethodCalled = true;
        if (this.adUnitId != null) {
            throw new IllegalStateException("You already set adUnitId with 'withId' method.");
        }
        this.adUnitId = RemoteConfigHelper.getConfigs().getString(rcAdUnitIdKey);
        return this;
    }

    public RewardedAdapter withId(@Size(min = com.admanager.admob.Consts.AD_UNIT_SIZE_MIN, max = com.admanager.admob.Consts.AD_UNIT_SIZE_MAX) String adUnitId) {
        anyIdMethodCalled = true;
        if (this.adUnitId != null) {
            throw new IllegalStateException("You already set adUnitId with 'withRemoteConfig' method");
        }
        this.adUnitId = adUnitId;
        return this;
    }

    @Override
    protected void show() {
        if (this.rewardedAd != null && this.rewardedAd.isLoaded()) {
            this.rewardedAd.show(getActivity(), rewardedAdCallback);
        } else {
            closed();
            loge("NOT LOADED");
        }
    }

    @Override
    protected void init() {
        if (isTestMode()) {
            this.adUnitId = ADMOB_REWARDED_TEST_ID;
        }
        if (!anyIdMethodCalled) {
            throw new IllegalStateException("call 'withId' or 'withRemoteConfigId' method after adapter creation.");
        }
        if (TextUtils.isEmpty(this.adUnitId)) {
            error("NO AD_UNIT_ID FOUND!");
            return;
        }

        MobileAds.initialize(getActivity());

        this.rewardedAd = new RewardedAd(getActivity(), this.adUnitId);

        AdRequest request = new AdRequest.Builder().build();
        this.rewardedAd.loadAd(request, callback);
    }

    @Override
    protected void destroy() {
        super.destroy();
        this.rewardedAd = null;
    }

}

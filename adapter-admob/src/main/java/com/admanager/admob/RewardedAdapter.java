package com.admanager.admob;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.Adapter;
import com.admanager.core.Consts;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public abstract class RewardedAdapter extends Adapter {
    private final String ADMOB_REWARDED_TEST_ID = "ca-app-pub-3940256099942544/5224354917";
    private final String TAG = "ADM_REWARDED";
    private String adUnitId;
    private boolean anyIdMethodCalled;
    private RewardedAd rewardedAd;
    private UserRewardedListener userRewardedListener;

    public RewardedAdapter(@Size(min = Consts.RC_KEY_SIZE) String enableKey) {
        super("Rewarded", enableKey);
    }

    public RewardedAdapter withRemoteConfigId(@Size(min = Consts.RC_KEY_SIZE) String rcAdUnitIdKey, UserRewardedListener userRewardedListener) {
        this.userRewardedListener = userRewardedListener;
        anyIdMethodCalled = true;
        if (this.adUnitId != null) {
            throw new IllegalStateException("You already set adUnitId with 'withId' method.");
        }
        this.adUnitId = RemoteConfigHelper.getConfigs().getString(rcAdUnitIdKey);
        return this;
    }

    public RewardedAdapter withId(@Size(min = com.admanager.admob.Consts.AD_UNIT_SIZE_MIN, max = com.admanager.admob.Consts.AD_UNIT_SIZE_MAX) String adUnitId, UserRewardedListener userRewardedListener) {
        this.userRewardedListener = userRewardedListener;
        anyIdMethodCalled = true;
        if (this.adUnitId != null) {
            throw new IllegalStateException("You already set adUnitId with 'withRemoteConfig' method");
        }
        this.adUnitId = adUnitId;
        return this;
    }

    @Override
    protected void show() {
        if (this.rewardedAd.isLoaded()) {
            RewardedAdCallback rewardedAdCallback = new RewardedAdCallback() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    Log.i(TAG, "onUserEarnedReward");
                    userRewardedListener.onUserRewarded();
                }

                @Override
                public void onRewardedAdOpened() {
                    super.onRewardedAdOpened();
                    Log.i(TAG, "onRewardedAdOpened");
                }

                @Override
                public void onRewardedAdClosed() {
                    super.onRewardedAdClosed();
                    Log.i(TAG, "onRewardedAdClosed");
                }

                @Override
                public void onRewardedAdFailedToShow(int i) {
                    super.onRewardedAdFailedToShow(i);
                    Log.i(TAG, "onRewardedAdFailedToShow, err : " + i);
                }
            };
            this.rewardedAd.show(getActivity(), rewardedAdCallback);
        } else {
            Log.i(TAG, "Ad not loaded!");
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

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        this.rewardedAd = new RewardedAd(getActivity(), ADMOB_REWARDED_TEST_ID);
        RewardedAdLoadCallback callback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdFailedToLoad(int i) {
                super.onRewardedAdFailedToLoad(i);
                Log.i(TAG, "onRewardedAdFailedToLoad, err: " + i);
            }

            @Override
            public void onRewardedAdLoaded() {
                super.onRewardedAdLoaded();
                Log.i(TAG, "onRewardedAdLoaded");
            }
        };

        this.rewardedAd.loadAd(new AdRequest.Builder().build(), callback);
    }

    @Override
    protected void destroy() {
        super.destroy();
        this.rewardedAd = null;
    }

    public interface UserRewardedListener {
        void onUserRewarded();
    }
}

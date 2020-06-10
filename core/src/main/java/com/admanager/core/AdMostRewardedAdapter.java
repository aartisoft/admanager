package com.admanager.core;

public class AdMostRewardedAdapter extends Adapter {

    UserRewardedListener userRewardedListener;

    public AdMostRewardedAdapter(String adapterName, String enableKey) {
        super(adapterName, enableKey);
    }

    protected final void earnedReward() {
        if (userRewardedListener != null) {
            userRewardedListener.onRewarded();
        }
    }

    public AdMostRewardedAdapter setUserRewardedListener(UserRewardedListener userRewardedListener) {
        this.userRewardedListener = userRewardedListener;
        return this;
    }

    @Override
    protected void show() {

    }

    @Override
    protected void init() {

    }

    public interface UserRewardedListener {
        void onRewarded();
    }
}

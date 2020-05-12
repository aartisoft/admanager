package com.admanager.core;

public abstract class RewardedAdapter extends Adapter {
    private UserRewardedListener userRewardedListener;

    public RewardedAdapter(String adapterName, String enableKey) {
        super(adapterName, enableKey);
    }

    protected final void earnedReward(int amount) {
        if (userRewardedListener != null) {
            userRewardedListener.onUserRewarded(amount);
        }
    }

    public RewardedAdapter setUserRewardedListener(UserRewardedListener userRewardedListener) {
        this.userRewardedListener = userRewardedListener;
        return this;
    }

    public interface UserRewardedListener {
        void onUserRewarded(int amount);
    }
}

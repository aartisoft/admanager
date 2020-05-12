package com.admanager.admob;

public class AdmobRewardedAdapter extends RewardedAdapter {
    public AdmobRewardedAdapter(String enableKey) {
        super(enableKey);
    }

    @Override
    public RewardedAdapter withRemoteConfigId(String rcAdUnitIdKey, UserRewardedListener userRewardedListener) {
        return super.withRemoteConfigId(rcAdUnitIdKey, userRewardedListener);
    }

    @Override
    public RewardedAdapter withId(String adUnitId, UserRewardedListener userRewardedListener) {
        return super.withId(adUnitId, userRewardedListener);
    }

    @Override
    protected void show() {
        super.show();
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void destroy() {
        super.destroy();
    }
}

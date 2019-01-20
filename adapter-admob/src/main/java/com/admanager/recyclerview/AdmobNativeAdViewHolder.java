package com.admanager.recyclerview;


import android.view.View;

import com.admanager.admob.AdmobAdHelper;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;


class AdmobNativeAdViewHolder extends BindableAdmobAdViewHolder {
    private UnifiedNativeAdView mAdView;

    AdmobNativeAdViewHolder(View itemView) {
        super(itemView);
        mAdView = (UnifiedNativeAdView) this.itemView;
    }

    protected void bindTo(UnifiedNativeAd unifiedNativeAd) {
        if (unifiedNativeAd == null) {
            mAdView.setVisibility(View.GONE);
            return;
        }

        mAdView.setVisibility(View.VISIBLE);
        AdmobAdHelper.populateUnifiedNativeAdView(unifiedNativeAd, mAdView);
    }
}

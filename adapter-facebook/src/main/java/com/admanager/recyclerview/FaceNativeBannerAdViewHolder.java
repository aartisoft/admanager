package com.admanager.recyclerview;

import android.view.View;
import android.widget.LinearLayout;

import com.admanager.facebook.FacebookAdHelper;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;

public class FaceNativeBannerAdViewHolder extends BindableFaceAdViewHolder {
    private LinearLayout adView;

    FaceNativeBannerAdViewHolder(View itemView) {
        super(itemView);
        adView = (LinearLayout) this.itemView;
    }

    @Override
    public void bindTo(NativeAdsManager manager) {
        // Inflate Native Banner Ad into Container
        if (manager == null) {
            itemView.setVisibility(View.GONE);
            return;
        }

        NativeAd nativeAd = manager.nextNativeAd();

        FacebookAdHelper.populateNativeAd(nativeAd, adView);
    }
}

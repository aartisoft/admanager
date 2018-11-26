package com.admanager.recyclerview;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.admanager.facebook.R;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;

import java.util.ArrayList;
import java.util.List;


class FaceNativeBannerAdViewHolder extends RecyclerView.ViewHolder {
    private RelativeLayout adChoicesContainer;
    private TextView nativeAdTitle;
    private TextView nativeAdSocialContext;
    private AdIconView nativeAdIcon;
    private TextView sponsoredLabel;
    private Button nativeAdCallToAction;
    private LinearLayout adView;

    FaceNativeBannerAdViewHolder(View itemView) {
        super(itemView);
        adView = (LinearLayout) this.itemView;
        nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);
    }

    void bindTo(NativeAdsManager manager) {
        // Inflate Native Banner Ad into Container
        if (manager == null) {
            itemView.setVisibility(View.GONE);
            return;
        }

        NativeAd nativeAd = manager.nextNativeAd();

        if (nativeAd == null) {
            itemView.setVisibility(View.GONE);
            return;
        }

        itemView.setVisibility(View.VISIBLE);

        nativeAd.unregisterView();
        // Add the AdChoices icon
        AdOptionsView adOptionsView = new AdOptionsView(itemView.getContext(), nativeAd, null);
        adChoicesContainer.addView(adOptionsView, 0);


        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adView,
                nativeAdIcon,
                clickableViews);
    }
}

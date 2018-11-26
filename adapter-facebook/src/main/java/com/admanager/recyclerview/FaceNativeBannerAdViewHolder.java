package com.admanager.recyclerview;


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


public class FaceNativeBannerAdViewHolder extends BindableFaceAdViewHolder {
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

        String alert = "You should use View with id '%s' in layout file.";
        if (nativeAdIcon == null)
            throw new IllegalStateException(String.format(alert, "native_ad_icon"));
        if (adChoicesContainer == null)
            throw new IllegalStateException(String.format(alert, "ad_choices_container"));
        if (nativeAdTitle == null)
            throw new IllegalStateException(String.format(alert, "native_ad_title"));
        if (nativeAdSocialContext == null)
            throw new IllegalStateException(String.format(alert, "native_ad_social_context"));
        if (sponsoredLabel == null)
            throw new IllegalStateException(String.format(alert, "native_ad_sponsored_label"));
        if (nativeAdCallToAction == null)
            throw new IllegalStateException(String.format(alert, "native_ad_call_to_action"));
    }

    @Override
    public void bindTo(NativeAdsManager manager) {
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

        AdOptionsView adOptionsView = new AdOptionsView(itemView.getContext(), nativeAd, null);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeAd.registerViewForInteraction(adView, nativeAdIcon, clickableViews);
    }
}

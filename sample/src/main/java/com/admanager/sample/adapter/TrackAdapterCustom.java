package com.admanager.sample.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.recyclerview.BaseAdapterWithFaceNative;
import com.admanager.recyclerview.BindableFaceAdViewHolder;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;

import java.util.ArrayList;
import java.util.List;

public class TrackAdapterCustom extends BaseAdapterWithFaceNative<TrackModel, TrackViewHolder> {

    public TrackAdapterCustom(Activity activity) {
        super(activity, TrackViewHolder.class, R.layout.item_track, null, showNative(), getNativeId());
    }

    private static boolean showNative() {
        return RemoteConfigHelper.getConfigs().getBoolean(RCUtils.NATIVE_FACEBOOK_ENABLED) && RemoteConfigHelper.areAdsEnabled();
    }

    private static String getNativeId() {
        return RemoteConfigHelper.getConfigs().getString(RCUtils.NATIVE_FACEBOOK_ID);
    }

    @NonNull
    @Override
    public NativeType getNativeType() {
        return NativeType.CUSTOM;
    }

    @Override
    public BindableFaceAdViewHolder getCustomNativeViewHolder(View view) {
        return new CustomFaceNativeViewHolder(view);
    }


    @Override
    public int getCustomNativeLayout() {
        return R.layout.custom_face_native;
    }


    public static class CustomFaceNativeViewHolder extends BindableFaceAdViewHolder {
        private RelativeLayout adChoicesContainer;
        private TextView nativeAdTitle;
        private TextView nativeAdSocialContext;
        private AdIconView nativeAdIcon;
        private TextView sponsoredLabel;
        private Button nativeAdCallToAction;
        private LinearLayout adView;

        CustomFaceNativeViewHolder(View itemView) {
            super(itemView);
            adView = (LinearLayout) this.itemView;
            nativeAdIcon = adView.findViewById(com.admanager.facebook.R.id.native_ad_icon);
            adChoicesContainer = adView.findViewById(com.admanager.facebook.R.id.ad_choices_container);
            nativeAdTitle = adView.findViewById(com.admanager.facebook.R.id.native_ad_title);
            nativeAdSocialContext = adView.findViewById(com.admanager.facebook.R.id.native_ad_social_context);
            sponsoredLabel = adView.findViewById(com.admanager.facebook.R.id.native_ad_sponsored_label);
            nativeAdCallToAction = adView.findViewById(com.admanager.facebook.R.id.native_ad_call_to_action);
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
}

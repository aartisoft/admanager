package com.admanager.recyclerview;


import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.admanager.facebook.R;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;

import java.util.ArrayList;
import java.util.List;


public class FaceNativeAdViewHolder extends BindableFaceAdViewHolder {
    private AdIconView adIconView;
    private TextView tvAdTitle;
    private TextView tvAdBody;
    private Button btnCTA;
    private TextView sponsorLabel;
    private LinearLayout adChoicesContainer;
    private MediaView mediaView;

    FaceNativeAdViewHolder(View itemView) {
        super(itemView);
        adIconView = (AdIconView) itemView.findViewById(R.id.adIconView);
        tvAdTitle = (TextView) itemView.findViewById(R.id.tvAdTitle);
        tvAdBody = (TextView) itemView.findViewById(R.id.tvAdBody);
        btnCTA = (Button) itemView.findViewById(R.id.btnCTA);
        adChoicesContainer = (LinearLayout) itemView.findViewById(R.id.adChoicesContainer);
        mediaView = (MediaView) itemView.findViewById(R.id.mediaView);
        sponsorLabel = (TextView) itemView.findViewById(R.id.sponsored_label);

        String alert = "You should use View with id '%s' in layout file.";
        if (adIconView == null)
            throw new IllegalStateException(String.format(alert, "adIconView"));
        if (tvAdTitle == null)
            throw new IllegalStateException(String.format(alert, "tvAdTitle"));
        if (tvAdBody == null)
            throw new IllegalStateException(String.format(alert, "tvAdBody"));
        if (btnCTA == null)
            throw new IllegalStateException(String.format(alert, "btnCTA"));
        if (adChoicesContainer == null)
            throw new IllegalStateException(String.format(alert, "adChoicesContainer"));
        if (mediaView == null)
            throw new IllegalStateException(String.format(alert, "mediaView"));
        if (sponsorLabel == null)
            throw new IllegalStateException(String.format(alert, "sponsored_label"));
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


        tvAdTitle.setText(nativeAd.getAdvertiserName());
        tvAdBody.setText(nativeAd.getAdBodyText());
        btnCTA.setText(nativeAd.getAdCallToAction());
        sponsorLabel.setText(nativeAd.getSponsoredTranslation());

        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(btnCTA);
        clickableViews.add(mediaView);
        nativeAd.registerViewForInteraction(itemView, mediaView, adIconView, clickableViews);
    }
}

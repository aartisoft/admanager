package com.admanager.recyclerview;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.admanager.admob.R;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;


class AdmobNativeAdViewHolder extends RecyclerView.ViewHolder {
    private NativeAppInstallAdView mAdView;

    AdmobNativeAdViewHolder(View itemView) {
        super(itemView);
        mAdView = (NativeAppInstallAdView) this.itemView;
        mAdView.setHeadlineView(this.itemView.findViewById(R.id.appinstall_headline));
        mAdView.setBodyView(this.itemView.findViewById(R.id.appinstall_body));
        mAdView.setCallToActionView(this.itemView.findViewById(R.id.appinstall_call_to_action));
        mAdView.setIconView(this.itemView.findViewById(R.id.appinstall_app_icon));
    }

    void bindTo(NativeAppInstallAd appInstallAd) {
        if (appInstallAd == null) {
            mAdView.setVisibility(View.GONE);
            return;
        }
        ((TextView) mAdView.getHeadlineView()).setText(appInstallAd.getHeadline());
        ((TextView) mAdView.getBodyView()).setText(appInstallAd.getBody());
        ((Button) mAdView.getCallToActionView()).setText(appInstallAd.getCallToAction());
        ((ImageView) mAdView.getIconView()).setImageDrawable(appInstallAd.getIcon().getDrawable());

        // assign native ad object to the native view and make visible
        mAdView.setNativeAd(appInstallAd);
        mAdView.setVisibility(View.VISIBLE);
    }
}

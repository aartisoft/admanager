package com.admanager.sample.adapter

import android.app.Activity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.admanager.config.RemoteConfigHelper
import com.admanager.recyclerview.AdmAdapterConfiguration
import com.admanager.recyclerview.BaseAdapterWithFaceNative
import com.admanager.recyclerview.BindableFaceAdViewHolder
import com.admanager.sample.R
import com.admanager.sample.RCUtils
import com.facebook.ads.AdIconView
import com.facebook.ads.AdOptionsView
import com.facebook.ads.NativeAdsManager
import java.util.*

class TrackAdapterCustom(activity: Activity?) :
    BaseAdapterWithFaceNative<TrackModel?, TrackViewHolder?>(
        activity, R.layout.item_track, null, showNative(), nativeId
    ) {
    override fun createViewHolder(view: View): TrackViewHolder = TrackViewHolder(view)

    override fun configure(): AdmAdapterConfiguration<NativeType> =
        AdmAdapterConfiguration<NativeType>().customNativeLayout(R.layout.custom_face_native)
            .type(NativeType.CUSTOM)

    override fun onCreateCustomNativeViewHolder(view: View): BindableFaceAdViewHolder =
        CustomFaceNativeViewHolder(view)

    class CustomFaceNativeViewHolder internal constructor(itemView: View?) :
        BindableFaceAdViewHolder(itemView!!) {
        private val adChoicesContainer: LinearLayout
        private val nativeAdTitle: TextView
        private val nativeAdSocialContext: TextView
        private val nativeAdIcon: AdIconView
        private val sponsoredLabel: TextView
        private val nativeAdCallToAction: Button
        private val adView: LinearLayout = this.itemView as LinearLayout
        public override fun bindTo(manager: NativeAdsManager) { // Inflate Native Banner Ad into Container
            if (manager == null) {
                itemView.visibility = View.GONE
                return
            }
            val nativeAd = manager.nextNativeAd()
            if (nativeAd == null) {
                itemView.visibility = View.GONE
                return
            }
            itemView.visibility = View.VISIBLE
            nativeAd.unregisterView()
            val adOptionsView = AdOptionsView(itemView.context, nativeAd, null)
            adChoicesContainer.removeAllViews()
            adChoicesContainer.addView(adOptionsView, 0)
            nativeAdTitle.text = nativeAd.advertiserName
            nativeAdSocialContext.text = nativeAd.adSocialContext
            nativeAdCallToAction.visibility =
                if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
            nativeAdCallToAction.text = nativeAd.adCallToAction
            sponsoredLabel.text = nativeAd.sponsoredTranslation
            val clickableViews: MutableList<View> =
                ArrayList()
            clickableViews.add(nativeAdTitle)
            clickableViews.add(nativeAdCallToAction)
            nativeAd.registerViewForInteraction(adView, nativeAdIcon, clickableViews)

        }

        init {
            nativeAdIcon = adView.findViewById(com.admanager.facebook.R.id.native_ad_icon)
            adChoicesContainer =
                adView.findViewById(com.admanager.facebook.R.id.ad_choices_container)
            nativeAdTitle =
                adView.findViewById(com.admanager.facebook.R.id.native_ad_title)
            nativeAdSocialContext =
                adView.findViewById(com.admanager.facebook.R.id.native_ad_body)
            sponsoredLabel =
                adView.findViewById(com.admanager.facebook.R.id.native_ad_sponsored_label)
            nativeAdCallToAction =
                adView.findViewById(com.admanager.facebook.R.id.native_ad_call_to_action)
        }
    }

    companion object {
        private fun showNative(): Boolean =
            RemoteConfigHelper.getConfigs().getBoolean(RCUtils.native_facebook_enabled) && RemoteConfigHelper.areAdsEnabled()

        private val nativeId: String
            get() = RemoteConfigHelper.getConfigs().getString(RCUtils.native_facebook_id)
    }
}
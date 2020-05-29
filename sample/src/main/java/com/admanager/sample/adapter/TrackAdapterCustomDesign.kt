package com.admanager.sample.adapter

import android.app.Activity
import android.view.View
import com.admanager.config.RemoteConfigHelper
import com.admanager.recyclerview.AdmAdapterConfiguration
import com.admanager.recyclerview.BaseAdapterWithFaceNative
import com.admanager.sample.R
import com.admanager.sample.RCUtils

class TrackAdapterCustomDesign(activity: Activity?) :
    BaseAdapterWithFaceNative<TrackModel?, TrackViewHolder?>(
        activity, R.layout.item_track, null, showNative(), nativeId
    ) {
    override fun createViewHolder(view: View): TrackViewHolder = TrackViewHolder(view)

    /**
     * You should use same 'ids' with [com.admanager.facebook.R.layout.item_face_native_banner_ad]
     * or
     * If you are using [NativeType.NATIVE_LARGE], then check 'ids' from [com.admanager.facebook.R.layout.item_face_native_ad]
     */
    override fun configure(): AdmAdapterConfiguration<NativeType> =
        AdmAdapterConfiguration<NativeType>().customNativeLayout(R.layout.custom_item_face_native_banner_ad)

    companion object {
        private fun showNative(): Boolean =
            RemoteConfigHelper.getConfigs().getBoolean(RCUtils.native_facebook_enabled) && RemoteConfigHelper.areAdsEnabled()

        private val nativeId: String
            get() = RemoteConfigHelper.getConfigs().getString(RCUtils.native_facebook_id)
    }
}
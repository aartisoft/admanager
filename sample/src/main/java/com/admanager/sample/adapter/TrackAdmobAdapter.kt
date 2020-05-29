package com.admanager.sample.adapter

import android.app.Activity
import android.view.View
import com.admanager.config.RemoteConfigHelper
import com.admanager.recyclerview.BaseAdapterWithAdmobNative
import com.admanager.sample.R
import com.admanager.sample.RCUtils

class TrackAdmobAdapter(activity: Activity?) :
    BaseAdapterWithAdmobNative<TrackModel?, TrackViewHolder?>(
        activity, R.layout.item_track, null, showNative(), nativeId
    ) {
    override fun createViewHolder(view: View): TrackViewHolder = TrackViewHolder(view)

    companion object {
        private fun showNative(): Boolean =
            RemoteConfigHelper.getConfigs().getBoolean(RCUtils.native_admob_enabled) && RemoteConfigHelper.areAdsEnabled()

        private val nativeId: String
            get() = RemoteConfigHelper.getConfigs().getString(RCUtils.native_admob_id)
    }
}
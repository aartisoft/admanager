package com.admanager.sample.adapter;

import android.app.Activity;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.recyclerview.BaseAdapterWithAdmobNative;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;

public class TrackAdmobAdapter extends BaseAdapterWithAdmobNative<TrackModel, TrackViewHolder> {

    public TrackAdmobAdapter(Activity activity) {
        super(activity, TrackViewHolder.class, R.layout.item_track, null, showNative(), getNativeId());
    }

    private static boolean showNative() {
        return RemoteConfigHelper.getConfigs().getBoolean(RCUtils.NATIVE_ADMOB_ENABLED) && RemoteConfigHelper.areAdsEnabled();
    }

    private static String getNativeId() {
        return RemoteConfigHelper.getConfigs().getString(RCUtils.NATIVE_ADMOB_ID);
    }

}

package com.admanager.sample.adapter;

import android.app.Activity;
import android.view.View;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.recyclerview.BaseAdapterWithFaceNative;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;

public class TrackAdapterWithDensity extends BaseAdapterWithFaceNative<TrackModel, TrackViewHolder> {

    public TrackAdapterWithDensity(Activity activity) {
        super(activity,  R.layout.item_track, null, showNative(), getNativeId());
    }

    private static boolean showNative() {
        return RemoteConfigHelper.getConfigs().getBoolean(RCUtils.NATIVE_FACEBOOK_ENABLED) && RemoteConfigHelper.areAdsEnabled();
    }

    private static String getNativeId() {
        return RemoteConfigHelper.getConfigs().getString(RCUtils.NATIVE_FACEBOOK_ID);
    }

    @Override
    public TrackViewHolder createViewHolder(View view) {
        return new TrackViewHolder(view);
    }

    @Override
    public int density() {
        return 5; // show 1 native between 5 rows
    }
}

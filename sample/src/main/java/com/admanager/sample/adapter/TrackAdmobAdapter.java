package com.admanager.sample.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.recyclerview.BaseAdapterWithAdmobNative;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;

public class TrackAdmobAdapter extends BaseAdapterWithAdmobNative<TrackModel, TrackViewHolder> {

    public TrackAdmobAdapter(Activity activity) {
        super(activity,  R.layout.item_track, null, showNative(), getNativeId());
    }

    private static boolean showNative() {
        return RemoteConfigHelper.getConfigs().getBoolean(RCUtils.NATIVE_ADMOB_ENABLED) && RemoteConfigHelper.areAdsEnabled();
    }

    private static String getNativeId() {
        return RemoteConfigHelper.getConfigs().getString(RCUtils.NATIVE_ADMOB_ID);
    }

    @Override
    public TrackViewHolder createViewHolder(View view) {
        return new TrackViewHolder(view);
    }

    @NonNull
    @Override
    public NativeType getNativeType() {
        return NativeType.NATIVE_BANNER;
    }
}

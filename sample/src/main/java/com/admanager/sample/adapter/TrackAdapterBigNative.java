package com.admanager.sample.adapter;

import android.app.Activity;
import android.view.View;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.recyclerview.AdmAdapterConfiguration;
import com.admanager.recyclerview.BaseAdapterWithFaceNative;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;

public class TrackAdapterBigNative extends BaseAdapterWithFaceNative<TrackModel, TrackViewHolder> {

    public TrackAdapterBigNative(Activity activity) {
        super(activity,  R.layout.item_track, null, showNative(), getNativeId());
    }

    @Override
    protected TrackViewHolder createViewHolder(View view) {
        return new TrackViewHolder(view);
    }

    private static boolean showNative() {
        return RemoteConfigHelper.getConfigs().getBoolean(RCUtils.NATIVE_FACEBOOK_ENABLED) && RemoteConfigHelper.areAdsEnabled();
    }

    private static String getNativeId() {
        return RemoteConfigHelper.getConfigs().getString(RCUtils.NATIVE_FACEBOOK_ID);
    }

    @Override
    protected AdmAdapterConfiguration<NativeType> configure() {
        return new AdmAdapterConfiguration<NativeType>()
                .type(NativeType.NATIVE_LARGE);
    }
}

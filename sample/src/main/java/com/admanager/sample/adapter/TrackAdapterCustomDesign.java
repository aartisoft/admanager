package com.admanager.sample.adapter;

import android.app.Activity;
import android.view.View;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.recyclerview.BaseAdapterWithFaceNative;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;

public class TrackAdapterCustomDesign extends BaseAdapterWithFaceNative<TrackModel, TrackViewHolder> {

    public TrackAdapterCustomDesign(Activity activity) {
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

    /**
     * You should use same 'ids' with {@link com.admanager.facebook.R.layout.item_face_native_banner_ad}
     * or
     * If you are using {@link NativeType.NATIVE_LARGE}, then check 'ids' from {@link com.admanager.facebook.R.layout.item_face_native_ad}
     */
    @Override
    public int getCustomNativeLayout() {
        return R.layout.custom_item_face_native_banner_ad;
    }
}

package com.admanager.sample.adapter;

import android.app.Activity;

import com.admanager.recyclerview.BaseAdapterWithFaceNative;
import com.admanager.sample.R;

import java.util.List;

public class TrackAdapter extends BaseAdapterWithFaceNative<TrackModel, TrackViewHolder> {

    public TrackAdapter(Activity activity, List<TrackModel> data, boolean show_native, String nativeAdUnitId) {
        super(activity, TrackViewHolder.class, R.layout.item_track, data, show_native, nativeAdUnitId);
    }

}

package com.admanager.colorcallscreen.adapter;

import android.app.Activity;
import android.view.View;

import com.admanager.colorcallscreen.R;
import com.admanager.colorcallscreen.api.BgModel;
import com.admanager.recyclerview.AdmAdapterConfiguration;
import com.admanager.recyclerview.BaseAdapter;

public class CallScreenAdapter extends BaseAdapter<BgModel, CallScreenViewHolder> {


    public CallScreenAdapter(Activity activity) {
        super(activity, R.layout.ccs_item_ring_incoming);
    }

    @Override
    protected AdmAdapterConfiguration<?> configure() {
        return new AdmAdapterConfiguration<>()
                .gridSize(2)
                .density(99);
    }

    @Override
    protected CallScreenViewHolder createViewHolder(View view) {
        return new CallScreenViewHolder(view);
    }

}

package com.admanager.sample.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.admanager.recyclerview.BindableViewHolder;
import com.admanager.sample.R;

public class TrackViewHolder extends BindableViewHolder<TrackModel> {
    private TextView name;
    private TextView trackId;

    public TrackViewHolder(@NonNull View itemView) {
        super(itemView);
        trackId = itemView.findViewById(R.id.trackId);
        name = itemView.findViewById(R.id.name);
    }

    @Override
    public void bindTo(Activity activity, TrackModel t, int position) {
        this.trackId.setText(String.valueOf(t.trackId));
        this.name.setText(t.name);
    }
}

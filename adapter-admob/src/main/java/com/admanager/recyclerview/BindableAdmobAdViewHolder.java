package com.admanager.recyclerview;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.formats.UnifiedNativeAd;

public abstract class BindableAdmobAdViewHolder extends RecyclerView.ViewHolder {
    public BindableAdmobAdViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    protected abstract void bindTo(UnifiedNativeAd ad);
}

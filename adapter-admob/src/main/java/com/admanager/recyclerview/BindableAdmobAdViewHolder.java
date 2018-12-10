package com.admanager.recyclerview;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.ads.formats.NativeAppInstallAd;

public abstract class BindableAdmobAdViewHolder extends RecyclerView.ViewHolder {
    public BindableAdmobAdViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    protected abstract void bindTo(NativeAppInstallAd ad);
}

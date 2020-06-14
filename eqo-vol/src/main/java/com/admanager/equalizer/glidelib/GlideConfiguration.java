package com.admanager.equalizer.glidelib;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

@GlideModule
public class GlideConfiguration extends AppGlideModule {

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        // Apply options to the builder here.
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.format(DecodeFormat.PREFER_ARGB_8888);
        builder.setDefaultRequestOptions(requestOptions);
    }
}
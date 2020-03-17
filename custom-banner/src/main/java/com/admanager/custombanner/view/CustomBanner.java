package com.admanager.custombanner.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.admanager.config.RemoteConfigHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * Created by Gust on 20.11.2018.
 */
public class CustomBanner extends ImageView {

    public static final String TAG = "CustomBanner";
    public static final String TAG_PREFIX = "http://schemas.android.com/apk/res-auto";

    public AdListener adListener;
    private String targetUrl;
    private String imageUrl;
    private boolean enable;

    public CustomBanner(Context context) {
        super(context);
        RemoteConfigHelper.init(context);
    }

    public CustomBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        RemoteConfigHelper.init(context);

        String remoteConfigTargetUrlKey = attrs.getAttributeValue(TAG_PREFIX, "remoteConfigTargetUrlKey");
        String remoteConfigImageUrlKey = attrs.getAttributeValue(TAG_PREFIX, "remoteConfigImageUrlKey");
        String remoteConfigEnableKey = attrs.getAttributeValue(TAG_PREFIX, "remoteConfigEnableKey");

        if (!TextUtils.isEmpty(remoteConfigEnableKey)) {
            enable = RemoteConfigHelper.getConfigs().getBoolean(remoteConfigEnableKey);
        } else {
            enable = true;
        }
        enable = enable && RemoteConfigHelper.areAdsEnabled();

        if (!TextUtils.isEmpty(remoteConfigImageUrlKey)) {
            imageUrl = RemoteConfigHelper.getConfigs().getString(remoteConfigImageUrlKey);
        }

        if (!TextUtils.isEmpty(remoteConfigTargetUrlKey)) {
            targetUrl = RemoteConfigHelper.getConfigs().getString(remoteConfigTargetUrlKey);
        }
    }

    public AdListener getAdListener() {
        return adListener;
    }

    public void setAdListener(AdListener adListener) {
        this.adListener = adListener;
    }

    public CustomBanner setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
        return this;
    }

    public CustomBanner setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public CustomBanner setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // View is now attached

        load();
    }

    public void load() {
        setVisibility(enable ? VISIBLE : GONE);
        if (enable && !TextUtils.isEmpty(imageUrl) && !TextUtils.isEmpty(targetUrl)) {
            loadBanner(imageUrl, targetUrl);
        }
    }

    private void loadBanner(final String imageUrl, final String targetUrl) {
        Glide.with(this)
                .load(imageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d(TAG, "load failed.", e);
                        setVisibility(View.GONE);
                        if (adListener != null) {
                            adListener.onError((e != null && e.getMessage() != null) ? e.getMessage() : "");
                        }
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        setVisibility(View.VISIBLE);
                        if (adListener != null) {
                            adListener.onLoaded(imageUrl);
                        }
                        setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openUrl(getContext(), targetUrl);
                            }
                        });
                        return false;
                    }
                })
                .into(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // View is now detached, and about to be destroyed

    }

    private void openUrl(Context c, String url) {
        try {
            if (adListener != null) {
                adListener.onClick(url);
            }
            if (url.contains("play.google.com/store/apps/")) {
                try {
                    url = url.replace("http://play.google.com/store/apps/", "market://");
                    url = url.replace("https://play.google.com/store/apps/", "market://");
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    c.startActivity(intent);
                    return;
                } catch (Exception e) {
                    Log.e(TAG, "couldn't open url in Play store app");
                }
            }
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            c.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "couldn't open url");
        }
    }

    public interface AdListener {
        void onClick(String url);

        void onLoaded(String imageUrl);

        void onError(String error);
    }
}

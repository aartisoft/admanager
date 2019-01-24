package com.admanager.custombanner.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.admanager.config.RemoteConfigHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

/**
 * Created by Gust on 20.11.2018.
 */
public class CustomBanner extends ImageView {

    public static final String TAG = "CustomBanner";
    public static final String TAG_PREFIX = "http://schemas.android.com/apk/res-auto";

    public AdListener adListener;
    private String remoteConfigTargetUrlKey;
    private String remoteConfigImageUrlKey;
    private String remoteConfigEnableKey;

    public CustomBanner(Context context) {
        super(context);
        RemoteConfigHelper.init(context);
    }

    public CustomBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        RemoteConfigHelper.init(context);

        this.remoteConfigTargetUrlKey = attrs.getAttributeValue(TAG_PREFIX, "remoteConfigTargetUrlKey");
        this.remoteConfigImageUrlKey = attrs.getAttributeValue(TAG_PREFIX, "remoteConfigImageUrlKey");
        this.remoteConfigEnableKey = attrs.getAttributeValue(TAG_PREFIX, "remoteConfigEnableKey");
    }


    public AdListener getAdListener() {
        return adListener;
    }

    public void setAdListener(AdListener adListener) {
        this.adListener = adListener;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // View is now attached

        boolean enable = TextUtils.isEmpty(remoteConfigEnableKey) || RemoteConfigHelper.getConfigs().getBoolean(remoteConfigEnableKey);
        enable = enable && RemoteConfigHelper.areAdsEnabled();
        setVisibility(enable ? VISIBLE : GONE);

        if (enable && !TextUtils.isEmpty(this.remoteConfigTargetUrlKey) && !TextUtils.isEmpty(this.remoteConfigImageUrlKey)) {
            String imageUrl = RemoteConfigHelper.getConfigs().getString(remoteConfigImageUrlKey);
            String targetUrl = RemoteConfigHelper.getConfigs().getString(remoteConfigTargetUrlKey);

            if (!TextUtils.isEmpty(imageUrl) && !TextUtils.isEmpty(targetUrl)) {
                loadBanner(imageUrl, targetUrl);
            }
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
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        setVisibility(View.VISIBLE);
                        setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openUrl(getContext(), targetUrl);
                            }
                        });
                        return false;
                    }
                })
                .apply(new RequestOptions().override(getLayoutParams().width, Target.SIZE_ORIGINAL))
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
    }
}

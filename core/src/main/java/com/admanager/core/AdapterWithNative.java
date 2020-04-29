package com.admanager.core;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.AnimRes;
import androidx.annotation.IntRange;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AdapterWithNative<T extends AdapterWithNative<?>> extends Adapter {
    private static Map<String, AdapterWithNative> adAdapter = new HashMap<>();
    private final String key;
    private long closeTimeToHide;
    private int buttonAnimation;

    public AdapterWithNative(String adapterName, String enableKey) {
        super(adapterName, enableKey);
        this.key = UUID.randomUUID().toString();
        adAdapter.put(key, this);
    }

    static void adClosed(String key) {
        AdapterWithNative adapterWithNative = adAdapter.get(key);
        if (adapterWithNative == null) {
            return;
        }
        adapterWithNative.closed();
    }

    static void bindAdsToContainer(String key, ViewGroup adContainer) {
        AdapterWithNative adapterWithNative = adAdapter.get(key);
        if (adapterWithNative == null) {
            return;
        }
        adContainer.setVisibility(View.VISIBLE);
        adContainer.removeAllViews();

        adapterWithNative.loadAdsToContainer(adContainer);
    }

    public T closeTimeToHide(@IntRange(from = 500, to = 10000) long closeTimeToHide) {
        this.closeTimeToHide = closeTimeToHide;
        return (T) this;
    }

    public T animateActionButton() {
        this.buttonAnimation = R.anim.adm_bounce;
        return (T) this;
    }

    public T animateActionButton(@AnimRes int animate) {
        this.buttonAnimation = animate;
        return (T) this;
    }

    protected abstract void loadAdsToContainer(ViewGroup adContainer);

    private String getKey() {
        return key;
    }

    protected void displayFullScreenNativeActivity() {
        Intent intent = new Intent(getActivity(), FullScreenNativeActivity.class);
        intent.putExtra(FullScreenNativeActivity.PARAM_KEY, getKey());
        intent.putExtra(FullScreenNativeActivity.PARAM_CLOSE_TIME_TO_HIDE, closeTimeToHide);
        getActivity().startActivity(intent);
    }

    @Override
    protected void destroy() {
        super.destroy();
        adAdapter.remove(key);
    }

    public void animateView(View view) {
        if (buttonAnimation > 0 && view != null) {
            Animation animation = AnimationUtils.loadAnimation(getActivity(), buttonAnimation);
            view.startAnimation(animation);
        }
    }
}

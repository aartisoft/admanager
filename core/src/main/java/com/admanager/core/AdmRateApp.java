package com.admanager.core;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.lang.ref.WeakReference;

@Deprecated
public class AdmRateApp {
    private static final String TAG = "AdmRateApp";

    private final int firstAskAfterOpening;
    private final int askingRateDayInterval;
    private final int askingStoreInterval;
    private final int customLayout;
    private final int primaryColor;
    private final Activity context;

    private AdmRateApp(Activity context, int firstAskAfterOpening, int askingRateDayInterval, int askingStoreInterval, int customLayout, int primaryColor) {
        this.context = context;
        this.firstAskAfterOpening = firstAskAfterOpening;
        this.askingRateDayInterval = askingRateDayInterval;
        this.askingStoreInterval = askingStoreInterval;
        this.primaryColor = primaryColor;
        this.customLayout = customLayout == 0 ? R.layout.popup_rate_layout : customLayout;
    }

    @Deprecated
    public void rate() {

    }

    public void show() {
        rate(true);
    }

    private void rate(boolean force) {
        boolean rate = Prefs.with(context).remindRate(firstAskAfterOpening, askingRateDayInterval);
        boolean store = Prefs.with(context).remindStore(askingStoreInterval);

        if (!force && (!rate && !store)) {
            return;
        }

        if (force) {
            rate = true;
            store = false;
        }

        try {
            final Dialog rd = new Dialog(context);
            rd.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            rd.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            View inflate = context.getLayoutInflater().inflate(customLayout, null);

            applyStyle(inflate);

            final TextView dismiss = inflate.findViewById(R.id.dismiss);
            final Button play = inflate.findViewById(R.id.play);
            final RatingBar ratingBar = inflate.findViewById(R.id.ratingBar);
            final TextView textView = inflate.findViewById(R.id.text);
            final ImageView rateIcon = inflate.findViewById(R.id.rate_icon);

            try {
                int icon = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).icon;
                rateIcon.setImageDrawable(ContextCompat.getDrawable(context, icon));
            } catch (Throwable ignored) {

            }

            if (store) {
                play.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                dismiss.setText(R.string.rate_text_store);
            }

            final float[] rating = {0};
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float _rating, boolean fromUser) {
                    if (!ratingBar.isPressed()) {
                        return;
                    }

                    rating[0] = _rating;
                    boolean highRate = rating[0] > 4f;
                    play.setVisibility(highRate ? View.VISIBLE : View.GONE);

                    dismiss.setText(highRate ? R.string.rate_text_store : R.string.rate_text_thanks);
                }
            });

            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdmUtils.openApp(context, context.getPackageName());
                    Prefs.with(context).setUserRate(5);
                    Prefs.with(context).increaseUserRateStoreCount(true);
                    rd.dismiss();

                }
            });
            final boolean finalStore = store;
            rd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Prefs.with(context).setUserRate(rating[0] == 0 ? 1 : rating[0]);
                    if (finalStore || rating[0] > 4F) {
                        Prefs.with(context).increaseUserRateStoreCount();
                    }
                }
            });

            rd.setContentView(inflate);
            rd.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyStyle(View view) {
        if (primaryColor == 0) {
            // no need to update colors
            return;
        }
        FrameLayout root = (FrameLayout) view;
        TextView header = view.findViewById(R.id.header);
        TextView text = view.findViewById(R.id.text);

        int color = ContextCompat.getColor(context, primaryColor);
        root.setBackgroundColor(color);
        header.setTextColor(color);
        text.setTextColor(color);
    }

    public static class Builder {

        private final WeakReference<Activity> context;
        private int firstAskAfterOpening = 3;
        private int askingRateDayInterval = 7;
        private int askingStoreInterval = 1;
        private int customLayout = 0;
        private int primaryColor = 0;

        public Builder(@NonNull Activity activity) {
            this.context = new WeakReference<>(activity);
        }

        public Builder firstAskAfterOpening(int openingTimes) {
            this.firstAskAfterOpening = openingTimes;
            return this;
        }

        public Builder askingRateDayInterval(int days) {
            this.askingRateDayInterval = days;
            return this;
        }

        public Builder askingStoreInterval(int days) {
            this.askingStoreInterval = days;
            return this;
        }

        public Builder customLayout(@LayoutRes int customLayout) {
            this.customLayout = customLayout;
            return this;
        }

        public Builder primaryColor(@ColorRes int primaryColor) {
            this.primaryColor = primaryColor;
            return this;
        }

        public AdmRateApp build(Bundle savedInstanceState) {
            Activity activity = this.context.get();
            if (AdmUtils.isContextInvalid(activity)) {
                return null;
            }
            AdmRateApp admRateApp = new AdmRateApp(activity, firstAskAfterOpening, askingRateDayInterval, askingStoreInterval, customLayout, primaryColor);
            if (savedInstanceState == null) {
                Prefs.with(activity).appOpened();
            }
            //            admRateApp.rate(false);
            return admRateApp;
        }
    }

}

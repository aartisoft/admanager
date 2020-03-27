package com.admanager.popuprate;

import android.app.Application;
import android.content.Context;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public class RateApp {
    private static RateApp INSTANCE;
    public int bgDrawable;
    public int bgColor;
    public int textColor;

    public RateApp(Application app, int bgDrawable, int bgColor, int textColor) {
        this.bgDrawable = bgDrawable;
        this.bgColor = bgColor;
        this.textColor = textColor;
    }

    public static RateApp getInstance() {
        return INSTANCE;
    }

    private static RateApp init(RateApp rateApp) {
        INSTANCE = rateApp;

        return INSTANCE;
    }

    public static class Builder {

        private final WeakReference<Context> context;
        private int bgDrawable;
        private int bgColor;
        private int textColor;

        public Builder(@NonNull Application context) {
            this.context = new WeakReference<>(context.getApplicationContext());
        }

        public RateApp.Builder bgDrawable(@DrawableRes int bgDrawable) {
            this.bgDrawable = bgDrawable;
            return this;
        }

        public RateApp.Builder bgColor(@ColorRes int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public RateApp.Builder textColor(@ColorRes int textColor) {
            this.textColor = textColor;
            return this;
        }

        public void build() {

            Context context = this.context.get();

            Application app = (Application) context.getApplicationContext();
            RateApp.init(new RateApp(app, bgDrawable, bgColor, textColor));

        }

    }
}
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
    public int progressDrawable;
    public int fullySelectedColor;
    public int fullyNotSelectedColor;

    public RateApp(Application app, int bgDrawable, int bgColor, int textColor, int progressDrawable, int fullySelectedColor, int fullyNotSelectedColor) {
        this.bgDrawable = bgDrawable;
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.progressDrawable = progressDrawable;
        this.fullySelectedColor = fullySelectedColor;
        this.fullyNotSelectedColor = fullyNotSelectedColor;
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
        private int progressDrawable;
        private int fullyNotSelectedColor;
        private int fullySelectedColor;

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

        public RateApp.Builder progressDrawable(@DrawableRes int progressDrawable) {
            this.progressDrawable = progressDrawable;
            return this;
        }

        public RateApp.Builder fullyNotSelectedColor(@ColorRes int fullyNotSelectedColor) {
            this.fullyNotSelectedColor = fullyNotSelectedColor;
            return this;
        }

        public RateApp.Builder fullySelectedColor(@ColorRes int fullySelectedColor) {
            this.fullySelectedColor = fullySelectedColor;
            return this;
        }
        public RateApp.Builder textColor(@ColorRes int textColor) {
            this.textColor = textColor;
            return this;
        }
        public void build() {

            Context context = this.context.get();

            Application app = (Application) context.getApplicationContext();
            RateApp.init(new RateApp(app, bgDrawable, bgColor, textColor, progressDrawable, fullySelectedColor, fullyNotSelectedColor));

        }

    }
}
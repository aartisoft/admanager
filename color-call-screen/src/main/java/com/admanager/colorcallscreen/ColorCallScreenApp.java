package com.admanager.colorcallscreen;

import android.app.Application;
import android.content.Context;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.admanager.core.Ads;
import com.admanager.core.AdsImp;

import java.lang.ref.WeakReference;

public class ColorCallScreenApp {
    public static String[] NAMES = new String[]{"Alisa", "Oliver", "Sofia", "Anna", "Diana", "Alex", "Emma", "John", "Barbara", "Gabriel", "Cris"};
    public static int[] IMAGES = new int[]{R.drawable.female_1, R.drawable.male_1, R.drawable.female_2, R.drawable.female_3, R.drawable.female_4, R.drawable.male_2, R.drawable.female_5, R.drawable.male_3, R.drawable.female_6, R.drawable.female_7, R.drawable.male_4};
    public static String NUMBER = "+1 (650) 7x8 66 99";
    private static ColorCallScreenApp INSTANCE;
    public String title;
    public Ads ads;
    public int bgColor;
    public int textColor;

    ColorCallScreenApp(Application app, Ads ads, String title, int bgColor, int textColor, String NUMBER, String[] NAMES, int[] IMAGES) {
        this.title = title;
        this.ads = ads;
        this.bgColor = bgColor;
        this.textColor = textColor;
        if (NAMES != null) {
            ColorCallScreenApp.NAMES = NAMES;
        }
        if (IMAGES != null) {
            ColorCallScreenApp.IMAGES = IMAGES;
        }
        if (NUMBER != null) {
            ColorCallScreenApp.NUMBER = NUMBER;
        }

    }

    public static ColorCallScreenApp getInstance() {
        return INSTANCE;
    }

    private static ColorCallScreenApp init(ColorCallScreenApp colorCallScreenApp) {
        INSTANCE = colorCallScreenApp;
        return INSTANCE;
    }

    public static class Builder {

        private final WeakReference<Context> context;
        private String title;
        private Ads ads;
        private int bgColor;
        private int textColor;
        private String[] NAMES;
        private int[] IMAGES;
        private String number;

        public Builder(@NonNull Application context) {
            this.context = new WeakReference<>(context.getApplicationContext());
        }

        public Builder ads(Ads ads) {
            this.ads = ads;
            return this;
        }

        public ColorCallScreenApp.Builder title(String title) {
            if (title == null) {
                throw new IllegalArgumentException("null title is not allowed!");
            }
            this.title = title;
            return this;
        }

        public ColorCallScreenApp.Builder title(@StringRes int title) {
            Context c = context.get();
            if (c != null) {
                this.title = c.getString(title);
            }
            return this;
        }

        public ColorCallScreenApp.Builder sampleNumber(String number) {
            this.number = number;
            return this;
        }

        public ColorCallScreenApp.Builder sampleProfiles(String[] NAMES, int[] IMAGES) {
            if (NAMES == null || IMAGES == null) {
                return this;
            }
            if (NAMES.length != IMAGES.length) {
                return this;
            }

            this.NAMES = NAMES;
            this.IMAGES = IMAGES;
            return this;
        }

        public ColorCallScreenApp.Builder bgColor(@ColorRes int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public ColorCallScreenApp.Builder textColor(@ColorRes int textColor) {
            this.textColor = textColor;
            return this;
        }

        public void build() {
            if (ads == null) {
                ads = new AdsImp();
            }
            Context context = this.context.get();

            Application app = (Application) context.getApplicationContext();
            ColorCallScreenApp.init(new ColorCallScreenApp(app, ads, title, bgColor, textColor, number, NAMES, IMAGES));

        }

    }
}
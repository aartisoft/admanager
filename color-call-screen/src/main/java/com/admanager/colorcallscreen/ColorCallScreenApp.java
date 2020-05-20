package com.admanager.colorcallscreen;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.LinearLayout;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.admanager.colorcallscreen.model.AfterCallCard;
import com.admanager.core.Ads;
import com.admanager.core.AdsImp;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;

public class ColorCallScreenApp {
    public static String[] NAMES = new String[]{"Alisa", "Oliver", "Sofia", "Anna", "Diana", "Alex", "Emma", "John", "Barbara", "Gabriel", "Cris"};
    public static int[] IMAGES = new int[]{R.drawable.female_1, R.drawable.male_1, R.drawable.female_2, R.drawable.female_3, R.drawable.female_4, R.drawable.male_2, R.drawable.female_5, R.drawable.male_3, R.drawable.female_6, R.drawable.female_7, R.drawable.male_4};
    public static String NUMBER = "+1 (650) 7x8 66 99";
    private static ColorCallScreenApp INSTANCE;
    public List<AfterCallCard> afterCallCards;
    public String title;
    public Ads ads;
    public int bgColor;
    public int textColor;
    public AfterCallLayout afterCallLayout;

    ColorCallScreenApp(Application app, Ads ads, String title, int bgColor, int textColor, AfterCallLayout afterCallLayout, String NUMBER, String[] NAMES, int[] IMAGES, List<AfterCallCard> afterCallCards) {
        this.title = title;
        this.ads = ads;
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.afterCallLayout = afterCallLayout;
        this.afterCallCards = afterCallCards;
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

    public interface AfterCallLayout extends Serializable {
        void inflateInto(Activity activity, LinearLayout container, String lastNumber);
    }

    public static class Builder {

        private final WeakReference<Context> context;
        private String title;
        private Ads ads;
        private int bgColor;
        private int textColor;
        private AfterCallLayout afterCallLayout;
        private List<AfterCallCard> afterCallCards;
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

        public ColorCallScreenApp.Builder afterCallCards(List<AfterCallCard> afterCallCards) {
            this.afterCallCards = afterCallCards;
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

        public ColorCallScreenApp.Builder afterCallLayout(AfterCallLayout afterCallLayout) {
            this.afterCallLayout = afterCallLayout;
            return this;
        }

        public void build() {
            if (ads == null) {
                ads = new AdsImp();
            }
            Context context = this.context.get();

            Application app = (Application) context.getApplicationContext();
            ColorCallScreenApp.init(new ColorCallScreenApp(app, ads, title, bgColor, textColor, afterCallLayout, number, NAMES, IMAGES, afterCallCards));

        }

    }

}
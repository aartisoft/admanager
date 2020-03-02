package com.admanager.barcode;

import android.app.Application;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.admanager.core.Ads;
import com.admanager.core.AdsImp;

import java.lang.ref.WeakReference;

public class BarcodeReaderApp {
    private static BarcodeReaderApp INSTANCE;
    public String title;
    public Ads ads;
    public int bgColor;

    BarcodeReaderApp(Application app, Ads ads, String title, int bgColor) {
        this.title = title;
        this.ads = ads;
        this.bgColor = bgColor;
    }

    public static BarcodeReaderApp getInstance() {
        return INSTANCE;
    }

    private static BarcodeReaderApp init(BarcodeReaderApp barcodeReaderApp) {
        INSTANCE = barcodeReaderApp;
        return INSTANCE;
    }

    public static class Builder {

        private final WeakReference<Context> context;
        private String title;
        private Ads ads;
        private int bgColor;

        public Builder(@NonNull Application context) {
            this.context = new WeakReference<>(context.getApplicationContext());
        }

        public Builder ads(Ads ads) {
            this.ads = ads;
            return this;
        }

        public BarcodeReaderApp.Builder title(String title) {
            if (title == null) {
                throw new IllegalArgumentException("null title is not allowed!");
            }
            this.title = title;
            return this;
        }

        public BarcodeReaderApp.Builder title(@StringRes int title) {
            Context c = context.get();
            if (c != null) {
                this.title = c.getString(title);
            }
            return this;
        }

        public BarcodeReaderApp.Builder bgColor(@ColorRes int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public void build() {
            if (ads == null) {
                ads = new AdsImp();
            }
            Context context = this.context.get();

            Application app = (Application) context.getApplicationContext();
            BarcodeReaderApp.init(new BarcodeReaderApp(app, ads, title, bgColor));

        }

    }
}
package com.admanager.speedometeraltitude;

import android.app.Application;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.admanager.core.Ads;
import com.admanager.core.AdsImp;
import com.github.anastr.speedviewlib.components.indicators.Indicator;

import java.lang.ref.WeakReference;

public class SpeedoMeterAltitudeApp {
    private static SpeedoMeterAltitudeApp INSTANCE;
    public Ads ads;
    public int bgColor;
    public int speedoImage;
    public int altimeterImage;
    public int altimeterIndicator;
    public Indicator.Indicators speedoIndicatorType;
    public int speedoMarkColor;
    public int speedoSpeedTextColor;
    public int speedoTextColor;
    public int unitTextColor;

    public SpeedoMeterAltitudeApp(Application app, Ads ads, int bgColor, int speedoImage, int altimeterImage, int altimeterIndicator, Indicator.Indicators speedoIndicatorType, int speedoMarkColor, int speedoSpeedTextColor, int speedoTextColor, int unitTextColor) {
        this.ads = ads;
        this.bgColor = bgColor;
        this.speedoImage = speedoImage;
        this.altimeterImage = altimeterImage;
        this.altimeterIndicator = altimeterIndicator;
        this.speedoIndicatorType = speedoIndicatorType;
        this.speedoMarkColor = speedoMarkColor;
        this.speedoSpeedTextColor = speedoSpeedTextColor;
        this.speedoTextColor = speedoTextColor;
        this.unitTextColor = unitTextColor;
    }

    public static SpeedoMeterAltitudeApp getInstance() {
        return INSTANCE;
    }

    private static SpeedoMeterAltitudeApp init(SpeedoMeterAltitudeApp gifsApp) {
        INSTANCE = gifsApp;
        return INSTANCE;
    }

    public static class Builder {

        private final WeakReference<Context> context;
        private Ads ads;
        private int bgColor;
        private int speedoImage;
        private int altimeterImage;
        private int altimeterIndicator;
        private Indicator.Indicators speedoIndicatorType;
        private int speedoMarkColor;
        private int speedoSpeedTextColor;
        private int speedoTextColor;
        private int unitTextColor;

        public Builder(@NonNull Application context) {
            this.context = new WeakReference<>(context.getApplicationContext());
        }

        public Builder unitTextColor(@ColorRes int unitTextColor) {
            this.unitTextColor = unitTextColor;
            return this;
        }

        public Builder speedoTextColor(@ColorRes int speedoTextColor) {
            this.speedoTextColor = speedoTextColor;
            return this;
        }

        public Builder speedoSpeedTextColor(@ColorRes int speedoSpeedTextColor) {
            this.speedoSpeedTextColor = speedoSpeedTextColor;
            return this;
        }

        public Builder speedoMarkColor(@ColorRes int speedoIndicatorType) {
            this.speedoMarkColor = speedoMarkColor;
            return this;
        }

        public Builder speedoIndicatorType(Indicator.Indicators speedoIndicatorType) {
            this.speedoIndicatorType = speedoIndicatorType;
            return this;
        }

        public Builder altimeterIndicator(@DrawableRes int altimeterIndicator) {
            this.altimeterIndicator = altimeterIndicator;
            return this;
        }

        public Builder altimeterImage(@DrawableRes int altimeterImage) {
            this.altimeterImage = altimeterImage;
            return this;
        }

        public Builder speedoImage(@DrawableRes int speedoImage) {
            this.speedoImage = speedoImage;
            return this;
        }

        public Builder ads(Ads ads) {
            this.ads = ads;
            return this;
        }


        public SpeedoMeterAltitudeApp.Builder bgColor(@ColorRes int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public void build() {
            if (ads == null) {
                ads = new AdsImp();
            }
            Context context = this.context.get();

            Application app = (Application) context.getApplicationContext();
            SpeedoMeterAltitudeApp.init(new SpeedoMeterAltitudeApp(
                    app,
                    ads,
                    bgColor,
                    speedoImage,
                    altimeterImage,
                    altimeterIndicator,
                    speedoIndicatorType,
                    speedoMarkColor,
                    speedoSpeedTextColor,
                    speedoTextColor,
                    unitTextColor
            ));

        }

    }
}

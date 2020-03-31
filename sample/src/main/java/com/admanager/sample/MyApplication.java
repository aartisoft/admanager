package com.admanager.sample;

import android.app.Activity;
import android.widget.LinearLayout;

import androidx.multidex.MultiDexApplication;

import com.admanager.admob.AdmobBannerLoader;
import com.admanager.admob.AdmobNativeLoader;
import com.admanager.applocker.AppLockerApp;
import com.admanager.barcode.BarcodeReaderApp;
import com.admanager.boosternotification.BoosterNotificationApp;
import com.admanager.compass.CompassApp;
import com.admanager.config.RemoteConfigApp;
import com.admanager.core.Ads;
import com.admanager.gifs.GifsApp;
import com.admanager.maps.MapsApp;
import com.admanager.news.NewsApp;
import com.admanager.periodicnotification.PeriodicNotificationApp;
import com.admanager.popuprate.RateApp;
import com.admanager.speedometeraltitude.SpeedoMeterAltitudeApp;
import com.admanager.speedtest.SpeedTestApp;
import com.admanager.unseen.UnseenApp;
import com.admanager.wastickers.WastickersApp;
import com.admanager.weather.WeatherApp;

public class MyApplication extends MultiDexApplication implements Ads {

    @Override
    public void onCreate() {
        super.onCreate();

        //        new AdmStaticNotification.Builder(this, R.string.easy_access_title, R.string.easy_access_text)
        //                .build();
        new RemoteConfigApp.Builder(RCUtils.getDefaults())
                .build();
        new PeriodicNotificationApp.Builder(this)
                .build();

        new BoosterNotificationApp.Builder(this)
                .ads(this)
                .build();

        new AppLockerApp.Builder(this)
                .ads(this)
                .build();

        new WastickersApp.Builder(this)
                .ads(this)
                .build();

        new BarcodeReaderApp.Builder(this)
                .ads(this)
                .build();

        new CompassApp.Builder(this)
                .ads(this)
                .build();

        new SpeedTestApp.Builder(this)
                .ads(this)
                .build();

        new GifsApp.Builder(this, getString(R.string.giphy_api_key))
                .ads(this)
                .build();

        new MapsApp.Builder(this)
                .build();

        new UnseenApp.Builder(this)
                .build();

        new NewsApp.Builder(this,
                getString(R.string.the_start_magazine_key),
                getString(R.string.the_start_magazine_publisher_id))
                .build();

        new SpeedoMeterAltitudeApp.Builder(this)
                .ads(this)
                //.speedoIndicatorType(Indicator.Indicators.XXX)
                //.altimeterImage(R.drawable.altimeterImage)
                //.bgColor(R.color.colorPrimary)
                //.speedoImageBike(R.drawable.bikeImage)
                //.speedoImageWalk(R.drawable.walkImage)
                //.speedoImageMotorcycle(R.drawable.motorcycleImage)
                //.speedoImageCar(R.drawable.carImage)
                //.speedoMarkColor(R.color.markColor)
                //.speedoSpeedTextColor(R.color.speedTextColor)
                //.speedoTextColor(R.color.speedoTextColor)
                //.unitTextColor(R.color.unitTextColor)
                //.altimeterIndicator(R.drawable.altimeterIndicator)
                .build();
        new WeatherApp.Builder(this)
                //.ads(this)
                //.bgColor(R.color.colorStatusColor)
                //.itemBg(R.color.weather_silver)
                // .textColor(android.R.color.holo_green_light)
                .build();

        new RateApp.Builder(this)
                //.textColor(your text color) - optional-
                //.bgColor(dialog bg color) - optional-
                //.bgDrawable(dialog bg drawable) - optional-
                //.fullySelectedColor(R.color.colorYellow) - optional-
                //.fullyNotSelectedColor(R.color.colorblue) - optional-
                //.progressDrawable(your PROGRESS drawable) - optional-
                .build();
    }

    @Override
    public void loadTop(Activity activity, LinearLayout container) {
        new AdmobBannerLoader(activity, container, RCUtils.ADMOB_LIBS_BANNER_ENABLED).loadWithRemoteConfigId(RCUtils.ADMOB_LIBS_BANNER_ID);
    }

    @Override
    public void loadBottom(Activity activity, LinearLayout container) {
        new AdmobNativeLoader(activity, container, RCUtils.NATIVE_ADMOB_ENABLED).size(AdmobNativeLoader.NativeType.NATIVE_BANNER).loadWithRemoteConfigId(RCUtils.NATIVE_ADMOB_ID);
    }
}

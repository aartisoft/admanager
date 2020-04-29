package com.admanager.sample;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.multidex.MultiDexApplication;

import com.admanager.admob.AdmobBannerLoader;
import com.admanager.admob.AdmobNativeLoader;
import com.admanager.applocker.AppLockerApp;
import com.admanager.barcode.BarcodeReaderApp;
import com.admanager.boosternotification.BoosterNotificationApp;
import com.admanager.colorcallscreen.ColorCallScreenApp;
import com.admanager.compass.CompassApp;
import com.admanager.config.RemoteConfigApp;
import com.admanager.core.Ads;
import com.admanager.gifs.GifsApp;
import com.admanager.gpstimeaddresscoord.GPSTimeApp;
import com.admanager.maps.MapsApp;
import com.admanager.news.NewsApp;
import com.admanager.periodicnotification.PeriodicNotificationApp;
import com.admanager.popuprate.RateApp;
import com.admanager.sample.activity.Splash1Activity;
import com.admanager.speedometeraltitude.SpeedoMeterAltitudeApp;
import com.admanager.speedtest.SpeedTestApp;
import com.admanager.unseen.UnseenApp;
import com.admanager.wastickers.WastickersApp;
import com.admanager.weather.WeatherApp;

public class MyApplication extends MultiDexApplication implements Ads, ColorCallScreenApp.AfterCallLayout {

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
                .ads(this)
                .build();

        new UnseenApp.Builder(this)
                .ads(this)
                .build();

        new ColorCallScreenApp.Builder(this)
                .ads(this)
                .afterCallLayout(this)
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
                .ads(this)
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

        new GPSTimeApp.Builder(this)
                .ads(this)
                //.bgColor(android.R.color.holo_blue_bright) -- optional --
                //.cardBgColor(android.R.color.holo_green_light)-- optional --
                //.textColor(android.R.color.holo_red_dark)-- optional --
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

    @Override
    public void inflateInto(Activity activity, LinearLayout container, String lastNumber) {
        // color call screen aftercall screen

        // IMPORTANT NOTE! : This code is only an example, you MUST change below code
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_after_call, container);
        TextView textView = view.findViewById(R.id.text);
        Button changeCallScreen = view.findViewById(R.id.changeCallScreen);
        Button goToContacts = view.findViewById(R.id.goToContacts);

        textView.setText("Phone number: " + lastNumber);
        goToContacts.setOnClickListener(view12 -> {
            Intent contactIntent = new Intent(Intent.ACTION_MAIN);
            contactIntent.addCategory(Intent.CATEGORY_APP_CONTACTS);
            try {
                startActivity(contactIntent);
            } catch (Exception e) {
                Toast.makeText(activity, getString(R.string.adm_ccs_intent_not_found), Toast.LENGTH_SHORT).show();
            }
        });
        changeCallScreen.setOnClickListener(view1 -> {
            Intent launchIntent = new Intent(activity, Splash1Activity.class);
            startActivity(launchIntent);
            activity.finish(); // terminate the aftercall screen
        });

    }
}

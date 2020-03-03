package com.admanager.sample;

import android.app.Activity;
import android.support.multidex.MultiDexApplication;
import android.widget.LinearLayout;

import com.admanager.admob.AdmobBannerLoader;
import com.admanager.admob.AdmobNativeLoader;
import com.admanager.applocker.AppLockerApp;
import com.admanager.barcode.BarcodeReaderApp;
import com.admanager.boosternotification.BoosterNotificationApp;
import com.admanager.compass.CompassApp;
import com.admanager.config.RemoteConfigApp;
import com.admanager.core.Ads;
import com.admanager.gifs.GifsApp;
import com.admanager.periodicnotification.PeriodicNotificationApp;
import com.admanager.speedtest.SpeedTestApp;
import com.admanager.wastickers.WastickersApp;

public class MyApplication extends MultiDexApplication implements Ads {

    @Override
    public void onCreate() {
        super.onCreate();

        //        new AdmStaticNotification.Builder(this, R.string.easy_access_title, R.string.easy_access_text)
        //                .build();

        new PeriodicNotificationApp.Builder(this)
                .build();

        new RemoteConfigApp.Builder(RCUtils.getDefaults())
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

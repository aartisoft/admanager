package com.admanager.sample

import android.app.Activity
import android.content.Intent
import android.widget.LinearLayout
import androidx.multidex.MultiDexApplication
import com.admanager.admob.AdmobBannerLoader
import com.admanager.admob.AdmobNativeLoader
import com.admanager.applocker.AppLockerApp
import com.admanager.barcode.BarcodeReaderApp
import com.admanager.boosternotification.BoosterNotificationApp
import com.admanager.colorcallscreen.ColorCallScreenApp
import com.admanager.colorcallscreen.model.AfterCallCard
import com.admanager.compass.CompassApp
import com.admanager.config.RemoteConfigApp
import com.admanager.core.Ads
import com.admanager.equalizer.EqoVolApp
import com.admanager.gifs.GifsApp
import com.admanager.gpstimeaddresscoord.GPSTimeApp
import com.admanager.maps.MapsApp
import com.admanager.news.NewsApp
import com.admanager.periodicnotification.PeriodicNotificationApp
import com.admanager.popuprate.RateApp
import com.admanager.sample.RCUtils.getDefaults
import com.admanager.sample.activity.Splash1Activity
import com.admanager.speedometeraltitude.SpeedoMeterAltitudeApp
import com.admanager.speedtest.SpeedTestApp
import com.admanager.unseen.UnseenApp
import com.admanager.wastickers.WastickersApp
import com.admanager.weather.WeatherApp
import java.util.*

class MyApplication : MultiDexApplication(), Ads {
    override fun onCreate() {
        super.onCreate()
        // AdmStaticNotification.Builder(this, R.string.easy_access_title, R.string.easy_access_text)
        // .build();
        RemoteConfigApp.Builder(getDefaults())
            .build()

        PeriodicNotificationApp.Builder(this)
            .build()

        BoosterNotificationApp.Builder(this)
            .ads(this)
            .build()

        AppLockerApp.Builder(this)
            .ads(this)
            .build()

        WastickersApp.Builder(this)
            .ads(this)
            .build()

        BarcodeReaderApp.Builder(this)
            .ads(this)
            .build()

        CompassApp.Builder(this)
            .ads(this)
            .build()

        SpeedTestApp.Builder(this)
            .ads(this)
            .build()

        GifsApp.Builder(this, getString(R.string.giphy_api_key))
            .ads(this)
            .build()

        MapsApp.Builder(this)
            .ads(this)
            .build()

        UnseenApp.Builder(this)
            .ads(this)
            .build()

        val afterCallCards = createAfterCallCards()

        ColorCallScreenApp.Builder(this)
            .ads(this)
            .afterCallCards(afterCallCards)
            .build()

        NewsApp.Builder(
            this,
            getString(R.string.the_start_magazine_key),
            getString(R.string.the_start_magazine_publisher_id)
        )
            .build()

        SpeedoMeterAltitudeApp.Builder(this)
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
            .build()

        WeatherApp.Builder(this)
            .ads(this)
            //.bgColor(R.color.colorStatusColor)
            //.itemBg(R.color.weather_silver)
            // .textColor(android.R.color.holo_green_light)
            .build()

        RateApp.Builder(this)
            //.textColor(your text color) - optional-
            //.bgColor(dialog bg color) - optional-
            //.bgDrawable(dialog bg drawable) - optional-
            //.fullySelectedColor(R.color.colorYellow) - optional-
            //.fullyNotSelectedColor(R.color.colorblue) - optional-
            //.progressDrawable(your PROGRESS drawable) - optional-
            .build()

        GPSTimeApp.Builder(this)
            .ads(this)
            //.bgColor(android.R.color.holo_blue_bright) -- optional --
            //.cardBgColor(android.R.color.holo_green_light)-- optional --
            //.textColor(android.R.color.holo_red_dark)-- optional --
            .build()

        /*ADJUST
        val status: OSPermissionSubscriptionState = OneSignal.getPermissionSubscriptionState()
        val pushtoken: String = status.getSubscriptionStatus().getPushToken()
        AdmAdjust.init(this,
            R.string.adjust_app_token,
            R.string.adjust_secret_id,
            R.string.adjust_info_1,
            R.string.adjust_info_2,
            R.string.adjust_info_3,
            R.string.adjust_info_4,
            pushtoken, null)*/

        EqoVolApp.Builder(this)
            .ads(this)
            .setEqualizer(true)
            .build()

    }

    private fun createAfterCallCards(): ArrayList<AfterCallCard> {
        val afterCallCards = ArrayList<AfterCallCard>()
        val intent = Intent(this, Splash1Activity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val e = AfterCallCard(R.string.after_call_card_title, R.drawable.ic_download, intent)
        e.subtitle = R.string.after_call_card_subtitle
        afterCallCards.add(e)
        return afterCallCards
    }

    override fun loadTop(activity: Activity, container: LinearLayout) {
        AdmobBannerLoader(
            activity,
            container,
            RCUtils.admob_libs_banner_enabled
        ).loadWithRemoteConfigId(RCUtils.admob_libs_banner_id)
    }

    override fun loadBottom(activity: Activity, container: LinearLayout) {
        AdmobNativeLoader(
            activity,
            container,
            RCUtils.native_admob_enabled
        ).size(AdmobNativeLoader.NativeType.NATIVE_BANNER)
            .loadWithRemoteConfigId(RCUtils.native_admob_id)
    }
}
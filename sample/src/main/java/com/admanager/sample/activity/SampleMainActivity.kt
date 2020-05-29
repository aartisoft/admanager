package com.admanager.sample.activity

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.admanager.admob.AdmobAdHelper
import com.admanager.admob.AdmobAdapter
import com.admanager.admob.AdmobNativeLoader
import com.admanager.applocker.AppLockerApp
import com.admanager.barcode.activities.BarcodeReaderActivity
import com.admanager.boosternotification.BoosterNotificationApp
import com.admanager.colorcallscreen.activities.ColorCallScreenActivity
import com.admanager.core.AdManagerBuilder
import com.admanager.core.AdmUtils
import com.admanager.core.ShareUtils
import com.admanager.core.toolbar.AdmToolbarAnimator
import com.admanager.gpstimeaddresscoord.activities.GPSTimeAddressCoordActivity
import com.admanager.maps.activities.MapsActivity
import com.admanager.popupenjoy.AdmPopupEnjoy
import com.admanager.popuppromo.AdmPopupPromo
import com.admanager.popuprate.dialog.RateAppView
import com.admanager.popuprate.dialog.RateDialog
import com.admanager.popuprate.listeners.RateClickListener
import com.admanager.sample.*
import com.admanager.speedometeraltitude.activities.SpeedoMeterAltitudeActivity
import com.admanager.speedtest.activities.SpeedTestActivity
import com.admanager.wastickers.activities.WAStickersActivity
import com.admanager.weather.activities.WeatherActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_main.*

/**
 * Created by Gust on 20.11.2018.
 */
class SampleMainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener, RateClickListener {
    private val dialog: RateAppView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate_popupad)
        // initialize Navigation Drawer
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        setTitle(R.string.ex_sample_main)
        val navView = findViewById<NavigationView>(R.id.nav_view)
        /*
         *  inits switch compat menu, and handles clicks events automatically
         * */
        BoosterNotificationApp.configureSwitchMenu(navView, R.id.nav_notification)
        navView.setNavigationItemSelectedListener(this)
        /*
         * Hides app lock menu if device not suitable for this feature
         * */
        AppLockerApp.configureMenu(navView, R.id.nav_applock)

        //        Homepage açılması -> 3sec inters -> enjoy pop up -> inters ->  firebase pop up
        /** STEP 1 - SHOW 3 SECS  */
        AdmobAdHelper.showNsecInters(
            3000, this, RCUtils.main_3sec_admob_enabled, RCUtils.main_3sec_admob_id
        ) {
            /** STEP 2 - SHOW ENJOY  */
            val enjoyAds: AdmPopupEnjoy.Ads = object : AdmPopupEnjoy.Ads {
                override fun createAdManagerBuilder(activity: Activity): AdManagerBuilder {
                    return AdManagerBuilder(activity).add(
                        AdmobAdapter(RCUtils.main_admob_enabled).withRemoteConfigId(
                            RCUtils.main_admob_id
                        )
                    )
                }

                override fun loadBottom(
                    activity: Activity, container: LinearLayout
                ) {
                    AdmobNativeLoader(
                        activity, container, RCUtils.native_admob_enabled
                    ).loadWithRemoteConfigId(RCUtils.native_admob_id)
                }
            }
            val enjoyListener = AdmPopupEnjoy.Listener {
                /** STEP 3 - SHOW FIREBASE POPUP  */
                AdmPopupPromo.Builder(this@SampleMainActivity)
                    .build()
                    .show()
            }
            AdmPopupEnjoy.Builder(this@SampleMainActivity, enjoyAds)
                .listener(enjoyListener)
                .build().show()
        }
        AdmToolbarAnimator(toolbar)
            .animate()
            .setBadgeText("9+")
            .start()

        txtMainTitle.underline()

        // Prefs should be initialized on application create or anywhere before using
        Prefs.init(this)

        snack("some integer from preferences: ${Prefs.someInteger}")
        Prefs.someInteger = 5

        Handler().postDelayed({
            toast("updated some integer from preferences: ${Prefs.someInteger}")
        }, 1500)


    }

    private fun toast(title: CharSequence, text: Boolean) {
        toast(title, text.toString())
    }

    private fun toast(title: CharSequence, text: Float) {
        toast(title, text.toString())
    }

    private fun toast(title: CharSequence, text: String) {
        val str = "$title: $text"
        toast(str)
        //logs only in debug apk
        logd(str)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.nav_dialogad -> AdmPopupPromo.Builder(this)
                .build()
                .show()
            R.id.nav_wastickers -> WAStickersActivity.start(this)
            R.id.nav_barcode_reader -> BarcodeReaderActivity.start(this)
            R.id.nav_speed_test -> SpeedTestActivity.start(this)
            R.id.nav_gps_satellite -> SpeedTestActivity.start(this)
            R.id.nav_compass -> { //            CompassActivity.start(this);
            }
            R.id.nav_share -> ShareUtils.shareApp(this, "Lets check interesting app", true)
            R.id.isConnected -> toast(item.title, AdmUtils.isConnected(this))
            R.id.nav_applock -> AppLockerApp.showActivity(this)
            R.id.isConnectedMobile -> toast(item.title, AdmUtils.isConnectedMobile(this))
            R.id.isConnectedWifi -> toast(item.title, AdmUtils.isConnectedWifi(this))
            R.id.dpToPx -> toast(item.title, AdmUtils.dpToPx(this, 50f))
            R.id.pxToDp -> toast(item.title, 50.pXtoDp)
            R.id.capitalize -> toast(item.title, AdmUtils.capitalize("CaPiTAlizEd"))
            R.id.maps -> { //MapsActivity.start(this,"ATM");
                MapsActivity.start(this)
            }
            R.id.weather -> WeatherActivity.start(this)
            R.id.mSpeedoMeter -> SpeedoMeterAltitudeActivity.start(this)
            R.id.mRateDialog -> RateDialog(this, this).show()
            R.id.mGps -> GPSTimeAddressCoordActivity.start(this)
            R.id.colorCallScreen -> ColorCallScreenActivity.start(this)
        }
        return false
    }

    override fun onRated(isShowed: Boolean) { // do whatever you want!
    }

    companion object {
        const val TAG = "RateAndPopupAds"
    }
}
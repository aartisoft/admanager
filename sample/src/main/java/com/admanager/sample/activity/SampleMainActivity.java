package com.admanager.sample.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.admanager.admob.AdmobAdHelper;
import com.admanager.admob.AdmobAdapter;
import com.admanager.admob.AdmobNativeLoader;
import com.admanager.applocker.AppLockerApp;
import com.admanager.barcode.activities.BarcodeReaderActivity;
import com.admanager.boosternotification.BoosterNotificationApp;
import com.admanager.compass.activities.CompassActivity;
import com.admanager.core.AdManagerBuilder;
import com.admanager.core.AdmUtils;
import com.admanager.core.ShareUtils;
import com.admanager.maps.activities.MapsActivity;
import com.admanager.popupenjoy.AdmPopupEnjoy;
import com.admanager.popuppromo.AdmPopupPromo;
import com.admanager.popuprate.dialog.RateAppView;
import com.admanager.popuprate.dialog.RateDialog;
import com.admanager.popuprate.listeners.RateClickListener;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;
import com.admanager.speedometeraltitude.activities.SpeedoMeterAltitudeActivity;
import com.admanager.speedtest.activities.SpeedTestActivity;
import com.admanager.wastickers.activities.WAStickersActivity;
import com.admanager.weather.activities.WeatherActivity;
import com.google.android.material.navigation.NavigationView;

/**
 * Created by Gust on 20.11.2018.
 */
public class SampleMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RateClickListener {
    public static final String TAG = "RateAndPopupAds";
    private RateAppView dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_popupad);

        // initialize Navigation Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setTitle(R.string.ex_sample_main);

        NavigationView navView = findViewById(R.id.nav_view);

        /*
         *  inits switch compat menu, and handles clicks events automatically
         * */
        BoosterNotificationApp.configureSwitchMenu(navView, R.id.nav_notification);
        navView.setNavigationItemSelectedListener(this);

        /*
         * Hides app lock menu if device not suitable for this feature
         * */
        AppLockerApp.configureMenu(navView, R.id.nav_applock);

//        Homepage açılması -> 3sec inters -> enjoy pop up -> inters ->  firebase pop up

        /** STEP 1 - SHOW 3 SECS */
        AdmobAdHelper.showNsecInters(3000, this, RCUtils.MAIN_3SEC_ADMOB_ENABLED, RCUtils.MAIN_3SEC_ADMOB_ID, new AdmobAdHelper.Listener() {
            @Override
            public void completed(boolean displayed) {
                /** STEP 2 - SHOW ENJOY */

                AdmPopupEnjoy.Ads enjoyAds = new AdmPopupEnjoy.Ads() {
                    @Override
                    public AdManagerBuilder createAdManagerBuilder(Activity activity) {
                        return new AdManagerBuilder(activity)
                                .add(new AdmobAdapter(RCUtils.MAIN_ADMOB_ENABLED).withRemoteConfigId(RCUtils.MAIN_ADMOB_ID));
                    }

                    @Override
                    public void loadBottom(Activity activity, LinearLayout container) {
                        new AdmobNativeLoader(activity, container, RCUtils.NATIVE_ADMOB_ENABLED).loadWithRemoteConfigId(RCUtils.NATIVE_ADMOB_ID);
                    }
                };

                AdmPopupEnjoy.Listener enjoyListener = new AdmPopupEnjoy.Listener() {
                    @Override
                    public void completed(boolean displayed) {
                        /** STEP 3 - SHOW FIREBASE POPUP */
                        new AdmPopupPromo.Builder(SampleMainActivity.this)
                                .build()
                                .show();

                    }
                };

                new AdmPopupEnjoy.Builder(SampleMainActivity.this, enjoyAds)
                        .listener(enjoyListener)
                        .build().show();

            }
        });


    }

    private void toast(CharSequence title, boolean text) {
        toast(title, String.valueOf(text));
    }

    private void toast(CharSequence title, float text) {
        toast(title, String.valueOf(text));
    }

    private void toast(CharSequence title, String text) {
        String str = title + ": " + text;
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_dialogad) {
            new AdmPopupPromo.Builder(this)
                    .build()
                    .show();
        } else if (id == R.id.nav_wastickers) {
            WAStickersActivity.start(this);
        } else if (id == R.id.nav_barcode_reader) {
            BarcodeReaderActivity.start(this);
        } else if (id == R.id.nav_speed_test) {
            SpeedTestActivity.start(this);
        } else if (id == R.id.nav_gps_satellite) {
            SpeedTestActivity.start(this);
        } else if (id == R.id.nav_compass) {
            CompassActivity.start(this);
        } else if (id == R.id.nav_share) {
            ShareUtils.shareApp(this, "Lets check interesting app", true);
        } else if (id == R.id.isConnected) {
            toast(item.getTitle(), AdmUtils.isConnected(this));
        } else if (id == R.id.nav_applock) {
            AppLockerApp.showActivity(this);
        } else if (id == R.id.isConnectedMobile) {
            toast(item.getTitle(), AdmUtils.isConnectedMobile(this));
        } else if (id == R.id.isConnectedWifi) {
            toast(item.getTitle(), AdmUtils.isConnectedWifi(this));
        } else if (id == R.id.dpToPx) {
            toast(item.getTitle(), AdmUtils.dpToPx(this, 50));
        } else if (id == R.id.pxToDp) {
            toast(item.getTitle(), AdmUtils.pxToDp(this, 50));
        } else if (id == R.id.capitalize) {
            toast(item.getTitle(), AdmUtils.capitalize("CaPiTAlizEd"));
        } else if (id == R.id.maps) {
            //MapsActivity.start(this,"ATM");
            MapsActivity.start(this);
        } else if (id == R.id.weather) {
            WeatherActivity.start(this);
        } else if (id == R.id.mSpeedoMeter) {
            SpeedoMeterAltitudeActivity.start(this);
        } else if (id == R.id.mRateDialog) {
            new RateDialog(this, this).show();
        }
        return false;
    }

    @Override
    public void onRated(boolean isShowed) {
        // do whatever you want!
    }
}

package com.admanager.sample.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.admanager.core.AdmRateApp;
import com.admanager.core.AdmUtils;
import com.admanager.core.ShareUtils;
import com.admanager.core.staticnotification.AdmStaticNotification;
import com.admanager.popupad.AdmPopupAd;
import com.admanager.sample.R;

/**
 * Created by Gust on 20.11.2018.
 */
public class SampleMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "RateAndPopupAds";
    AdmRateApp rateApp;

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
        AdmStaticNotification.configureSwitchMenu(navView, R.id.nav_notification);
        navView.setNavigationItemSelectedListener(this);

        /*
         * show firebase popup Ad
         * */
        new AdmPopupAd.Builder(this)
                .build()
                .show();

        /*
         * Ask user for rating after 3rd open or 7 days after then first open
         * */
        rateApp = new AdmRateApp.Builder(this)
                .build(savedInstanceState);

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
        if (id == R.id.nav_rate) {
            rateApp.rate();
        } else if (id == R.id.nav_share) {
            ShareUtils.shareApp(this, "Lets check interesting app", true);
        } else if (id == R.id.isConnected) {
            toast(item.getTitle(), AdmUtils.isConnected(this));
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
        }
        return false;
    }
}

package com.admanager.applocker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.admanager.applocker.AppLockerApp;
import com.admanager.applocker.R;
import com.admanager.applocker.fragments.AllAppFragment;
import com.admanager.applocker.prefrence.Prefs;
import com.admanager.applocker.utils.AppLockConstants;
import com.admanager.applocker.utils.AppLockInitializer;

public class HomeActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    private AppLockerApp.Ads ads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locker_activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ads = AppLockerApp.getInstance().getAds();
        ads.loadBottom(this, (LinearLayout) findViewById(R.id.bottom_container));
        ads.loadTop(this, (LinearLayout) findViewById(R.id.top_container));

        fragmentManager = getSupportFragmentManager();

        getSupportActionBar().setTitle("All Applications");
        Fragment f = AllAppFragment.newInstance(AppLockConstants.ALL_APPS);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();

        AppLockInitializer.initAndForcePermissionDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppLockInitializer.onActivityResult(this);
    }

    @Override
    public void onBackPressed() {
        if (getCurrentFragment() instanceof AllAppFragment) {
            super.onBackPressed();
        } else {
            fragmentManager.popBackStack();
            getSupportActionBar().setTitle(R.string.nav_home);
            Fragment f = AllAppFragment.newInstance(AppLockConstants.ALL_APPS);
            fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
        }
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app_lock_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_password) {
            boolean passwordSet = Prefs.with(this).isPasswordSet();
            PasswordActivity.startPasswordSet(this, !passwordSet);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

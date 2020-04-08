package com.admanager.sample.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.admanager.admob.AdmobAdapter;
import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.AdManagerBuilder;
import com.admanager.facebook.FacebookAdapter;
import com.admanager.sample.RCUtils;

/**
 * Created by Gust on 20.11.2018.
 */
public class Splash1Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RemoteConfigHelper.init(this);

        //        If you want to close all ads for testing, use this method
        //RemoteConfigHelper.setAdsEnabled(!BuildConfig.DEBUG);

        new AdManagerBuilder(this)
                .add(new AdmobAdapter(RCUtils.S1_ADMOB_ENABLED).withRemoteConfigId(RCUtils.S1_ADMOB_ID))
                .add(new FacebookAdapter(RCUtils.S1_FACEBOOK_ENABLED).withRemoteConfigId(RCUtils.S1_FACEBOOK_ID))
                .thenStart(Splash2Activity.class)
                .build()
                .show();

    }
}

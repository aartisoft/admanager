package com.admanager.sample.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.admanager.admob.AdmobBannerLoader;
import com.admanager.facebook.FacebookBannerLoader;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;

/**
 * Created by Gust on 20.11.2018.
 */
public class BannerDemoActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_banner);

        LinearLayout admobContainer = (LinearLayout) findViewById(R.id.admob_container);
        LinearLayout facebookContainer = (LinearLayout) findViewById(R.id.facebook_container);

        new FacebookBannerLoader(this, facebookContainer, RCUtils.FACEBOOK_BANNER_ENABLED).loadWithRemoteConfigId(RCUtils.FACEBOOK_BANNER_ID);
        new AdmobBannerLoader(this, admobContainer, RCUtils.ADMOB_BANNER_ENABLED).loadWithRemoteConfigId(RCUtils.ADMOB_BANNER_ID);
    }

}
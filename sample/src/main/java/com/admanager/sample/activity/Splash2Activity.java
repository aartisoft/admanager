package com.admanager.sample.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.admanager.admob.AdmobAdapter;
import com.admanager.core.AdManager;
import com.admanager.core.AdManagerBuilder;
import com.admanager.facebook.FacebookAdapter;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;

/**
 * Created by Gust on 20.11.2018.
 */
public class Splash2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_2);

        AdManager build = new AdManagerBuilder(this)
                .add(new AdmobAdapter(RCUtils.S2_ADMOB_ENABLED).withRemoteConfigId(RCUtils.S2_ADMOB_ID))
                .add(new FacebookAdapter(RCUtils.S2_FACEBOOK_ENABLED).withRemoteConfigId(RCUtils.S2_FACEBOOK_ID))
                .thenStart(MainActivity.class)
                .build();

        build.showOnClick(R.id.confirm, true);
    }
}

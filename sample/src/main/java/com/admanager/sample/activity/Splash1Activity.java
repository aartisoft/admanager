package com.admanager.sample.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.admanager.admob.AdmobAdapter;
import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.AdManager;
import com.admanager.core.AdManagerBuilder;
import com.admanager.facebook.FacebookAdapter;
import com.admanager.sample.RCUtils;

/**
 * Created by Gust on 20.12.2017.
 */
public class Splash1Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RemoteConfigHelper.init(this);

        AdManager manager = new AdManagerBuilder(this)
                .add(new AdmobAdapter(RCUtils.S1_ADMOB_ENABLED).withRemoteConfigId(RCUtils.S1_ADMOB_ID))
                .add(new FacebookAdapter(RCUtils.S1_FACEBOOK_ENABLED).withRemoteConfigId(RCUtils.S1_FACEBOOK_ID))
                .thenStart(Splash2Activity.class)
                .build();

        // show immediately when loaded
        manager.show();

    }
}

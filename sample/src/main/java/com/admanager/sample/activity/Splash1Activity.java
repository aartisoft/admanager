package com.admanager.sample.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.admanager.admob.AdmobAdapter;
import com.admanager.core.AdManager;
import com.admanager.facebook.FacebookAdapter;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;
import com.admanager.unity.UnityAdapter;

/**
 * Created by Gust on 20.12.2017.
 */
public class Splash1Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AdManager build = new AdManager(this)
                .add(new AdmobAdapter(RCUtils.S1_ADMOB_ENABLED).withRemoteConfigId(RCUtils.S1_ADMOB_ID))
                .add(new FacebookAdapter(RCUtils.S1_FACEBOOK_ENABLED).withRemoteConfigId(RCUtils.S1_FACEBOOK_ID))
                .add(new UnityAdapter(RCUtils.S1_UNITY_ENABLED).withId(getString(R.string.unity_game_id), getString(R.string.unity_placement_id_s1)))
                .build();
        build.show();
    }
}

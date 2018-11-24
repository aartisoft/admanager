package com.admanager.sample.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.admanager.admob.AdmobAdapter;
import com.admanager.core.AdManager;
import com.admanager.facebook.FacebookAdapter;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;
import com.admanager.unity.UnityAdapter;

/**
 * Created by Gust on 20.12.2017.
 */
public class Splash2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN, View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.splash_screen_2);

        AdManager build = new AdManager(this)
                .add(new AdmobAdapter(RCUtils.S2_ADMOB_ENABLED).withRemoteConfigId(RCUtils.S2_ADMOB_ID))
                .add(new FacebookAdapter(RCUtils.S2_FACEBOOK_ENABLED).withRemoteConfigId(RCUtils.S2_FACEBOOK_ID))
                .add(new UnityAdapter(RCUtils.S2_UNITY_ENABLED).withId(getString(R.string.unity_game_id), getString(R.string.unity_placement_id_s2)))
                .build();
        build.show();
    }
}

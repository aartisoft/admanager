package com.admanager.sample.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.admanager.admob.AdmobAdapter;
import com.admanager.core.AdManager;
import com.admanager.core.AdManagerBuilder;
import com.admanager.core.DummyAdapter;
import com.admanager.facebook.FacebookAdapter;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;
import com.admanager.unity.UnityAdapter;

import java.util.concurrent.TimeUnit;

/**
 * Created by Gust on 20.11.2018.
 */
public class MainIntersActivity extends AppCompatActivity implements View.OnClickListener {

    AdManager adManager;
    AdManager unityOnExit;
    AdManager unityOnResume;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_inters);

        adManager = new AdManagerBuilder(this)
                .add(new DummyAdapter())
                .add(new AdmobAdapter(RCUtils.MAIN_ADMOB_ENABLED).withRemoteConfigId(RCUtils.MAIN_ADMOB_ID))
                .add(new FacebookAdapter(RCUtils.MAIN_FACEBOOK_ENABLED).withRemoteConfigId(RCUtils.MAIN_FACEBOOK_ID))
                .build();

        unityOnExit = new AdManagerBuilder(this)
                .add(new UnityAdapter(RCUtils.ONEXIT_UNITY_ENABLED).withId(getString(R.string.unity_game_id), getString(R.string.unity_placement_id_onexit)))
                .build();

        unityOnResume = new AdManagerBuilder(this)
                .add(new UnityAdapter(RCUtils.ONRESUME_UNITY_ENABLED).withId(getString(R.string.unity_game_id), getString(R.string.unity_placement_id_onresume)))
                .build();

        findViewById(R.id.any_action).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.any_action:
                // any_action();
                adManager.showOne();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        unityOnResume.showOneByTimeCap(TimeUnit.MINUTES.toMillis(1));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        unityOnExit.showAndFinish();
    }
}

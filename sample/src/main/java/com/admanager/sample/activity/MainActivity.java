package com.admanager.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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
 * Created by Gust on 20.12.2017.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    AdManager adManager;
    AdManager unityOnExit;
    AdManager unityOnResume;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button anyAction = findViewById(R.id.any_action);
        anyAction.setOnClickListener(this);

        Button otherAction = findViewById(R.id.other_action);
        otherAction.setOnClickListener(this);

        Button actionWithoutAds = findViewById(R.id.action_without_ads);
        actionWithoutAds.setOnClickListener(this);

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


        Button recyclerViewExample = findViewById(R.id.recycler_view_example);
        recyclerViewExample.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.any_action:
                // any_action();
                adManager.showOne();
                break;
            case R.id.other_action:
                // other_action();
                adManager.showOne();
                break;
            case R.id.action_without_ads:
                // action_without_ads();
                break;
            case R.id.recycler_view_example:
                startActivity(new Intent(this, NativeListActivity.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        unityOnResume.showOneByTimeCap(TimeUnit.SECONDS.toMillis(15));
//        unityOnResume.showOneByTimeCap(TimeUnit.MINUTES.toMillis(1));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        unityOnExit.showAndFinish();
    }
}

package com.admanager.sample.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.admanager.admob.AdmobAdapter;
import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.AdManager;
import com.admanager.core.AdManagerBuilder;
import com.admanager.core.DummyAdapter;
import com.admanager.facebook.FacebookAdapter;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;
import com.admanager.sample.adapter.TrackAdapter;
import com.admanager.sample.adapter.TrackModel;
import com.admanager.unity.UnityAdapter;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Gust on 20.12.2017.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    AdManager adManager;
    AdManager unityOnExit;
    AdManager unityOnResume;
    TrackAdapter trackAdapter;

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


        // Recycler View
        boolean showNative = RemoteConfigHelper.getConfigs().getBoolean(RCUtils.NATIVE_FACEBOOK_ENABLED);
        String nativeId = RemoteConfigHelper.getConfigs().getString(RCUtils.NATIVE_FACEBOOK_ID);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        trackAdapter = new TrackAdapter(this, new ArrayList<TrackModel>(), showNative, nativeId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(trackAdapter);

        loadTracks();
    }

    private void loadTracks() {
        trackAdapter.loadingMore();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<TrackModel> data = new ArrayList<>();
                for (int i = 0; i < 50; i++) {
                    data.add(new TrackModel(i, "Track_" + i));
                }
                trackAdapter.setData(data);
                trackAdapter.loaded();
            }
        }, 4000);
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

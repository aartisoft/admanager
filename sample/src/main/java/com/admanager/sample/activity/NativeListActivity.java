package com.admanager.sample.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;
import com.admanager.sample.adapter.TrackAdapter;
import com.admanager.sample.adapter.TrackModel;

import java.util.ArrayList;

/**
 * Created by Gust on 20.12.2017.
 */
public class NativeListActivity extends AppCompatActivity {

    TrackAdapter trackAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_list);

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
        trackAdapter.setLoadingFullScreen();
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

}

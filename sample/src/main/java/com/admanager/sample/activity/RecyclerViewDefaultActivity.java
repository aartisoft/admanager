package com.admanager.sample.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.admanager.sample.R;
import com.admanager.sample.adapter.TrackAdapter;
import com.admanager.sample.adapter.TrackModel;

import java.util.ArrayList;

/**
 * Created by Gust on 20.11.2018.
 */
public class RecyclerViewDefaultActivity extends AppCompatActivity {

    TrackAdapter trackAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_list);

        // Recycler View
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layout);

        // set adapter
        trackAdapter = new TrackAdapter(this);
        recyclerView.setAdapter(trackAdapter);

        // divider
        recyclerView.addItemDecoration(new DividerItemDecoration(this, layout.getOrientation()));

        loadTracksAsync();
    }

    private void loadTracksAsync() {
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
        }, 2000);
    }

}

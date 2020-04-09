package com.admanager.sample.activity;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.recyclerview.ClickListener;
import com.admanager.sample.R;
import com.admanager.sample.adapter.TrackAdmobAdapter;
import com.admanager.sample.adapter.TrackModel;

import java.util.ArrayList;

/**
 * Created by Gust on 20.11.2018.
 */
public class RecyclerViewAdmobDefaultActivity extends AppCompatActivity {

    TrackAdmobAdapter trackAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_list);

        // Recycler View
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layout);

        // set adapter
        trackAdapter = new TrackAdmobAdapter(this);
        trackAdapter.setClickListener(new ClickListener<TrackModel>() {
            @Override
            public void clicked(TrackModel model, int position) {
                System.out.println(model);
            }
        });
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

package com.admanager.gpstimeaddresscoord.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.core.Consts;
import com.admanager.gpstimeaddresscoord.GPSTimeApp;
import com.admanager.gpstimeaddresscoord.R;

public class GPSTimeAddressCoordActivity extends AppCompatActivity {

    private RecyclerView mRecycler;
    private ProgressBar mProgress;

    public static void start(Context context) {
        Intent intent = new Intent(context, GPSTimeAddressCoordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        initViews();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setTitle(getString(R.string.gpsTime_address));

        GPSTimeApp instance = GPSTimeApp.getInstance();
        if (instance == null) {
            Log.e(Consts.TAG, "init GPSTime module in Application class");
            finish();
        } else {
            if (instance.ads != null) {
                instance.ads.loadTop(this, (LinearLayout) findViewById(R.id.top));
                instance.ads.loadBottom(this, (LinearLayout) findViewById(R.id.bottom));
            }
        }

    }

    private void initViews() {


    }


}
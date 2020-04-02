package com.admanager.gpstimeaddresscoord.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.core.Consts;
import com.admanager.gpstimeaddresscoord.GPSTimeApp;
import com.admanager.gpstimeaddresscoord.R;

public class GPSTimeAddressCoordActivity extends AppCompatActivity {

    private ProgressBar mProgress;
    private TextView gpsTime, gpsDate, gpsCoord, gpsAddress;
    private ImageView openAddress, saveAddress;
    private RecyclerView recyclerAddress;

    public static void start(Context context) {
        Intent intent = new Intent(context, GPSTimeAddressCoordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adm_gps_activity);
        initViews();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle(getString(R.string.gpsTime_address));
        configureStyle();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void configureStyle() {
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
        gpsTime = findViewById(R.id.gpsTime);
        gpsDate = findViewById(R.id.gpsDate);
        gpsCoord = findViewById(R.id.gpsCoord);
        gpsAddress = findViewById(R.id.gpsAddress);
        openAddress = findViewById(R.id.openAddress);
        saveAddress = findViewById(R.id.saveAddress);
        recyclerAddress = findViewById(R.id.recyclerAddress);
        mProgress = findViewById(R.id.mProgress);

    }


}
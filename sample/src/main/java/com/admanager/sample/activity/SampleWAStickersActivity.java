package com.admanager.sample.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.admanager.sample.R;
import com.admanager.wastickers.WastickersApp;
import com.admanager.wastickers.utils.PermissionChecker;

/**
 * Created by Gust on 20.11.2018.
 */
public class SampleWAStickersActivity extends AppCompatActivity {
    PermissionChecker permissionChecker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wastickers);

        permissionChecker = new PermissionChecker(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        WastickersApp.loadAndBindTo(this, permissionChecker, recyclerView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

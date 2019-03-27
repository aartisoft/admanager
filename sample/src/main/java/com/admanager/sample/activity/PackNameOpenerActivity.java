package com.admanager.sample.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.admanager.admob.AdmobAdapter;
import com.admanager.core.AdManager;
import com.admanager.core.AdManagerBuilder;
import com.admanager.core.DummyAdapter;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;

/**
 * Created by Gust on 20.11.2018.
 */
public class PackNameOpenerActivity extends AppCompatActivity implements View.OnClickListener {

    private AdManager adManager;
    private String packageName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_name_opener);

        findViewById(R.id.btn_whatsapp).setOnClickListener(this);
        findViewById(R.id.btn_gmail).setOnClickListener(this);

        loadAdChain();
    }

    private void loadAdChain() {
        adManager = new AdManagerBuilder(this)
                .add(new AdmobAdapter(RCUtils.MAIN_ADMOB_ENABLED).withRemoteConfigId(RCUtils.MAIN_ADMOB_ID))
                .add(new DummyAdapter(new Runnable() {
                    @Override
                    public void run() {
                        PackNameRedirectingActivity.redirectTo(PackNameOpenerActivity.this, packageName);
                    }
                }))
                .build();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_gmail) {
            packageName = "com.google.android.gm";
        } else if (id == R.id.btn_whatsapp) {
            packageName = "com.whatsapp";
        }

        //display ads
        adManager.show();

        // load again
        loadAdChain();
    }

}

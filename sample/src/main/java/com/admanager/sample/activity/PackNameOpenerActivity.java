package com.admanager.sample.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.admanager.admob.AdmobAdapter;
import com.admanager.core.AdManager;
import com.admanager.core.AdManagerBuilder;
import com.admanager.core.Adapter;
import com.admanager.core.DummyAdapter;
import com.admanager.facebook.FacebookAdapter;
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


        adManager = new AdManagerBuilder(this)
                .add(new DummyAdapter())
                .add(new AdmobAdapter(RCUtils.MAIN_ADMOB_ENABLED).withRemoteConfigId(RCUtils.MAIN_ADMOB_ID))
                .add(new FacebookAdapter(RCUtils.MAIN_FACEBOOK_ENABLED).withRemoteConfigId(RCUtils.MAIN_FACEBOOK_ID))
                .listener(new AdManager.AAdapterListener() {
                    @Override
                    public void finished(int order, Class<? extends Adapter> clz, boolean displayed, boolean showOneBarrier) {
                        if (showOneBarrier && !TextUtils.isEmpty(packageName)) {
                            PackNameRedirectingActivity.redirectTo(PackNameOpenerActivity.this, packageName);
                        }
                    }
                })
                .build();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_gmail) {
            showAds("com.google.android.gm");
        } else if (id == R.id.btn_whatsapp) {
            showAds("com.whatsapp");
        } else {
            showAds();
        }
    }

    public void showAds(String packName) {
        packageName = packName;
        adManager.showOne();
    }

    public void showAds() {
        packageName = null;
        adManager.showOne();
    }

}

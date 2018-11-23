package com.admanager.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.admanager.admob.AdmobAdapter;
import com.admanager.core.AdManager;
import com.admanager.facebook.FacebookAdapter;

/**
 * Created by Gust on 20.12.2017.
 */
public class SampleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AdManager build = new AdManager(this)
                .add(new AdmobAdapter("", ""))
                .add(new FacebookAdapter("", ""))
                .build();
        build.show();
    }
}

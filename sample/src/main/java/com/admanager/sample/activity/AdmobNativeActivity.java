package com.admanager.sample.activity;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.admanager.admob.AdmobNativeLoader;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;

/**
 * Created by Gust on 20.11.2018.
 */
public class AdmobNativeActivity extends AppCompatActivity {
    public static final String TAG = "AdmobNative";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_container);

        LinearLayout container = findViewById(R.id.container);

        new AdmobNativeLoader(this, container, RCUtils.NATIVE_ADMOB_ENABLED)
                .withBorder()
                .loadWithRemoteConfigId(RCUtils.NATIVE_ADMOB_ID);
    }


}

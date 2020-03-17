package com.admanager.sample.activity;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.admanager.facebook.FacebookNativeLoader;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;

/**
 * Created by Gust on 20.11.2018.
 */
public class FacebookNativeActivity extends AppCompatActivity {
    public static final String TAG = "FacebookNative";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_container);

        LinearLayout container = findViewById(R.id.container);

        new FacebookNativeLoader(this, container, RCUtils.NATIVE_FACEBOOK_ENABLED)
                .loadWithRemoteConfigId(RCUtils.NATIVE_FACEBOOK_ID);
    }


}

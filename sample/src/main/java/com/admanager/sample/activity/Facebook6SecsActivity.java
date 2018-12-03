package com.admanager.sample.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.admanager.facebook.FacebookAdHelper;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;

/**
 * Created by Gust on 20.11.2018.
 */
public class Facebook6SecsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_6_secs);

        FacebookAdHelper.showNsecInters(6000, this, RCUtils.MAIN_6SEC_FACEBOOK_ENABLED, RCUtils.MAIN_6SEC_FACEBOOK_ID);
    }

}

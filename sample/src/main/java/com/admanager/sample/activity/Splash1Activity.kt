package com.admanager.sample.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.admanager.admob.AdmobAdapter
import com.admanager.config.RemoteConfigHelper
import com.admanager.core.AdManagerBuilder
import com.admanager.facebook.FacebookAdapter
import com.admanager.sample.RCUtils

/**
 * Created by Gust on 20.11.2018.
 */
class Splash1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RemoteConfigHelper.init(this)
        //        If you want to close all ads for testing, use this method
        //RemoteConfigHelper.setAdsEnabled(!BuildConfig.DEBUG);

//        AdmStaticNotification.setClickListener(this, () -> System.out.println("CLICKED")); // you need to call this method in launcher Activity
//        BoosterNotificationApp.setClickListener(this, () -> System.out.println("CLICKED")); // you need to call this method in launcher Activity
        AdManagerBuilder(this)
            .add(AdmobAdapter(RCUtils.s1_admob_enabled).withRemoteConfigId(RCUtils.s1_admob_id))
            .add(FacebookAdapter(RCUtils.s1_facebook_enabled).withRemoteConfigId(RCUtils.s1_facebook_id))
            .thenStart(Splash2Activity::class.java)
            .build()
            .show()
    }
}
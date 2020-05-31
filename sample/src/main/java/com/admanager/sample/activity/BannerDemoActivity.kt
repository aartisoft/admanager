package com.admanager.sample.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.admanager.admob.AdmobBannerLoader
import com.admanager.custombanner.CustomBannerLoader
import com.admanager.facebook.FacebookBannerLoader
import com.admanager.sample.R
import com.admanager.sample.RCUtils
import kotlinx.android.synthetic.main.activity_with_banner.*

/**
 * Created by Gust on 20.11.2018.
 */
class BannerDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_banner)

        FacebookBannerLoader(this, top_1, RCUtils.facebook_banner_enabled)
            .withBorderTop().loadWithRemoteConfigId(RCUtils.facebook_banner_id)

        CustomBannerLoader(this, top_2, RCUtils.custom_banner_enabled)
            .loadWithRemoteConfigId(
                RCUtils.custom_banner_click_url,
                RCUtils.custom_banner_image_url
            )

        AdmobBannerLoader(this, bottom_1, RCUtils.admob_banner_enabled).withBorderBottom()
            .loadWithRemoteConfigId(RCUtils.admob_banner_id)


    }
}
package com.admanager.sample.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.admanager.admob.AdmobBannerLoader
import com.admanager.custombanner.CustomBannerLoader
import com.admanager.facebook.FacebookBannerLoader
import com.admanager.inmobi.InmobiBannerLoader
import com.admanager.mopub.MopubBannerLoader
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

        FacebookBannerLoader(this, facebook_container, RCUtils.facebook_banner_enabled)
            .withBorderTop().loadWithRemoteConfigId(RCUtils.facebook_banner_id)

        AdmobBannerLoader(this, admob_container, RCUtils.admob_banner_enabled).withBorderBottom()
            .loadWithRemoteConfigId(RCUtils.admob_banner_id)

        MopubBannerLoader(this, mopub_container, RCUtils.mopub_banner_enabled).withBorder()
            .loadWithId(getString(R.string.mopub_banner))

        InmobiBannerLoader(this, inmobi_container, RCUtils.inmobi_banner_enabled).withBorder()
            .loadWithId(
                getString(R.string.inmobi_account_id),
                getString(R.string.inmobi_banner).toLong()
            )

        CustomBannerLoader(this, custom_banner_with_loader, RCUtils.custom_banner_enabled)
            .loadWithRemoteConfigId(
                RCUtils.custom_banner_click_url,
                RCUtils.custom_banner_image_url
            )
    }
}
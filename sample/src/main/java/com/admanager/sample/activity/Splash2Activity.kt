package com.admanager.sample.activity

import android.widget.LinearLayout
import com.admanager.admob.AdmobAdapter
import com.admanager.admob.AdmobBannerLoader
import com.admanager.admob.AdmobNativeLoader
import com.admanager.core.AdManagerBuilder
import com.admanager.core.DummyAdapter
import com.admanager.core.tutorial.AdmTutorialActivity
import com.admanager.core.tutorial.AdmTutorialConfiguration
import com.admanager.core.tutorial.NativePosition
import com.admanager.sample.R
import com.admanager.sample.RCUtils

/**
 * Created by Gust on 20.11.2018.
 */
class Splash2Activity : AdmTutorialActivity() {
    override fun configure(): AdmTutorialConfiguration =// use Configuration for custom styling
        AdmTutorialConfiguration(this)
            .nativePosition(NativePosition.TOP, NativePosition.BOTTOM, NativePosition.CENTER)
            .titleColor(R.color.colorPrimary)
            .viewPagerPadding(0)
            .hideButton()

    override fun addTutorialPages() {
        addPage(R.string.tut_desc_1, R.drawable.tut_img_1)
        addPage(R.string.app_name, R.string.tut_desc_2, R.drawable.tut_img_2)
        addPage(R.string.tut_desc_3, R.drawable.tut_img_3)
    }

    override fun loadAd(container: LinearLayout) {
        AdmobNativeLoader(this, container, RCUtils.tutorial_native_admob_enabled)
            .size(AdmobNativeLoader.NativeType.NATIVE_XL)
            .loadWithRemoteConfigId(RCUtils.tutorial_native_admob_id)
    }

    override fun loadTopAd(container: LinearLayout?) {
        AdmobBannerLoader(this, container, RCUtils.admob_banner_enabled)
            .loadWithRemoteConfigId(RCUtils.admob_banner_id)
    }

    override fun loadBottomAd(container: LinearLayout?) {
        AdmobBannerLoader(this, container, RCUtils.admob_banner_enabled)
            .loadWithRemoteConfigId(RCUtils.admob_banner_id)
    }
    override fun createAdManagerBuilder(): AdManagerBuilder = AdManagerBuilder(this)
        .add(DummyAdapter())
        .add(AdmobAdapter(RCUtils.s2_admob_enabled).withRemoteConfigId(RCUtils.s2_admob_id))
        .thenStart(MainActivity::class.java)
}
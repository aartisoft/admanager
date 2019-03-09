package com.admanager.sample.activity;

import android.widget.LinearLayout;

import com.admanager.admob.AdmobAdapter;
import com.admanager.admob.AdmobBannerLoader;
import com.admanager.core.AdManagerBuilder;
import com.admanager.core.tutorial.AdmTutorialActivity;
import com.admanager.core.tutorial.AdmTutorialConfiguration;
import com.admanager.facebook.FacebookAdapter;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;
import com.google.android.gms.ads.AdSize;

/**
 * Created by Gust on 20.11.2018.
 */
public class Splash2Activity extends AdmTutorialActivity {

    @Override
    protected void configure(AdmTutorialConfiguration conf) {
        conf.hideButton();
        // use Configuration for custom styling
    }

    @Override
    protected void addTutorialPages() {
        addPage(R.string.tut_desc_1, R.drawable.tut_img_1);
        addPage(R.string.tut_desc_2, R.drawable.tut_img_2);
        addPage(R.string.tut_desc_3, R.drawable.tut_img_3);
    }

    @Override
    protected void loadAd(LinearLayout container) {
        new AdmobBannerLoader(this, container, RCUtils.ADMOB_BANNER_ENABLED).size(AdSize.MEDIUM_RECTANGLE).loadWithRemoteConfigId(RCUtils.ADMOB_BANNER_ID);
        //        new AdmobNativeLoader(this, container, RCUtils.NATIVE_ADMOB_ENABLED).size(AdmobNativeLoader.NativeType.NATIVE_XL).loadWithRemoteConfigId(RCUtils.NATIVE_ADMOB_ID);
    }

    @Override
    protected AdManagerBuilder createAdManagerBuilder() {
        return new AdManagerBuilder(this)
                .add(new AdmobAdapter(RCUtils.S2_ADMOB_ENABLED).withRemoteConfigId(RCUtils.S2_ADMOB_ID))
                .add(new FacebookAdapter(RCUtils.S2_FACEBOOK_ENABLED).withRemoteConfigId(RCUtils.S2_FACEBOOK_ID))
                .thenStart(MainActivity.class);
    }

}

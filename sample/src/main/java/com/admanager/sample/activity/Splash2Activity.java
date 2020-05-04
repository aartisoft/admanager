package com.admanager.sample.activity;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.admanager.admob.AdmobAdapterWithNative;
import com.admanager.admob.AdmobNativeLoader;
import com.admanager.core.AdManagerBuilder;
import com.admanager.core.tutorial.AdmTutorialActivity;
import com.admanager.core.tutorial.AdmTutorialConfiguration;
import com.admanager.core.tutorial.NativePosition;
import com.admanager.facebook.FacebookAdapter;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;

/**
 * Created by Gust on 20.11.2018.
 */
public class Splash2Activity extends AdmTutorialActivity {

    @NonNull
    @Override
    protected AdmTutorialConfiguration configure() {
        // use Configuration for custom styling
        return new AdmTutorialConfiguration(this)
                .nativePosition(NativePosition.TOP, NativePosition.BOTTOM, NativePosition.CENTER)
                .titleColor(R.color.colorPrimary)
                .viewPagerPadding(0)
                .hideButton();
    }

    @Override
    protected void addTutorialPages() {
        addPage(R.string.tut_desc_1, R.drawable.tut_img_1);
        addPage(R.string.app_name, R.string.tut_desc_2, R.drawable.tut_img_2);
        addPage(R.string.tut_desc_3, R.drawable.tut_img_3);
    }

    @Override
    protected void loadAd(LinearLayout container, int page) {
        new AdmobNativeLoader(this, container, RCUtils.NATIVE_ADMOB_ENABLED).size(AdmobNativeLoader.NativeType.NATIVE_XL).loadWithRemoteConfigId(RCUtils.NATIVE_ADMOB_ID);
    }

    @Override
    protected AdManagerBuilder createAdManagerBuilder() {
        return new AdManagerBuilder(this)
                .add(new AdmobAdapterWithNative(RCUtils.S2_ADMOB_ENABLED).withRemoteConfigId(RCUtils.S2_ADMOB_ID))
                .add(new FacebookAdapter(RCUtils.S2_FACEBOOK_ENABLED).withRemoteConfigId(RCUtils.S2_FACEBOOK_ID))
                .thenStart(MainActivity.class);
    }

}

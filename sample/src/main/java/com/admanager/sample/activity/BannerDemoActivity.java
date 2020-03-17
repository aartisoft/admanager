package com.admanager.sample.activity;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.admanager.admob.AdmobBannerLoader;
import com.admanager.custombanner.CustomBannerLoader;
import com.admanager.facebook.FacebookBannerLoader;
import com.admanager.inmobi.InmobiBannerLoader;
import com.admanager.mopub.MopubBannerLoader;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;

/**
 * Created by Gust on 20.11.2018.
 */
public class BannerDemoActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_banner);

        LinearLayout admobContainer = (LinearLayout) findViewById(R.id.admob_container);
        LinearLayout facebookContainer = (LinearLayout) findViewById(R.id.facebook_container);
        LinearLayout mopubContainer = (LinearLayout) findViewById(R.id.mopub_container);
        LinearLayout inmobiContainer = (LinearLayout) findViewById(R.id.inmobi_container);
        LinearLayout customBannerContainer = (LinearLayout) findViewById(R.id.custom_banner_with_loader);

        new FacebookBannerLoader(this, facebookContainer, RCUtils.FACEBOOK_BANNER_ENABLED).withBorderTop().loadWithRemoteConfigId(RCUtils.FACEBOOK_BANNER_ID);
        new AdmobBannerLoader(this, admobContainer, RCUtils.ADMOB_BANNER_ENABLED).withBorderBottom().loadWithRemoteConfigId(RCUtils.ADMOB_BANNER_ID);
        new MopubBannerLoader(this, mopubContainer, RCUtils.MOPUB_BANNER_ENABLED).withBorder().loadWithId(getString(R.string.mopub_banner));
        new InmobiBannerLoader(this, inmobiContainer, RCUtils.INMOBI_BANNER_ENABLED).withBorder().loadWithId(getString(R.string.inmobi_account_id), Long.parseLong(getString(R.string.inmobi_banner)));
        new CustomBannerLoader(this, customBannerContainer, RCUtils.CUSTOM_BANNER_ENABLED).loadWithRemoteConfigId(RCUtils.CUSTOM_BANNER_CLICK_URL, RCUtils.CUSTOM_BANNER_IMAGE_URL);
    }

}

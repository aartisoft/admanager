package com.admanager.sample.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.admanager.admob.AdmobNativeLoader
import com.admanager.sample.R
import com.admanager.sample.RCUtils
import kotlinx.android.synthetic.main.activity_native_container.*

/**
 * Created by Gust on 20.11.2018.
 */
class AdmobNativeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_container)

        AdmobNativeLoader(this, container, RCUtils.native_admob_enabled)
            .withBorder()
            .loadWithRemoteConfigId(RCUtils.native_admob_id)
    }

    companion object {
        const val TAG = "AdmobNative"
    }
}
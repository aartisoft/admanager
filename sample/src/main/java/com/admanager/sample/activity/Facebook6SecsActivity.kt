package com.admanager.sample.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.admanager.facebook.FacebookAdHelper
import com.admanager.sample.R
import com.admanager.sample.RCUtils

/**
 * Created by Gust on 20.11.2018.
 */
class Facebook6SecsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_6_secs)

        FacebookAdHelper.showNsecInters(
            6000,
            this,
            RCUtils.main_6sec_facebook_enabled,
            RCUtils.main_6sec_facebook_id
        )
    }
}
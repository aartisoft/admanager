package com.admanager.sample.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.admanager.admob.AdmobAdapter
import com.admanager.admob.AdmobRewardedAdapter
import com.admanager.core.AdManager
import com.admanager.core.AdManagerBuilder
import com.admanager.core.DummyAdapter
import com.admanager.sample.R
import com.admanager.sample.RCUtils
import com.admanager.sample.logd
import com.admanager.sample.toast
import kotlinx.android.synthetic.main.activity_main_inters.*

/**
 * Created by Gust on 20.11.2018.
 */
class MainIntersActivity : AppCompatActivity(), View.OnClickListener {
    var adManager: AdManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_inters)
        adManager = AdManagerBuilder(this)
            .add(DummyAdapter())
            .add(AdmobAdapter(RCUtils.main_admob_enabled).withRemoteConfigId(RCUtils.main_admob_id))
            .add(
                AdmobRewardedAdapter(RCUtils.main_popup_rewarded_enabled).withRemoteConfigId(RCUtils.main_popup_rewarded_id)
                    .setUserRewardedListener { amount: Int ->
                        toast("Here you go! You won $amount reward!")
                        logd("Here you go! You won $amount reward!")
                    }
            )
            .build()
        any_action.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.any_action ->  // any_action();
                adManager?.show()
        }
    }
}
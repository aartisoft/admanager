package com.admanager.sample.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.admanager.admob.AdmobAdapter
import com.admanager.admob.AdmobRewardedAdapter
import com.admanager.core.AdManager
import com.admanager.core.AdManagerBuilder
import com.admanager.core.DummyAdapter
import com.admanager.facebook.FacebookAdapter
import com.admanager.sample.R
import com.admanager.sample.RCUtils
import com.admanager.sample.logd
import com.admanager.sample.toast
import com.admanager.unity.UnityAdapter
import kotlinx.android.synthetic.main.activity_main_inters.*
import java.util.concurrent.TimeUnit

/**
 * Created by Gust on 20.11.2018.
 */
class MainIntersActivity : AppCompatActivity(), View.OnClickListener {
    var adManager: AdManager? = null
    var unityOnExit: AdManager? = null
    var unityOnResume: AdManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_inters)
        adManager = AdManagerBuilder(this)
            .add(DummyAdapter())
            .add(AdmobAdapter(RCUtils.main_admob_enabled).withRemoteConfigId(RCUtils.main_admob_id))
            .add(FacebookAdapter(RCUtils.main_facebook_enabled).withRemoteConfigId(RCUtils.main_facebook_id))
            .add(
                AdmobRewardedAdapter(RCUtils.main_popup_rewarded_enabled).withRemoteConfigId(RCUtils.main_popup_rewarded_id)
                    .setUserRewardedListener { amount: Int ->
                        toast("Here you go! You won $amount reward!")
                        logd("Here you go! You won $amount reward!")
                    }
            )
            .build()
        unityOnExit = AdManagerBuilder(this).add(
            UnityAdapter(RCUtils.onexit_unity_enabled).withId(
                getString(R.string.unity_game_id),
                getString(R.string.unity_placement_id_onexit)
            )
        )
            .build()
        unityOnResume = AdManagerBuilder(this).add(
            UnityAdapter(RCUtils.onresume_unity_enabled).withId(
                getString(R.string.unity_game_id),
                getString(R.string.unity_placement_id_onresume)
            )
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

    override fun onResume() {
        super.onResume()
        unityOnResume?.showOneByTimeCap(TimeUnit.MINUTES.toMillis(1))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        unityOnExit?.showAndFinish()
    }
}
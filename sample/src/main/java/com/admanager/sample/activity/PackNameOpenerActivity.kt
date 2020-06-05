package com.admanager.sample.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.admanager.admob.AdmobAdapter
import com.admanager.core.AdManager
import com.admanager.core.AdManagerBuilder
import com.admanager.core.DummyAdapter
import com.admanager.sample.R
import com.admanager.sample.RCUtils
import com.admanager.sample.activity.PackNameRedirectingActivity.Companion.redirectTo
import kotlinx.android.synthetic.main.activity_pack_name_opener.*

/**
 * Created by Gust on 20.11.2018.
 */
class PackNameOpenerActivity : AppCompatActivity(),
    View.OnClickListener {
    private var adManager: AdManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pack_name_opener)

        btn_whatsapp.setOnClickListener(this)
        btn_gmail.setOnClickListener(this)

        adManager = AdManagerBuilder(this)
            .add(DummyAdapter())
            .add(AdmobAdapter(RCUtils.main_admob_enabled).withRemoteConfigId(RCUtils.main_admob_id))
            .build()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_gmail -> showAds("com.google.android.gm")
            R.id.btn_whatsapp -> showAds("com.whatsapp")
            else -> showAds()
        }
    }

    private fun showAds(packName: String?) {
        adManager?.showOne {
            redirectTo(
                this@PackNameOpenerActivity,
                packName
            )
        }
    }

    private fun showAds() {
        adManager?.showOne()
    }
}
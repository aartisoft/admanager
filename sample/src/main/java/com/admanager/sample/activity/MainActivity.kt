package com.admanager.sample.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.admanager.sample.R
import com.admanager.sample.activity.SampleMainActivity
import com.admanager.sample.logd
import com.admanager.sample.toast
import java.util.*

/**
 * Created by Gust on 20.11.2018.
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {
    var clickMap = HashMap<Int, Class<out Activity?>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        clickMap[R.id.ex_inters] = MainIntersActivity::class.java
        clickMap[R.id.ex_face_6_sec] = Facebook6SecsActivity::class.java
        clickMap[R.id.ex_banner_demo] = BannerDemoActivity::class.java
        clickMap[R.id.ex_admob_native] = AdmobNativeActivity::class.java
        clickMap[R.id.ex_face_native] = FacebookNativeActivity::class.java
        clickMap[R.id.ex_recycler_view_admob_default] = RecyclerViewAdmobDefaultActivity::class.java
        clickMap[R.id.ex_recycler_view_default] = RecyclerViewDefaultActivity::class.java
        clickMap[R.id.ex_recycler_view_density] = RecyclerViewDensityActivity::class.java
        clickMap[R.id.ex_recycler_view_grid] = RecyclerViewGridActivity::class.java
        clickMap[R.id.ex_recycler_view_bignative] = RecyclerViewBigNativeActivity::class.java
        clickMap[R.id.ex_recycler_view_custom_design] = RecyclerViewCustomDesignActivity::class.java
        clickMap[R.id.ex_recycler_view_custom] = RecyclerViewCustomActivity::class.java
        clickMap[R.id.ex_sample_main] = SampleMainActivity::class.java
        clickMap[R.id.ex_pack_name_opener] = PackNameOpenerActivity::class.java
        for (i in clickMap.keys) {
            findViewById<View>(i).setOnClickListener(this)
        }
    }

    override fun onClick(v: View) {
        val id = v.id
        val activityClass = clickMap[id]
        if (activityClass == null) {
            toast("Add activity to map")
            logd("Add activity to map")
            return
        }
        startActivity(Intent(this, activityClass))
    }
}
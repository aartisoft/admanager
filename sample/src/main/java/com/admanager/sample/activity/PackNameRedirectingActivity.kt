package com.admanager.sample.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.admanager.config.RemoteConfigHelper
import com.admanager.core.AdmUtils
import com.admanager.sample.R
import com.admanager.sample.RCUtils

/**
 * Created by Gust on 20.11.2018.
 */
class PackNameRedirectingActivity : AppCompatActivity() {
    /*
    *  BU ACTIVITY İÇERİSİNDE;
    *  * Kullanici hangi̇ app e tikladi,
    *  * Nereye yönlendi̇ri̇lecek,
    *  * Toplam kaç kere tikladi
    *  GİBİ BİLGİLER GÖSTERİLEBİLİR.
    * */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opener_post_splash)
        Handler().postDelayed({
            val intent =
                packageManager.getLaunchIntentForPackage(pn!!)
            if (intent != null) {
                startActivity(intent)
            } else {
                AdmUtils.openApp(
                    this@PackNameRedirectingActivity,
                    pn
                )
            }
            finish()
        }, RemoteConfigHelper.getConfigs().getLong(RCUtils.opener_delay))
    }

    companion object {

        private var pn: String? = null

        @JvmStatic
        fun redirectTo(context: Context, packageName: String?) {
            pn = packageName
            val intent =
                Intent(context, PackNameRedirectingActivity::class.java)
            context.startActivity(intent)
        }
    }
}
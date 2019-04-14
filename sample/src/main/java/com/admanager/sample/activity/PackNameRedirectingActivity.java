package com.admanager.sample.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.admanager.config.RemoteConfigHelper;
import com.admanager.core.AdmUtils;
import com.admanager.sample.R;
import com.admanager.sample.RCUtils;

/**
 * Created by Gust on 20.11.2018.
 */
public class PackNameRedirectingActivity extends AppCompatActivity {

    /*
     * BU ACTIVITY İÇERİSİNDE;
     *  * Kullanici hangi̇ app e tikladi,
     *  * Nereye yönlendi̇ri̇lecek,
     *  * Toplam kaç kere tikladi
     *  GİBİ BİLGİLER GÖSTERİLEBİLİR.
     * */

    private static String pn;

    public static void redirectTo(Context context, String packageName) {
        pn = packageName;
        Intent intent = new Intent(context, PackNameRedirectingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opener_post_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = getPackageManager().getLaunchIntentForPackage(pn);
                if (intent != null) {
                    startActivity(intent);
                } else {
                    AdmUtils.openApp(PackNameRedirectingActivity.this, pn);
                }
                finish();
            }
        }, RemoteConfigHelper.getConfigs().getLong(RCUtils.OPENER_DELAY));
    }
}

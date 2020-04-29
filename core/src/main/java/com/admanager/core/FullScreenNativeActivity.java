package com.admanager.core;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class FullScreenNativeActivity extends Activity implements View.OnClickListener {

    public static final String PARAM_KEY = "key";
    public static final String PARAM_CLOSE_TIME_TO_HIDE = "closeTimeToHide";
    private static final String TAG = "FullScNativeAct";
    private String key;
    private long timeToHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.adm_full_screen_native_activity);

        key = getIntent().getStringExtra(PARAM_KEY);
        timeToHide = getIntent().getLongExtra(PARAM_CLOSE_TIME_TO_HIDE, 0);

        ViewGroup adContainer = findViewById(R.id.ad);
        AdapterWithNative.bindAdsToContainer(key, adContainer);

        ImageView imageView = findViewById(R.id.app_icon);
        TextView textView = findViewById(R.id.app_name);

        // get name
        ApplicationInfo applicationInfo = getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        String appname = stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : getString(stringId);
        textView.setText(appname);

        //get icon
        try {
            Drawable icon = getPackageManager().getApplicationIcon(getPackageName());
            imageView.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        final View close = findViewById(R.id.close);
        close.setOnClickListener(this);
        if (timeToHide > 0) {
            close.setVisibility(View.INVISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    close.setVisibility(View.VISIBLE);
                }
            }, timeToHide);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.close) {
            closeAd();
        }
    }

    @Override
    public void onBackPressed() {
        closeAd();
//        super.onBackPressed();
    }

    private void closeAd() {
        AdapterWithNative.adClosed(key);
        finish();
    }
}
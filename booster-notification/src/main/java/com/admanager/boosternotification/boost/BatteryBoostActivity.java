package com.admanager.boosternotification.boost;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.admanager.boosternotification.BoosterNotificationApp;
import com.admanager.boosternotification.R;
import com.airbnb.lottie.LottieAnimationView;

import java.util.List;

public class BatteryBoostActivity extends AppCompatActivity {

    private static final String TAG = "RamBoostActivity";
    LottieAnimationView lottieView;
    TextView txtResult;
    TextView txtResult2;
    TextView txtStatus;
    private int color = 0xFF30E471;
    private Button btnClose;
    private BoosterNotificationApp.Ads ads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boost);

        ads = BoosterNotificationApp.getInstance().getAds();
        ads.loadBottom(this, (LinearLayout) findViewById(R.id.bottom_container));
        ads.loadTop(this, (LinearLayout) findViewById(R.id.top_container));

        lottieView = findViewById(R.id.lottieView);
        txtResult = findViewById(R.id.txtResult);
        txtResult2 = findViewById(R.id.txtResult2);
        txtStatus = findViewById(R.id.txtStatus);
        txtStatus.setBackground(Utils.createOvalDrawable(color));
        btnClose = findViewById(R.id.btnClose);

        txtResult.setText(R.string.bba_scanning);
        btnClose.setBackground(Utils.createRoundedStrokeDrawable(color));
        btnClose.setTextColor(color);


        final int scannedApps = scanApps();

        lottieView.setAnimation("battery.json");
        lottieView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                txtResult.setText(R.string.bba_optimized);
                txtResult2.setText(getResources().getString(R.string.bba_result, scannedApps));
                txtStatus.setText("\u2713");
                findViewById(R.id.btnClose).setVisibility(View.VISIBLE);
            }
        });
        lottieView.playAnimation();

        startTxtStatusAnimaiton(scannedApps);
    }

    private void startTxtStatusAnimaiton(int amount) {
        ValueAnimator animator = ValueAnimator.ofInt(0, amount);
        animator.setDuration(5000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                txtStatus.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.setStartDelay(100);
        animator.start();
    }

    private int scanApps() {
        int numScannedApps = 0;
        List<ApplicationInfo> packages;
        //get a list of installed apps.
        packages = getPackageManager().getInstalledApplications(0);
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ApplicationInfo packageInfo : packages) {
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) continue;
            if (packageInfo.packageName.equals(getApplicationContext().getPackageName())) continue;
            if (mActivityManager != null) {
                mActivityManager.killBackgroundProcesses(packageInfo.packageName);
                numScannedApps++;
            }
        }
        return numScannedApps;
    }


    public void close(View v) {
        PackageManager pm = getPackageManager();
        Intent launchIntentForPackage = pm.getLaunchIntentForPackage(getPackageName());
        launchIntentForPackage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(launchIntentForPackage);
        finish();
    }

    @Override
    public void onBackPressed() {
        close(null);
    }
}


package com.admanager.applocker.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.admanager.applocker.AppLockerApp;
import com.admanager.applocker.R;

import static com.admanager.applocker.utils.AppLockInitializer.REQ_CODE_PASSWORD;
import static com.admanager.applocker.utils.AppLockInitializer.REQ_CODE_PASSWORD_TYPE;

public class PasswordTypeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String ASK_RECOVERY = "ask_recovery";
    private static final String PASSWORD_SET = "password_set";

    boolean askRecovery = false;
    AppLockerApp.Ads ads;

    public static void startPasswordSet(Activity activity, boolean askRecovery) {
        Intent i = new Intent(activity, PasswordTypeActivity.class);
        i.putExtra(ASK_RECOVERY, askRecovery);
        activity.startActivityForResult(i, REQ_CODE_PASSWORD_TYPE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_password_type);

        ads = AppLockerApp.getInstance().getAds();
        ads.loadBottom(this, (LinearLayout) findViewById(R.id.bottom_container));
        ads.loadTop(this, (LinearLayout) findViewById(R.id.top_container));

        askRecovery = getIntent().getBooleanExtra(ASK_RECOVERY, false);

        findViewById(R.id.pattern_container).setOnClickListener(this);
        findViewById(R.id.pin_container).setOnClickListener(this);
    }

    public void startPasswordSet(Class<? extends PasswordActivity> cls) {
        Intent i = new Intent(this, cls);
        i.putExtra(ASK_RECOVERY, askRecovery);
        i.putExtra(PASSWORD_SET, true);
        startActivityForResult(i, REQ_CODE_PASSWORD);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Class<? extends PasswordActivity> cls = null;
        if (id == R.id.pin_container) {
            cls = PasswordPinActivity.class;
        } else if (id == R.id.pattern_container) {
            cls = PasswordPatternActivity.class;
        }
        if (cls != null) {
            startPasswordSet(cls);
            finish();
        }
    }
}

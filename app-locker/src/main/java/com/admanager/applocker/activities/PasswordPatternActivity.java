package com.admanager.applocker.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.admanager.applocker.AppLockerApp;
import com.admanager.applocker.R;
import com.admanager.applocker.utils.PasswordType;
import com.admanager.core.Ads;
import com.takwolf.android.lock9.Lock9View;

public class PasswordPatternActivity extends PasswordActivity implements View.OnClickListener, Lock9View.CallBack {
    Lock9View lock9View;
    Button actionButton;
    TextView textView;
    int trying = 0;
    String enteredPassword;
    private Ads ads;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_password_pattern);

        ads = AppLockerApp.getInstance().getAds();
        ads.loadBottom(this, (LinearLayout) findViewById(R.id.bottom_container));
        ads.loadTop(this, (LinearLayout) findViewById(R.id.top_container));

        fillExtras();

        lock9View = findViewById(R.id.lock_9_view);
        actionButton = findViewById(R.id.action);
        actionButton.setText(passwordSet ? R.string.password_retry : R.string.forget_password);
        textView = findViewById(R.id.textView);
        lock9View.setCallBack(this);

        actionButton.setOnClickListener(this);

        if (passwordSet) {
            actionButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (passwordSet) {
            onClickRetry(v);
        } else {
            actionForgotPass();
        }
    }

    @Override
    public void onFinish(String password) {
        if (passwordSet) {
            onFinishSetPass(password);
        } else {
            validatePass(password);
        }
    }

    /**
     * password set action
     */
    public void onClickRetry(View v) {
        int id = v.getId();
        if (id == actionButton.getId()) {
            trying = 0;

            textView.setText(getString(R.string.password_pattern_draw));
            actionButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * password set action
     */
    public void onFinishSetPass(String password) {
        actionButton.setVisibility(View.VISIBLE);
        if (trying == 0) {
            trying++;
            enteredPassword = password;
            textView.setText(R.string.password_pattern_redraw);
        } else if (trying > 0) {
            if (enteredPassword.matches(password)) {
                savePass(enteredPassword, PasswordType.PATTERN);
            } else {
                Toast.makeText(getApplicationContext(), "Both Pattern did not match - Try again", Toast.LENGTH_SHORT).show();
                trying = 0;
                textView.setText(R.string.password_pattern_draw);
                actionButton.setVisibility(View.INVISIBLE);
            }
        }
    }
}

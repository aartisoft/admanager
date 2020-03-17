package com.admanager.applocker.activities;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.admanager.applocker.AppLockerApp;
import com.admanager.applocker.R;
import com.admanager.applocker.utils.PasswordType;
import com.admanager.core.Ads;

public class PasswordPinActivity extends PasswordActivity implements View.OnClickListener {
    Button[] pins;
    Button reset;
    Button clear;
    Button actionButton;
    TextView textView;
    int trying = 0;
    String lastPass;
    String currentPass;
    LinearLayout pinPlaceholder;
    private Ads ads;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_password_pin);

        ads = AppLockerApp.getInstance().getAds();
        ads.loadBottom(this, (LinearLayout) findViewById(R.id.bottom_container));
        ads.loadTop(this, (LinearLayout) findViewById(R.id.top_container));

        pinPlaceholder = findViewById(R.id.pinPlaceholder);
        fillExtras();
        updateEnterDots();

        int ids[] = new int[]{R.id.pin_0, R.id.pin_1, R.id.pin_2, R.id.pin_3, R.id.pin_4, R.id.pin_5, R.id.pin_6, R.id.pin_7, R.id.pin_8, R.id.pin_9};
        pins = new Button[ids.length];
        for (int i = 0; i < ids.length; i++) {
            pins[i] = findViewById(ids[i]);
            pins[i].setOnClickListener(this);
        }

        reset = findViewById(R.id.reset);
        clear = findViewById(R.id.clear);
        clear.setOnClickListener(this);
        reset.setOnClickListener(this);

        actionButton = findViewById(R.id.action);
        actionButton.setText(passwordSet ? R.string.password_retry : R.string.forget_password);
        textView = findViewById(R.id.textView);

        actionButton.setOnClickListener(this);

        if (passwordSet) {
            actionButton.setVisibility(View.INVISIBLE);
        }
    }

    private void updateEnterDots() {
        if (currentPass == null) {
            currentPass = "";
        }
        pinPlaceholder.removeAllViews();

        for (int i = 0; i < 4; i++) {
            int color = R.color.appLockColorText;
            if (i < currentPass.length()) {
                color = R.color.appLockColorAccent;
            }
            color = ContextCompat.getColor(this, color);

            TextView child = new TextView(this);
            child.setText("•");
            child.setTextSize(96);
            child.setTextColor(color);
            child.setPadding(20, 0, 20, 0);
            pinPlaceholder.addView(child);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == actionButton.getId()) {

            if (passwordSet) {
                onClickRetry(v);
            } else {
                actionForgotPass();
            }
        } else if (id == clear.getId()) {
            if (currentPass == null) {
                currentPass = "";
            }
            if (currentPass.length() > 0) {
                currentPass = currentPass.substring(0, currentPass.length() - 1);
            }
        } else if (id == reset.getId()) {
            currentPass = "";
        } else if (v instanceof Button) {
            if (currentPass == null) {
                currentPass = "";
            }

            CharSequence text = ((Button) v).getText();
            currentPass += text;
            if (currentPass.length() == 4) {
                onFinish(currentPass);
            }
        }
        updateEnterDots();
    }

    @Override
    public void onFinish(String password) {
        if (passwordSet) {
            onFinishSetPass(password);
        } else {
            validatePass(password);
        }
        this.currentPass = "";
    }

    /**
     * password set action
     */
    public void onClickRetry(View v) {
        int id = v.getId();
        if (id == actionButton.getId()) {
            trying = 0;

            textView.setText(getString(R.string.enter_pin));
            actionButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * currentPass set action
     */
    public void onFinishSetPass(String currentPass) {
        actionButton.setVisibility(View.VISIBLE);
        if (trying == 0) {
            trying++;
            lastPass = currentPass;
            textView.setText(R.string.enter_pin_re);
        } else if (trying > 0) {
            if (lastPass.matches(currentPass)) {
                savePass(lastPass, PasswordType.PIN);
            } else {
                Toast.makeText(getApplicationContext(), "Both PIN did not match - Try again", Toast.LENGTH_SHORT).show();
                trying = 0;
                textView.setText(R.string.enter_pin);
                actionButton.setVisibility(View.INVISIBLE);
            }
        }
    }
}

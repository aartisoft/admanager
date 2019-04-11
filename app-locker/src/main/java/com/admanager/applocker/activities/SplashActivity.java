package com.admanager.applocker.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.admanager.applocker.R;
import com.admanager.applocker.prefrence.Prefs;

public class SplashActivity extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.locker_activity_splash);

        Intent intent;
        boolean passwordSet = Prefs.with(this).isPasswordSet();
        if (passwordSet) {
            intent = PasswordActivity.intentPasswordAsk(this, null);
        } else {
            intent = new Intent(this, HomeActivity.class);
        }
        startActivity(intent);
        finish();
    }


}

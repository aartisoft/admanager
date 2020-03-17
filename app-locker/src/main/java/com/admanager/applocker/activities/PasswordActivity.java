package com.admanager.applocker.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.admanager.applocker.prefrence.Prefs;
import com.admanager.applocker.services.AppCheckServices;
import com.admanager.applocker.utils.PasswordType;
import com.takwolf.android.lock9.Lock9View;

public abstract class PasswordActivity extends AppCompatActivity implements View.OnClickListener, Lock9View.CallBack {
    private static final String ASK_FOR_PACKAGE = "ask_for_package";
    private static final String ASK_RECOVERY = "ask_recovery";
    private static final String PASSWORD_SET = "password_set";
    String askForPackage;
    boolean passwordSet;
    boolean askRecovery;
    boolean success;

    public static void startPasswordSet(Activity activity, boolean askRecovery) {
        PasswordTypeActivity.startPasswordSet(activity, askRecovery);
    }

    public static Intent intentPasswordAsk(Context context, String askForPackage) {
        PasswordType type = Prefs.with(context).getPasswordType();

        Class<? extends PasswordActivity> cls = PasswordPinActivity.class;
        if (type.equals(PasswordType.PATTERN)) {
            cls = PasswordPatternActivity.class;
        }

        Intent intent = new Intent(context, cls);
        intent.putExtra(ASK_FOR_PACKAGE, askForPackage);
        intent.putExtra(PASSWORD_SET, false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        return intent;
    }

    public static void startPasswordAsk(Context context, String askForPackage) {
        Intent intent = intentPasswordAsk(context, askForPackage);
        context.startActivity(intent);
    }

    void fillExtras() {
        askForPackage = getIntent().getStringExtra(ASK_FOR_PACKAGE);
        passwordSet = getIntent().getBooleanExtra(PASSWORD_SET, false);
        askRecovery = getIntent().getBooleanExtra(ASK_RECOVERY, false);
    }

    /**
     * password check action
     */
    public void actionForgotPass() {
        success = true;
        PasswordRecoveryActivity.startActivity(this, true);
    }

    /**
     * password set action
     *
     * @param enteredPassword
     */
    public void savePass(String enteredPassword, PasswordType type) {
        Prefs.with(this).savePassword(enteredPassword);
        Prefs.with(this).passwordSet(true);
        Prefs.with(this).setPasswordType(type);

        if (askRecovery) {
            PasswordRecoveryActivity.startActivity(this, false);
        }
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (askForPackage != null) {
            AppCheckServices.removedAsked(askForPackage);
        }
        if (!success && askForPackage != null) {
            //            AppLockConstants.startHomeLauncher(this);
            //            finish();
        }
        if (isFinishing()) {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    /**
     * password check action
     */
    public void validatePass(String password) {
        boolean matches = Prefs.with(getApplicationContext()).getPassword().matches(password);
        if (!matches) {
            Toast.makeText(getApplicationContext(), "Wrong Password Try Again", Toast.LENGTH_SHORT).show();
            return;
        }
        if (askForPackage == null) {
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
        } else {
            AppCheckServices.setAsUnlocked(askForPackage);
            success = true;
        }
        finish();
    }
}

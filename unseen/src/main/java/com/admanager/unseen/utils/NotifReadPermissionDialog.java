package com.admanager.unseen.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.admanager.unseen.R;

public class NotifReadPermissionDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "unseen_notif_dialog";

    public NotifReadPermissionDialog(Activity activity) {
        super(activity);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.adm_unseen_permission_dialog);

        TextView message = findViewById(R.id.message);
        String appName = "";
        try {
            appName = getContext().getString(getContext().getApplicationInfo().labelRes);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        message.setText(String.format("%s\n%s", getContext().getString(R.string.unseen_perm_req_text_1), getContext().getString(R.string.unseen_perm_req_text_2, appName)));

        Button read_btn = findViewById(R.id.read_btn);
        read_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_btn) {
            if (Build.VERSION.SDK_INT >= 18) {
                try {
                    getContext().startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                    return;
                } catch (Throwable e) {
                    Log.e("AccessibilityUtils", "Notification listeners activity not found.", e);
                }
            }
            try {
                getContext().startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            } catch (Throwable e) {
                Log.e("AccessibilityUtils", "Accessibility settings not found!", e);
            }
        }

//        dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onstart");
    }
}


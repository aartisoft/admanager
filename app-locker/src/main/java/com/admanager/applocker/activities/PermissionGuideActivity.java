package com.admanager.applocker.activities;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.admanager.applocker.R;

public class PermissionGuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_guide);

        // close Guide when click
        findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // set application name
        TextView txvMessage = findViewById(R.id.txvMessage);
        String appName = getString(R.string.app_name);
        txvMessage.setText(getString(R.string.enable_accessibility_service_guide, appName, appName));

        // set application icon
        ImageView imvIcon = findViewById(R.id.imvIcon);
        try {
            Drawable icon = getPackageManager().getApplicationIcon(getPackageName());
            imvIcon.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}

package com.admanager.equalizer.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.admanager.equalizer.R;
import com.admanager.musicplayer.utilities.Constants;
import com.admanager.musicplayer.utilities.ContextUtils;
import com.admanager.musicplayer.utilities.ExternalStorageUtil;
import com.admanager.musicplayer.utilities.SharedPrefUtils;

public class MPSplashActivity extends AppCompatActivity {
    private Uri actionUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mp_activity_splash);

        SharedPrefUtils.setFirstStarted(this, true);
        SharedPrefUtils.setFirstSetEqualizer(this, true);

        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();

            if (action != null && action.equalsIgnoreCase("android.intent.action.VIEW")) {
                SharedPrefUtils.setFirstActionView(this, true);

                actionUri = intent.getData();
            } else {
                SharedPrefUtils.setFirstActionView(this, false);
            }
        }

        checkPermissions();
    }

    private void checkPermissions() {
        if (ExternalStorageUtil.isExternalStorageMounted()) {
            // If do not grant write external storage permission.
            if (ContextCompat.checkSelfPermission(MPSplashActivity.this, Constants.PERMISSIONS_STORAGE[0]) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MPSplashActivity.this, Constants.PERMISSIONS_STORAGE[1]) != PackageManager.PERMISSION_GRANTED) {

                // Request user to grant write external storage permission.
                ActivityCompat.requestPermissions(MPSplashActivity.this, Constants.PERMISSIONS_STORAGE, Constants.REQUEST_CODE_READ_AND_WRITE_EXTERNAL_STORAGE_PERMISSION);
            } else {
                startMainActivity();

                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.REQUEST_CODE_READ_AND_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                if (ContextUtils.isContextValid(this)) {
                    Toast.makeText(MPSplashActivity.this, getResources().getString(R.string.mp_read_granted), Toast.LENGTH_LONG).show();
                }

                startMainActivity();

                finish();
            } else {
                if (ContextUtils.isContextValid(this)) {
                    Toast.makeText(MPSplashActivity.this, getResources().getString(R.string.mp_not_granted), Toast.LENGTH_LONG).show();
                }

                startActivity(new Intent(this, MPWarningActivity.class));

                finish();
            }
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(MPSplashActivity.this, EqoVolActivity.class);
        intent.putExtra(Constants.ACTION_URI_NAME, actionUri);

        startActivity(intent);
    }
}

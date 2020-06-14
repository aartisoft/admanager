package com.admanager.equalizer.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
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

public class MPWarningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mp_activity_warning);

        findViewById(R.id.waPermission).setOnClickListener(view -> checkPermissions());
    }

    private void checkPermissions() {
        if (ExternalStorageUtil.isExternalStorageMounted()) {
            // If do not grant write external storage permission.
            if (ContextCompat.checkSelfPermission(MPWarningActivity.this, Constants.PERMISSIONS_STORAGE[0]) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MPWarningActivity.this, Constants.PERMISSIONS_STORAGE[1]) != PackageManager.PERMISSION_GRANTED) {

                // Request user to grant write external storage permission.
                ActivityCompat.requestPermissions(this, Constants.PERMISSIONS_STORAGE, Constants.REQUEST_CODE_READ_AND_WRITE_EXTERNAL_STORAGE_PERMISSION);
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
                    Toast.makeText(MPWarningActivity.this, getResources().getString(R.string.mp_read_granted), Toast.LENGTH_LONG).show();
                }

                startActivity(new Intent(this, EqoVolActivity.class));

                finish();
            } else {
                if (ContextUtils.isContextValid(this)) {
                    Toast.makeText(MPWarningActivity.this, getResources().getString(R.string.mp_not_granted), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}


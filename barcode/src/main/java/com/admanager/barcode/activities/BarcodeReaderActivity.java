package com.admanager.barcode.activities;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.admanager.barcode.BarcodeReaderApp;
import com.admanager.barcode.BarcodeReaderFragment;
import com.admanager.barcode.R;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

public class BarcodeReaderActivity extends AppCompatActivity implements BarcodeReaderFragment.BarcodeReaderListener {
    private TextView mTvResult;
    private ClipboardManager clipboard;

    public static void start(Context context) {
        Intent intent = new Intent(context, BarcodeReaderActivity.class);
        context.startActivity(intent);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        mTvResult = findViewById(R.id.tv_result);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        BarcodeReaderApp instance = BarcodeReaderApp.getInstance();
        if (instance != null) {
            if (instance.ads != null) {
                instance.ads.loadTop(this, (LinearLayout) findViewById(R.id.top));
                instance.ads.loadBottom(this, (LinearLayout) findViewById(R.id.bottom));
            }
            if (instance.title != null) {
                setTitle(instance.title);
            }

            if (instance.bgColor != 0) {
                findViewById(R.id.root).setBackgroundColor(ContextCompat.getColor(this, instance.bgColor));
            }
        }

        BarcodeReaderFragment readerFragment = BarcodeReaderFragment.newInstance(true, false, View.VISIBLE);
        readerFragment.setListener(this);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fm_container, readerFragment);
        fragmentTransaction.commitAllowingStateLoss();

        mTvResult.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mTvResult.setMaxLines(2);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mTvResult.setMaxLines(Integer.MAX_VALUE);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScanned(Barcode barcode) {
        if (barcode == null) {
            return;
        }
        mTvResult.setText(barcode.rawValue);

        ClipData clip = ClipData.newPlainText("label", barcode.rawValue);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, getString(R.string.copied_to_clipboard), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {

    }

    @Override
    public void onCameraPermissionDenied() {

    }


}
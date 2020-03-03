package com.admanager.gpssatellite.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.admanager.gpssatellite.GPSSatelliteApp;
import com.admanager.gpssatellite.R;

public class GPSSatelliteActivity extends AppCompatActivity {
    private static final int BIGDECIMAL_ROUND_SCALE = 1;
    ImageView needle;
    ImageView compass;
    Button start;
    TextView rate;
    ProgressBar progressBar;

    private double currentNeedlePosition = 0;

    public static void start(Context context) {
        Intent intent = new Intent(context, GPSSatelliteActivity.class);
        context.startActivity(intent);
    }

    public static double getAngle(double bitsPerSecondDouble) {
        double angle = 0;

        if (bitsPerSecondDouble >= 0.0 && bitsPerSecondDouble < 1.0f) {
            angle = bitsPerSecondDouble * 30.0f;
        } else if (bitsPerSecondDouble >= 1.0 && bitsPerSecondDouble < 5.0f) {
            angle = 30 + (bitsPerSecondDouble - 1) / 4.0f * 30.0f;
        } else if (bitsPerSecondDouble >= 5.0 && bitsPerSecondDouble < 10.0f) {
            angle = 60 + (bitsPerSecondDouble - 5) / 5.0f * 30.0f;
        } else if (bitsPerSecondDouble >= 10.0 && bitsPerSecondDouble < 20.0f) {
            angle = 90 + (bitsPerSecondDouble - 10) / 10.0f * 30.0f;
        } else if (bitsPerSecondDouble >= 20.0 && bitsPerSecondDouble < 30.0f) {
            angle = 120 + (bitsPerSecondDouble - 20) / 10.0f * 30.0f;
        } else if (bitsPerSecondDouble >= 30.0 && bitsPerSecondDouble < 50.0f) {
            angle = 150 + (bitsPerSecondDouble - 30) / 20.0f * 30.0f;
        } else if (bitsPerSecondDouble >= 50.0 && bitsPerSecondDouble < 75.0f) {
            angle = 180 + (bitsPerSecondDouble - 50) / 25.0f * 30.0f;
        } else if (bitsPerSecondDouble >= 75.0 && bitsPerSecondDouble < 100.0f) {
            angle = 210 + (bitsPerSecondDouble - 75) / 25.0f * 30.0f;
        }

        return angle;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_satellite);

        needle = findViewById(R.id.needle);
        compass = findViewById(R.id.compass);
        start = findViewById(R.id.start);
        rate = findViewById(R.id.rate);
        progressBar = findViewById(R.id.progressBar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        GPSSatelliteApp instance = GPSSatelliteApp.getInstance();
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
            if (instance.textColor != 0) {
                rate.setTextColor(ContextCompat.getColor(this, instance.textColor));
            }
            if (instance.buttonBgColor != 0) {
                start.setBackgroundColor(ContextCompat.getColor(this, instance.buttonBgColor));
            }
            if (instance.buttonTextColor != 0) {
                start.setTextColor(ContextCompat.getColor(this, instance.buttonTextColor));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
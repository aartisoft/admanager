package com.admanager.speedtest.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.admanager.speedtest.R;
import com.admanager.speedtest.SpeedTestApp;

import java.math.BigDecimal;
import java.math.RoundingMode;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class SpeedTestActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int BIGDECIMAL_ROUND_SCALE = 1;
    ImageView needle;
    ImageView compass;
    Button start;
    TextView rate;
    ProgressBar progressBar;

    private double currentNeedlePosition = 0;

    public static void start(Context context) {
        Intent intent = new Intent(context, SpeedTestActivity.class);
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
        setContentView(R.layout.activity_speedtest);

        needle = findViewById(R.id.needle);
        compass = findViewById(R.id.compass);
        start = findViewById(R.id.start);
        rate = findViewById(R.id.rate);
        progressBar = findViewById(R.id.progressBar);

        start.setOnClickListener(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        SpeedTestApp instance = SpeedTestApp.getInstance();
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

    @Override
    public void onClick(View v) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                startSpeedTestAsyncTask();
            }
        });
    }

    private void rotateNeedle(double currentNeedlePosition, double directionParam, int duration) {
        this.currentNeedlePosition = currentNeedlePosition;

        RotateAnimation raQibla = new RotateAnimation((float) currentNeedlePosition, (float) directionParam, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        raQibla.setDuration(duration);
        raQibla.setFillAfter(true);
        raQibla.setInterpolator(new LinearInterpolator());

        needle.startAnimation(raQibla);
        needle.refreshDrawableState();
    }

    private void startSpeedTestAsyncTask() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rotateNeedle(currentNeedlePosition, 0, 500);
            }
        });

        SpeedTestSocket speedTestSocket = new SpeedTestSocket();

        // add a listener to wait for speedtest completion and progress
        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

            @Override
            public void onCompletion(final SpeedTestReport report) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI(report, null);
                    }
                });
            }

            @Override
            public void onError(SpeedTestError speedTestError, String errorMessage) {
                // called when a download/upload error occur
            }

            @Override
            public void onProgress(final float percent, final SpeedTestReport report) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI(report, percent);
                    }
                });
            }
        });

        speedTestSocket.startDownload("http://ipv4.ikoula.testdebit.info/1M.iso");
    }

    private void updateUI(SpeedTestReport report, Float percent) {
        if (percent != null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress((int) percent.floatValue());
        }

        BigDecimal bps = report.getTransferRateBit();
        if (bps != null) {
            double bpsDouble = bps.divide(new BigDecimal(1000000.0f), BIGDECIMAL_ROUND_SCALE, RoundingMode.HALF_UP).doubleValue();
            String bpsStr = String.valueOf(bpsDouble);
            String emoji = "";
            if (bpsDouble < 1.0) {
                emoji = "\uD83D\uDC4E";
            } else if (bpsDouble >= 4.0) {
                emoji = "\uD83D\uDC4D";
            }
            rate.setText(String.format("%s %s %s", bpsStr, getString(R.string.speedtest_mbps), emoji));

            if (percent == null) {
                progressBar.setVisibility(View.INVISIBLE);
                progressBar.setProgress(0);
                rotateNeedle(0, getAngle(bpsDouble), 500);
            }
        }
    }
}
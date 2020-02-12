package com.admanager.compass.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.admanager.compass.CompassApp;
import com.admanager.compass.R;
import com.admanager.compass.utils.Compass;

public class CompassActivity extends AppCompatActivity implements Compass.CompassChangedListener {
    ImageView needleView;
    View northContainer;
    TextView maInformation;
    TextView degreeText;

    private Compass compass;

    public static void start(Context context) {
        Intent intent = new Intent(context, CompassActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        needleView = findViewById(R.id.compass_needle);
        northContainer = findViewById(R.id.compass_bg);
        maInformation = findViewById(R.id.maInformation);
        degreeText = findViewById(R.id.degreeText);

        compass = new Compass(this);
        compass.setListener(this);
        request();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        CompassApp instance = CompassApp.getInstance();
        if (instance != null) {
            if (instance.ads != null) {
                instance.ads.loadTop(this, (LinearLayout) findViewById(R.id.top));
                instance.ads.loadBottom(this, (LinearLayout) findViewById(R.id.bottom));
            }
            if (instance.title != null) {
                setTitle(instance.title);
            }

            if (instance.bgColor != 0) {
                findViewById(R.id.root).setBackgroundColor(instance.bgColor);
            }
            if (instance.textColor != 0) {
                degreeText.setTextColor(instance.textColor);
                maInformation.setTextColor(instance.textColor);
            }
            if (instance.compassNeedle != 0) {
                needleView.setBackground(ContextCompat.getDrawable(this, instance.compassNeedle));
            }
            if (instance.compassBg != 0) {
                northContainer.setBackground(ContextCompat.getDrawable(this, instance.compassBg));
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
    public void onStart() {
        super.onStart();
        if (compass != null) {
            compass.start();
        }

        request();
    }

    private void request() {

        if (compass != null) {
            compass.request();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (compass != null) {
            compass.stop();
        }
    }

    private void adjustArrow(float lastAzimuth, float azimuth) {
        degreeText.setText((int) (azimuth) + "°");

        //Animations
        Rotate[] rotates = new Rotate[]{
                new Rotate(needleView, -lastAzimuth, -azimuth),
                new Rotate(northContainer, -lastAzimuth, -azimuth)
        };

        for (Rotate rotate : rotates) {
            Animation an = new RotateAnimation(rotate.from, rotate.to, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            an.setDuration(0);
            an.setRepeatCount(0);
            an.setFillAfter(true);
            rotate.v.startAnimation(an);
        }

    }

    @Override
    public void onChanged(float oldAzimuth, float newAzimuth, boolean noSensor) {
        System.out.println("degree:" + oldAzimuth + "->" + newAzimuth);
        adjustArrow(oldAzimuth, newAzimuth);

        if (noSensor) {
            maInformation.setText(R.string.compass_no_sensor);
        } else {
            maInformation.setText("");
        }
    }

    class Rotate {
        View v;
        float from;
        float to;

        public Rotate(View v, float from, float to) {
            this.v = v;
            this.from = from;
            this.to = to;
        }


    }


}
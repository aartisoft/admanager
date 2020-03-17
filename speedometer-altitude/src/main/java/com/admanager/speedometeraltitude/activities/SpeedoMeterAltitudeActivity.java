package com.admanager.speedometeraltitude.activities;

import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.core.Consts;
import com.admanager.speedometeraltitude.R;
import com.admanager.speedometeraltitude.SpeedoMeterAltitudeApp;
import com.admanager.speedometeraltitude.adapter.SpeedLimitAdapter;
import com.admanager.speedometeraltitude.model.SpeedLimit;
import com.admanager.speedometeraltitude.utils.GPSUtils;
import com.admanager.speedometeraltitude.utils.PermissionChecker;
import com.github.anastr.speedviewlib.ImageSpeedometer;

import java.util.Arrays;

public class SpeedoMeterAltitudeActivity extends AppCompatActivity implements LocationListener, GpsStatus.Listener, SpeedLimitAdapter.SelectedListener {
    private static final int SPEEDO_REQUEST_CODE = 9999;
    private PermissionChecker permissionChecker;
    private ImageSpeedometer speedoMeter;
    private ImageView altimeterIndicator, altimeterBG;
    private TextView txtAltitude;
    private LinearLayout root;
    private LocationManager locationManager;
    private float speed;
    private double altitude;
    private ProgressBar altitudeProgress;
    private SpeedLimitAdapter adapter;
    private RecyclerView recyclerSpeedLimit;
    private SpeedoMeterAltitudeApp instance;

    public static void start(Context context) {
        Intent intent = new Intent(context, SpeedoMeterAltitudeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedometer_altitude);
        initView();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        permissionChecker = new PermissionChecker(this);
        setTitle(getString(R.string.speedometer));
        adapter = new SpeedLimitAdapter(this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerSpeedLimit.setLayoutManager(layoutManager);
        recyclerSpeedLimit.setAdapter(adapter);
        adapter.setData(Arrays.asList(SpeedLimit.values()));

        buildSpeedo();

    }

    private void buildSpeedo() {
        instance = SpeedoMeterAltitudeApp.getInstance();
        if (instance != null) {
            if (instance.ads != null) {
                instance.ads.loadTop(this, (LinearLayout) findViewById(R.id.top));
                instance.ads.loadBottom(this, (LinearLayout) findViewById(R.id.bottom));
            }
            if (instance.bgColor != 0) {
                root.setBackgroundColor(ContextCompat.getColor(this, instance.bgColor));
            }
            if (instance.altimeterImage != 0) {
                altimeterBG.setBackgroundResource(instance.altimeterImage);
            } else {
                altimeterBG.setBackgroundResource(R.drawable.bg_altimeter);
            }
            if (instance.altimeterIndicator != 0) {
                altimeterIndicator.setBackgroundResource(instance.altimeterIndicator);
            } else {
                altimeterIndicator.setBackgroundResource(R.drawable.indicator_altimeter);
            }
            if (instance.speedoImageWalk != 0) {
                speedoMeter.setImageSpeedometer(instance.speedoImageWalk);
            }
            if (instance.speedoImageBike != 0) {
                speedoMeter.setImageSpeedometer(instance.speedoImageBike);
            }
            if (instance.speedoImageMotorcycle != 0) {
                speedoMeter.setImageSpeedometer(instance.speedoImageMotorcycle);
            }
            if (instance.speedoImageCar != 0) {
                speedoMeter.setImageSpeedometer(instance.speedoImageCar);
            }
            if (instance.speedoIndicatorType != null) {
                speedoMeter.setIndicator(instance.speedoIndicatorType);
            }
            if (instance.speedoMarkColor != 0) {
                speedoMeter.setMarkColor(instance.speedoMarkColor);
            }
            if (instance.speedoSpeedTextColor != 0) {
                speedoMeter.setSpeedTextColor(instance.speedoSpeedTextColor);
            }
            if (instance.speedoTextColor != 0) {
                speedoMeter.setTextColor(instance.speedoTextColor);
            }
            if (instance.unitTextColor != 0) {
                speedoMeter.setUnitTextColor(instance.unitTextColor);
            }
        } else {
            Log.e(Consts.TAG, "init Gif module in Application class");
            finish();
        }
    }

    private void initView() {
        txtAltitude = findViewById(R.id.txtAltitude);
        speedoMeter = findViewById(R.id.speedoMeter);
        altimeterIndicator = findViewById(R.id.altimeterIndicator);
        root = findViewById(R.id.root);
        altimeterBG = findViewById(R.id.altimeterBG);
        altitudeProgress = findViewById(R.id.altitudeProgress);
        recyclerSpeedLimit = findViewById(R.id.recyclerSpeedLimit);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        permissionChecker.checkPermissionGrantedAndRun(new Runnable() {
            @Override
            public void run() {
                locationUpdates();
            }
        });
    }

    private void locationUpdates() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                locationManager.addGpsStatusListener(this);
            } else {
                throw new NullPointerException("Don't try on emulator because of network provider, or you have issue about Location Manager");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    private void openGPS() {
        new GPSUtils(this).turnGPSOn(new GPSUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                toast(R.string.gps_on);
            }
        }, SPEEDO_REQUEST_CODE);
    }

    private void toast(int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            speed = location.getSpeed();
            altitude = location.getAltitude();
            if (altitude == 0) {
                altitudeProgress.setVisibility(View.VISIBLE);
            } else {
                altitudeProgress.setVisibility(View.GONE);
                if (String.valueOf(altitude).length() > 6) {
                    txtAltitude.setText(String.valueOf(altitude).substring(0, 6) + "m");
                } else {
                    txtAltitude.setText(altitude + "m");
                }
            }
            float calculateRotation = (float) ((360 * altitude / 10) / 100);
            altimeterIndicator.setRotation(calculateRotation);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        openGPS();
    }

    @Override
    public void onGpsStatusChanged(int event) {
        speedoMeter.speedTo(speed);
    }

    @Override
    public void selectedItem(SpeedLimit speedLimit) {
        loadSpeedLimits(speedLimit.getId());
    }

    private void loadSpeedLimits(int id) {
        adapter.setSelected(id);
        switch (id) {
            case 0: {
                speedoMeter.setMaxSpeed(26);
                if (instance.speedoImageWalk != 0)
                    speedoMeter.setImageSpeedometer(instance.speedoImageWalk);
                else
                    speedoMeter.setImageSpeedometer(R.drawable.speed1);

                break;
            }
            case 1: {
                speedoMeter.setMaxSpeed(65);
                if (instance.speedoImageBike != 0)
                    speedoMeter.setImageSpeedometer(instance.speedoImageBike);
                else
                    speedoMeter.setImageSpeedometer(R.drawable.speed2);

                break;
            }
            case 2: {
                speedoMeter.setMaxSpeed(130);
                if (instance.speedoImageMotorcycle != 0)
                    speedoMeter.setImageSpeedometer(instance.speedoImageMotorcycle);
                else
                    speedoMeter.setImageSpeedometer(R.drawable.speed3);

                break;
            }
            case 3: {
                speedoMeter.setMaxSpeed(260);
                if (instance.speedoImageCar != 0)
                    speedoMeter.setImageSpeedometer(instance.speedoImageCar);
                else
                    speedoMeter.setImageSpeedometer(R.drawable.speed4);

                break;
            }
        }
    }
}
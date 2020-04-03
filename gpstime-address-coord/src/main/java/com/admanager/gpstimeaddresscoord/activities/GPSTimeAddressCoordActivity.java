package com.admanager.gpstimeaddresscoord.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.core.Consts;
import com.admanager.gpstimeaddresscoord.GPSTimeApp;
import com.admanager.gpstimeaddresscoord.R;
import com.admanager.gpstimeaddresscoord.common.PermissionChecker;
import com.admanager.gpstimeaddresscoord.utils.GPSUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPSTimeAddressCoordActivity extends AppCompatActivity implements LocationListener, GpsStatus.Listener {

    private static final int GPS_COORD_ADDRESS_REQUEST_CODE = 8888;
    private ProgressBar mProgress;
    private TextView gpsTime, gpsDate, gpsCoord, gpsAddress;
    private ImageView openAddress, saveAddress;
    private RecyclerView recyclerAddress;
    private PermissionChecker permissionChecker;
    private LocationManager locationManager;
    private Geocoder geocoder;
    private double latitude, longitude;
    private List<Address> addressList;
    private String address;

    public static void start(Context context) {
        Intent intent = new Intent(context, GPSTimeAddressCoordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adm_gps_activity);
        init();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle(getString(R.string.gpsTime_address));
        configureStyle();

        permissionChecker.checkPermissionGrantedAndRun(new Runnable() {
            @Override
            public void run() {
                //granted permission
                locationUpdates();
            }
        });
    }

    private void getAddressInfos() {
        if (latitude != 0 || longitude != 0) {
            try {
                addressList = geocoder.getFromLocation(latitude, longitude, 1);
                address = addressList.get(0).getAddressLine(0);
                gpsAddress.setText(address);
                Log.d("GeoCoaderAddress", address);
                if (!gpsAddress.getText().toString().equalsIgnoreCase("")) {
                    //    binding.loadingAddress.setVisibility(View.GONE);
                } else {
                    //  binding.loadingAddress.setVisibility(View.VISIBLE);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void configureStyle() {
        GPSTimeApp instance = GPSTimeApp.getInstance();
        if (instance == null) {
            Log.e(Consts.TAG, "init GPS-Address-Coord module in Application class");
            finish();
        } else {
            if (instance.ads != null) {
                instance.ads.loadTop(this, (LinearLayout) findViewById(R.id.top));
                instance.ads.loadBottom(this, (LinearLayout) findViewById(R.id.bottom));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void init() {
        gpsTime = findViewById(R.id.gpsTime);
        gpsDate = findViewById(R.id.gpsDate);
        gpsCoord = findViewById(R.id.gpsCoord);
        gpsAddress = findViewById(R.id.gpsAddress);
        openAddress = findViewById(R.id.openAddress);
        saveAddress = findViewById(R.id.saveAddress);
        recyclerAddress = findViewById(R.id.recyclerAddress);
        mProgress = findViewById(R.id.mProgress);
        permissionChecker = new PermissionChecker(this);
        geocoder = new Geocoder(this, Locale.getDefault());

    }

    private void openGPS() {
        new GPSUtils(this).turnGPSOn(new GPSUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                toast(R.string.gps_on);
            }
        }, GPS_COORD_ADDRESS_REQUEST_CODE);
    }

    private void toast(int mesg) {
        Toast.makeText(this, getString(mesg), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onGpsStatusChanged(int event) {

    }
}
package com.admanager.gpstimeaddresscoord.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.core.Consts;
import com.admanager.gpstimeaddresscoord.GPSTimeApp;
import com.admanager.gpstimeaddresscoord.R;
import com.admanager.gpstimeaddresscoord.adapter.GpsAddressAdapter;
import com.admanager.gpstimeaddresscoord.common.Common;
import com.admanager.gpstimeaddresscoord.common.PermissionChecker;
import com.admanager.gpstimeaddresscoord.database.LocationDao;
import com.admanager.gpstimeaddresscoord.model.Addresses;
import com.admanager.gpstimeaddresscoord.utils.GPSUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class GPSTimeAddressCoordActivity extends AppCompatActivity implements LocationListener, GpsStatus.Listener, GpsAddressAdapter.RefreshListener {

    private static final int GPS_COORD_ADDRESS_REQUEST_CODE = 8888;
    private ProgressBar mProgress, mAddressProgress;
    private TextView gpsTime, gpsDate, gpsCoord, gpsAddress, noItemMsg;
    private ImageView openAddress, saveAddress;
    private RecyclerView recyclerAddress;
    private PermissionChecker permissionChecker;
    private LocationManager locationManager;
    private Geocoder geocoder;
    private double latitude, longitude;
    private List<Address> addressList;
    private String address;
    private long mGpsTime;
    private GpsAddressAdapter mAdapter;
    private LinearLayout root;
    private CardView cardClock, cardDate, cardCoord, cardAddress;

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
        loadAddressList();
        permissionChecker.checkPermissionGrantedAndRun(new Runnable() {
            @Override
            public void run() {
                //granted permission
                locationUpdates();
            }
        });
    }

    private void getAddressInfos() {
        if (latitude != 0 && longitude != 0) {
            try {
                addressList = geocoder.getFromLocation(latitude, longitude, 1);
                address = addressList.get(0).getAddressLine(0);
                gpsAddress.setText(address);
                Log.d("GeoCoaderAddress", address);
                if (!gpsAddress.getText().toString().equalsIgnoreCase("")) {
                    mAddressProgress.setVisibility(View.GONE);
                } else {
                    mAddressProgress.setVisibility(View.VISIBLE);
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
            if (instance.bgColor != 0) {
                root.setBackgroundColor(ContextCompat.getColor(this, instance.bgColor));
            }
            if (instance.cardBgColor != 0) {
                cardDate.setCardBackgroundColor(ContextCompat.getColor(this, instance.cardBgColor));
                cardCoord.setCardBackgroundColor(ContextCompat.getColor(this, instance.cardBgColor));
                cardAddress.setCardBackgroundColor(ContextCompat.getColor(this, instance.cardBgColor));
                cardClock.setCardBackgroundColor(ContextCompat.getColor(this, instance.cardBgColor));
            }
            if (instance.textColor != 0) {
                gpsTime.setTextColor(ContextCompat.getColor(this, instance.textColor));
                gpsDate.setTextColor(ContextCompat.getColor(this, instance.textColor));
                gpsCoord.setTextColor(ContextCompat.getColor(this, instance.textColor));
                gpsAddress.setTextColor(ContextCompat.getColor(this, instance.textColor));
                noItemMsg.setTextColor(ContextCompat.getColor(this, instance.textColor));
            }
            if (instance.bgColor != 0) {
                root.setBackgroundColor(ContextCompat.getColor(this, instance.bgColor));
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
        mAddressProgress = findViewById(R.id.mAddressProgress);
        noItemMsg = findViewById(R.id.noItemMsg);
        root = findViewById(R.id.root);
        cardClock = findViewById(R.id.cardClock);
        cardAddress = findViewById(R.id.cardAddress);
        cardCoord = findViewById(R.id.cardCoord);
        cardDate = findViewById(R.id.cardDate);

        permissionChecker = new PermissionChecker(this);
        geocoder = new Geocoder(this, Locale.getDefault());
        mAdapter = new GpsAddressAdapter(this, this);
        recyclerAddress.setAdapter(mAdapter);
        recyclerAddress.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
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


    private void setData() {
        String coordinates = latitude + " , " + longitude;
        if (latitude == 0 && longitude == 0) {
            gpsCoord.setText(R.string.gps_coord_loading);
        } else {
            gpsCoord.setText(coordinates);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        mGpsTime = location.getTime();
        getAddressInfos();
        setData();
        setTime();
    }

    private void setTime() {
        if (mGpsTime == 0) {
            gpsTime.setText(R.string.gps_coord_loading);
        } else {
            SimpleDateFormat watch = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat formattedDate = new SimpleDateFormat("dd.MM.yyyy");
            String clock = watch.format(mGpsTime);
            String date = formattedDate.format(mGpsTime);
            gpsTime.setText(clock);
            gpsDate.setText(date);
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
        getAddressInfos();
        setTime();
    }

    public void openAddress(View view) {
        Common.goLocation(this, latitude, longitude);
    }


    public void saveMyAddress(View view) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.adm_gps_dialog_save, null);
        final EditText editSaveName = dialogView.findViewById(R.id.editAddressName);
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle(R.string.gps_type_a_location_name)
                .setPositiveButton(R.string.gps_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveAddress(editSaveName.getText().toString());
                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.gps_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private void saveAddress(final String addressName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LocationDao dao = Common.getDatabase(GPSTimeAddressCoordActivity.this).locationDao();
                Addresses mAddress = new Addresses();
                mAddress.savedAddress = gpsAddress.getText().toString();
                mAddress.addressName = addressName;
                mAddress.lat = latitude;
                mAddress.lng = longitude;
                dao.insertAddress(mAddress);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GPSTimeAddressCoordActivity.this, getString(R.string.gps_added_to_list), Toast.LENGTH_SHORT).show();
                        loadAddressList();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onRefresh() {
        loadAddressList();
    }

    private void loadAddressList() {
        new LoadAddressesList().execute();
    }

    private class LoadAddressesList extends AsyncTask<List<Addresses>, Void, List<Addresses>> {

        @Override
        protected List<Addresses> doInBackground(List<Addresses>... lists) {
            LocationDao dao = Common.getDatabase(GPSTimeAddressCoordActivity.this).locationDao();
            List<Addresses> addressList = dao.getAddressList();
            return addressList;
        }

        @Override
        protected void onPostExecute(List<Addresses> addresses) {
            mAdapter.setData(addresses);
            if (addresses.size() > 0) {
                noItemMsg.setVisibility(View.GONE);
            } else {
                noItemMsg.setVisibility(View.VISIBLE);
            }
            mProgress.setVisibility(View.GONE);
        }
    }

}
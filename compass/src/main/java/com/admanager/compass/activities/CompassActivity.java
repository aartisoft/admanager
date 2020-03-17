package com.admanager.compass.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.admanager.compass.CompassApp;
import com.admanager.compass.R;
import com.admanager.compass.ipapi.IPApi;
import com.admanager.compass.ipapi.IpModel;
import com.admanager.compass.utils.Compass;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CompassActivity extends AppCompatActivity implements Compass.CompassChangedListener {
    ImageView needleView;
    ImageView northContainer;
    ImageView kabe;
    TextView maInformation;
    TextView degreeText;
    private Retrofit retrofit;

    private Compass compass;
    private float QIBLA_DEGREE;
    private String additionalText = "";

    public static void start(Context context) {
        Intent intent = new Intent(context, CompassActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        needleView = findViewById(R.id.compass_needle);
        kabe = findViewById(R.id.kabe);
        northContainer = findViewById(R.id.compass_bg);
        maInformation = findViewById(R.id.maInformation);
        degreeText = findViewById(R.id.degreeText);

        compass = new Compass(this);
        compass.setListener(this);
        if (compass != null) {
            compass.request();
        }

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
                findViewById(R.id.root).setBackgroundColor(ContextCompat.getColor(this, instance.bgColor));
            }
            if (instance.bgDrawable != 0) {
                findViewById(R.id.root).setBackgroundResource(instance.bgDrawable);
            }
            if (instance.textColor != 0) {
                degreeText.setTextColor(ContextCompat.getColor(this, instance.textColor));
                maInformation.setTextColor(ContextCompat.getColor(this, instance.textColor));
            }
            if (instance.compassNeedle != 0) {
                needleView.setImageResource(instance.compassNeedle);
            }
            if (instance.compassBg != 0) {
                northContainer.setImageResource(instance.compassBg);
            }
            if (instance.kabeIcon != 0) {
                kabe.setImageResource(instance.kabeIcon);
            }

            if (instance.qibla) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl("http://ip-api.com/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
                retrofit.create(IPApi.class).ip().enqueue(new Callback<IpModel>() {
                    @Override
                    public void onResponse(Call<IpModel> call, Response<IpModel> response) {
                        if (response.body() != null && response.code() == 200) {
                            QIBLA_DEGREE = calculateQibla(response.body().lat, response.body().lon);
                            additionalText = response.body().city;
                            kabe.setVisibility(View.VISIBLE);
                        }
                        if (compass != null) {
                            compass.request();
                        }
                    }

                    @Override
                    public void onFailure(Call<IpModel> call, Throwable t) {
                        if (compass != null) {
                            compass.request();
                        }
                    }
                });
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
        if (compass != null) {
            compass.request();
        }
    }

    public float calculateQibla(double latitude, double longitude) {
        double phiK = 21.4 * Math.PI / 180.0;
        double lambdaK = 39.8 * Math.PI / 180.0;
        double phi = latitude * Math.PI / 180.0;
        double lambda = longitude * Math.PI / 180.0;
        double psi = 180.0 / Math.PI * Math.atan2(Math.sin(lambdaK - lambda), Math.cos(phi) * Math.tan(phiK) - Math.sin(phi) * Math.cos(lambdaK - lambda));
        return Math.round(psi);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (compass != null) {
            compass.stop();
        }
    }

    private void adjustArrow(float lastAzimuth, float azimuth) {

        degreeText.setText((int) (azimuth - QIBLA_DEGREE) + "°");

        //Animations
        Rotate[] rotates = new Rotate[]{
                new Rotate(needleView, QIBLA_DEGREE - lastAzimuth, QIBLA_DEGREE - azimuth),
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
        adjustArrow(oldAzimuth, newAzimuth);

        if (noSensor) {
            maInformation.setText(additionalText + " " + getString(R.string.compass_no_sensor));
        } else {
            maInformation.setText(additionalText);
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
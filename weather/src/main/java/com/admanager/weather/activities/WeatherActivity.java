package com.admanager.weather.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.core.Consts;
import com.admanager.weather.R;
import com.admanager.weather.WeatherApp;
import com.admanager.weather.adapters.ForecastAdapter;
import com.admanager.weather.day.HourlyWeathers;
import com.admanager.weather.day.MyWeather;
import com.admanager.weather.forecast.Forecast;
import com.admanager.weather.forecast.List;
import com.admanager.weather.remote.ApiService;
import com.admanager.weather.remote.RetrofitClient;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherActivity extends AppCompatActivity implements View.OnTouchListener {

    private final String TAG = "WeatherActivity";
    private LinearLayout rootLayout;
    private ProgressBar loadingWeather;
    private ImageView weatherIcon;
    private TextView weatherDate, weatherStatus, windText, humidityext, pressureText, tempTxt;

    private RecyclerView recyclerForecast;
    private ApiService service;
    private LinearLayout dailyLayout;
    private Timer timer;
    private TimerTask timerTask;
    private int each_time = 1000 * 4;
    private Handler handler = new Handler();
    private int index = 0;
    private ForecastAdapter forecastAdapter;
    private boolean isForward;
    private String celsiusText;

    public static void start(Context context) {
        Intent intent = new Intent(context, WeatherActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initView();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            setTitle(R.string.weather);
        }
        configureDesign();
        service = RetrofitClient.getRetrofitInstance(getString(R.string.base_url)).create(ApiService.class);
        forecastAdapter = new ForecastAdapter(this);

        formatDate();
        loadDailyWeather();
        loadForecastWeather();

        recyclerForecast.setOnTouchListener(this);
    }

    private void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if (isForward && index == forecastAdapter.getItemCount()) {
                            isForward = false;
                            index = forecastAdapter.getItemCount();
                        } else if (index == 0 && !isForward) {
                            isForward = true;
                            index = 0;
                        }
                        if (isForward)
                            index++;
                        else
                            index--;

                        recyclerForecast.smoothScrollToPosition(index);
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, each_time);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void formatDate() {
        Date mDate = new Date();
        String dayOfTheWeek = (String) DateFormat.format("EEEE", mDate);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = format.format(mDate);
        weatherDate.setText(dayOfTheWeek + " , " + formattedDate);
    }

    private void loadForecastWeather() {
        recyclerForecast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerForecast.setAdapter(forecastAdapter);
        Call<Forecast> callHourlyWeather = service.myForecastWeathers();
        callHourlyWeather.enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                if (response.body() == null)
                    return;

                java.util.List<List> body = response.body().getList();
                java.util.List<HourlyWeathers> hourlyList = new ArrayList<>();
                for (int i = 0; i < body.size(); i++) {
                    HourlyWeathers hourlyWeathers = new HourlyWeathers();
                    Double hourlyCelsius = calculateCelsius(body.get(i).getMain().getTemp());
                    hourlyWeathers.setCelsius(hourlyCelsius);
                    hourlyWeathers.setDate(body.get(i).getDt() * 1000);
                    hourlyWeathers.setDescription(body.get(i).getWeather().get(0).getDescription());
                    hourlyWeathers.setHumidity(body.get(i).getMain().getHumidity());
                    hourlyWeathers.setImgUrl(getString(R.string.imgUrl) + body.get(i).getWeather().get(0).getIcon() + ".png");
                    hourlyWeathers.setPressure(body.get(i).getMain().getPressure());
                    hourlyWeathers.setWindSpeed(body.get(i).getWind().getSpeed());
                    hourlyList.add(hourlyWeathers);
                }
                forecastAdapter.setData(hourlyList);
                startTimer();
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                t.getMessage();
            }
        });
    }

    private double calculateCelsius(Double temp) {
        return (5 * (temp - 32.0)) / 9.0;
    }

    private void loadDailyWeather() {
        Call<MyWeather> callDayWeather = service.myWeather();
        callDayWeather.enqueue(new Callback<MyWeather>() {
            @Override
            public void onResponse(Call<MyWeather> call, Response<MyWeather> response) {
                MyWeather body = response.body();
                if (body == null)
                    return;

                weatherStatus.setText(String.valueOf(body.getWeather().get(0).getDescription()));
                pressureText.setText(body.getMain().getPressure() + " IN");
                windText.setText(String.valueOf(body.getWind().getSpeed()));
                humidityext.setText(String.valueOf(body.getMain().getHumidity()));
                tempTxt.setText(getCelsiusText(body));
                Glide.with(WeatherActivity.this)
                        .load(getString(R.string.imgUrl) + body.getWeather().get(0).getIcon() + ".png")
                        .into(weatherIcon);
                if (!tempTxt.getText().toString().equalsIgnoreCase("")) {
                    loadingWeather.setVisibility(View.GONE);
                }
                Log.d(TAG, getString(R.string.imgUrl) + body.getWeather().get(0).getIcon() + ".png");
            }

            @Override
            public void onFailure(Call<MyWeather> call, Throwable t) {
                t.getMessage();
            }
        });

    }

    private String getCelsiusText(MyWeather body) {
        double celsius = calculateCelsius(body.getMain().getTemp());
        if (String.valueOf(celsius).length() >= 5)
            celsiusText = String.valueOf(celsius).substring(0, 5) + "°C";
        else
            celsiusText = celsius + "°C";

        return celsiusText;
    }

    private void configureDesign() {
        WeatherApp instance = WeatherApp.getInstance();
        if (instance != null) {
            if (instance.ads != null) {
                instance.ads.loadTop(this, (LinearLayout) findViewById(R.id.top));
                instance.ads.loadBottom(this, (LinearLayout) findViewById(R.id.bottom));
            }
            if (instance.bgColor != 0) {
                rootLayout.setBackgroundColor(ContextCompat.getColor(this, instance.bgColor));
            }
            if (instance.itemBg != 0) {
                dailyLayout.setBackgroundColor(ContextCompat.getColor(this, instance.itemBg));
                humidityext.setBackgroundColor(ContextCompat.getColor(this, instance.itemBg));
                windText.setBackgroundColor(ContextCompat.getColor(this, instance.itemBg));
                pressureText.setBackgroundColor(ContextCompat.getColor(this, instance.itemBg));
                tempTxt.setBackgroundColor(ContextCompat.getColor(this, instance.itemBg));
            }
            if (instance.textColor != 0) {
                weatherDate.setTextColor(ContextCompat.getColor(this, instance.textColor));
                weatherStatus.setTextColor(ContextCompat.getColor(this, instance.textColor));
                windText.setTextColor(ContextCompat.getColor(this, instance.textColor));
                humidityext.setTextColor(ContextCompat.getColor(this, instance.textColor));
                pressureText.setTextColor(ContextCompat.getColor(this, instance.textColor));
                tempTxt.setTextColor(ContextCompat.getColor(this, instance.textColor));
            }
        } else {
            Log.e(Consts.TAG, "init Weather module in Application class");
            finish();
        }
    }

    private void initView() {
        rootLayout = findViewById(R.id.rootLayout);
        loadingWeather = findViewById(R.id.loadingWeather);
        weatherIcon = findViewById(R.id.weatherIcon);
        weatherDate = findViewById(R.id.weatherDate);
        weatherStatus = findViewById(R.id.weatherStatus);
        windText = findViewById(R.id.windText);
        humidityext = findViewById(R.id.humidityext);
        pressureText = findViewById(R.id.pressureText);
        tempTxt = findViewById(R.id.tempTxt);
        recyclerForecast = findViewById(R.id.recyclerForecast);
        dailyLayout = findViewById(R.id.dailyLayout);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                timer.cancel();
                break;
        }
        return false;
    }
}

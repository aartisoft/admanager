package com.admanager.weather.remote;

import com.admanager.weather.day.MyWeather;
import com.admanager.weather.forecast.Forecast;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("data/2.5/weather")
    Call<MyWeather> myWeather();

    @GET("data/2.5/forecast?")
    Call<Forecast> myForecastWeathers();
}

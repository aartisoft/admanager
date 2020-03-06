package com.admanager.compass.ipapi;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IPApi {
    @GET("json")
    Call<IpModel> ip();
}

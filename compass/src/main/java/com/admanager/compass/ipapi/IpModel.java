package com.admanager.compass.ipapi;

import com.google.gson.annotations.SerializedName;

public class IpModel {
    @SerializedName("lat")
    public double lat;
    @SerializedName("lon")
    public double lon;
    @SerializedName("city")
    public String city;
}


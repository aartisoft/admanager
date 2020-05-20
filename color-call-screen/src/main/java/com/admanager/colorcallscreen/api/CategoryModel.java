package com.admanager.colorcallscreen.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CategoryModel implements Serializable {

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("icon")
    @Expose
    public String icon;

    @SerializedName("image")
    @Expose
    public String image;

}
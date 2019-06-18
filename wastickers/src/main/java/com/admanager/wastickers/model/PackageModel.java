package com.admanager.wastickers.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PackageModel implements Serializable {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("tray_image")
    @Expose
    public String trayImage;
    @SerializedName("popularity")
    @Expose
    public int popularity;
    @SerializedName("stickers")
    @Expose
    public List<StickerModel> stickers;

    public boolean isInWhiteList;

}

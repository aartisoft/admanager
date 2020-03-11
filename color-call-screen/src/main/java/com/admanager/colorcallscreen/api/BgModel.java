package com.admanager.colorcallscreen.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.UUID;

public class BgModel implements Serializable {

    @SerializedName("image")
    @Expose
    public String image;

    @SerializedName("category")
    @Expose
    public String category;

    @SerializedName("thumbnail")
    @Expose
    public String thumbnail;

    public static BgModel randomImportedName() {
        BgModel bg = new BgModel();
        bg.image = "/" + UUID.randomUUID().toString() + ".png";
        return bg;
    }
}
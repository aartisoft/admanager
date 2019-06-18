package com.admanager.wastickers.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class StickerModel implements Serializable {

    @SerializedName("image")
    @Expose
    public String image;

    @SerializedName("emojis")
    @Expose
    public List<String> emojis;

}

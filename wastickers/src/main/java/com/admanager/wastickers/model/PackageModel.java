package com.admanager.wastickers.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

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
    @SerializedName("tags")
    @Expose
    public List<String> tags;
    @SerializedName("stickers")
    @Expose
    public List<StickerModel> stickers;

    public boolean isInWhiteList;

    public boolean contains(String q) {
        Locale E = Locale.ENGLISH;
        if (name != null && name.toLowerCase(E).contains(q.toLowerCase(E))) {
            return true;
        }
        if (tags != null) {
            for (String tag : tags) {
                if (tag != null && tag.toLowerCase(E).contains(q.toLowerCase(E))) {
                    return true;
                }
            }
        }
        return false;
    }
}

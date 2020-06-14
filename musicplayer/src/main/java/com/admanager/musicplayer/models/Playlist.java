package com.admanager.musicplayer.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Playlist implements Parcelable {
    public final static Creator<Playlist> CREATOR = new Creator<Playlist>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        public Playlist[] newArray(int size) {
            return (new Playlist[size]);
        }

    };
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("trackIds")
    @Expose
    private List<Long> trackIds = null;

    protected Playlist(Parcel in) {
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.trackIds = new ArrayList<>();
        in.readList(this.trackIds, (Long.class.getClassLoader()));
    }

    public Playlist() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getTrackIds() {
        return trackIds;
    }

    public void setTrackIds(List<Long> tracks) {
        this.trackIds = tracks;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(name);
        dest.writeList(trackIds);
    }

    public int describeContents() {
        return 0;
    }
}

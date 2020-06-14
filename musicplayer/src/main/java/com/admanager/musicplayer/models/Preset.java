package com.admanager.musicplayer.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Preset extends SpinnerPreset implements Parcelable {
    public final static Creator<Preset> CREATOR = new Creator<Preset>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Preset createFromParcel(Parcel in) {
            return new Preset(in);
        }

        public Preset[] newArray(int size) {
            return (new Preset[size]);
        }

    };
    @SerializedName("bandLevels")
    @Expose
    private List<Short> bandLevels = null;

    protected Preset(Parcel in) {
        this.bandLevels = new ArrayList<>();
        in.readList(this.bandLevels, (Integer.class.getClassLoader()));
    }

    public Preset() {
    }

    public List<Short> getBandLevels() {
        return bandLevels;
    }

    public void setBandLevels(List<Short> bandLevels) {
        this.bandLevels = bandLevels;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(bandLevels);
    }

    public int describeContents() {
        return 0;
    }
}

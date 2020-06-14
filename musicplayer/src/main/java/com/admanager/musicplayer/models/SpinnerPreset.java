package com.admanager.musicplayer.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SpinnerPreset implements Parcelable {
    public final static Creator<SpinnerPreset> CREATOR = new Creator<SpinnerPreset>() {


        @SuppressWarnings({
                "unchecked"
        })
        public SpinnerPreset createFromParcel(Parcel in) {
            return new SpinnerPreset(in);
        }

        public SpinnerPreset[] newArray(int size) {
            return (new SpinnerPreset[size]);
        }

    };
    @SerializedName("presetNumber")
    @Expose
    private int presetNumber;
    @SerializedName("presetName")
    @Expose
    private String presetName;
    //Spinner item position
    @SerializedName("position")
    @Expose
    private int position;

    protected SpinnerPreset(Parcel in) {
        this.presetNumber = ((int) in.readValue((Integer.class.getClassLoader())));
        this.presetName = ((String) in.readValue((String.class.getClassLoader())));
        this.position = ((int) in.readValue((Integer.class.getClassLoader())));
    }

    public SpinnerPreset() {
    }

    public int getPresetNumber() {
        return presetNumber;
    }

    public void setPresetNumber(int presetNumber) {
        this.presetNumber = presetNumber;
    }

    public String getPresetName() {
        return presetName;
    }

    public void setPresetName(String presetName) {
        this.presetName = presetName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(presetNumber);
        dest.writeValue(presetName);
        dest.writeValue(position);
    }

    public int describeContents() {
        return 0;
    }
}

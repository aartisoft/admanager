
package com.admanager.musicplayer.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Genre implements Parcelable {
    public final static Creator<Genre> CREATOR = new Creator<Genre>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Genre createFromParcel(Parcel in) {
            return new Genre(in);
        }

        public Genre[] newArray(int size) {
            return (new Genre[size]);
        }

    };
    @SerializedName("genreId")
    @Expose
    private long genreId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("audioIds")
    @Expose
    private List<Long> audioIds = null;

    protected Genre(Parcel in) {
        this.genreId = ((long) in.readValue((Long.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.audioIds = new ArrayList<>();
        in.readList(this.audioIds, (Long.class.getClassLoader()));
    }

    public Genre() {
    }

    public long getGenreId() {
        return genreId;
    }

    public void setGenreId(long genreId) {
        this.genreId = genreId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getAudioIds() {
        return audioIds;
    }

    public void setAudioIds(List<Long> ids) {
        this.audioIds = ids;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(genreId);
        dest.writeValue(name);
        dest.writeList(audioIds);
    }

    public int describeContents() {
        return 0;
    }
}

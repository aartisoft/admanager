package com.admanager.wastickers.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Locale;

public class Sticker implements Parcelable {
    public static final Creator<Sticker> CREATOR = new Creator<Sticker>() {
        @Override
        public Sticker createFromParcel(Parcel in) {
            return new Sticker(in);
        }

        @Override
        public Sticker[] newArray(int size) {
            return new Sticker[size];
        }
    };
    public String fileName;
    public String imageURL;
    public List<String> emojis;
    public long size;

    public Sticker(String fileName, List<String> emojis) {
        this.fileName = fileName.toLowerCase(Locale.ENGLISH);
        this.emojis = emojis;
    }

    protected Sticker(Parcel in) {
        fileName = in.readString();
        emojis = in.createStringArrayList();
        size = in.readLong();
        imageURL = in.readString();
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileName);
        dest.writeStringList(emojis);
        dest.writeLong(size);
        dest.writeString(imageURL);
    }
}

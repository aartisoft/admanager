package com.admanager.equalizer.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Music implements Parcelable {
    public static final Creator CREATOR = new Creator() {
        public final /* synthetic */ Object createFromParcel(Parcel parcel) {
            return new Music(parcel);
        }

        public final /* bridge */ /* synthetic */ Object[] newArray(int i) {
            return new Music[i];
        }
    };
    private String packageName;
    private boolean isPlaying;
    private String track;
    private String artist;
    private long albumId;

    public Music() {

    }

    public Music(String packName, boolean isPlay, String trac, String art, long album) {
        this.packageName = packName;
        this.isPlaying = isPlay;
        this.track = trac;
        this.artist = art;
        this.albumId = album;
    }

    public Music(Parcel parcel) {
        this.packageName = parcel.readString();
        this.isPlaying = parcel.readByte() == 1;
        this.track = parcel.readString();
        this.artist = parcel.readString();
        this.albumId = parcel.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.packageName);
        parcel.writeByte(this.isPlaying ? (byte) 1 : 0);
        parcel.writeString(this.track);
        parcel.writeString(this.artist);
        parcel.writeLong(this.albumId);
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }
}

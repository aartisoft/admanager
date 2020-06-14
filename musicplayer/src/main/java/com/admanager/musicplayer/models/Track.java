
package com.admanager.musicplayer.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Track implements Parcelable {
    public final static Creator<Track> CREATOR = new Creator<Track>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        public Track[] newArray(int size) {
            return (new Track[size]);
        }

    };
    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("albumId")
    @Expose
    private long albumId;
    @SerializedName("artistId")
    @Expose
    private long artistId;
    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("album")
    @Expose
    private String album;
    @SerializedName("artist")
    @Expose
    private String artist;
    @SerializedName("duration")
    @Expose
    private long duration;
    @SerializedName("trackNo")
    @Expose
    private int trackNo;
    @SerializedName("totalMsec")
    @Expose
    private String totalMsec;
    @SerializedName("playing")
    @Expose
    private boolean playing;

    protected Track(Parcel in) {
        this.id = ((long) in.readValue((Long.class.getClassLoader())));
        this.albumId = ((long) in.readValue((Long.class.getClassLoader())));
        this.artistId = ((long) in.readValue((Long.class.getClassLoader())));
        this.path = ((String) in.readValue((String.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.album = ((String) in.readValue((String.class.getClassLoader())));
        this.artist = ((String) in.readValue((String.class.getClassLoader())));
        this.duration = ((long) in.readValue((Long.class.getClassLoader())));
        this.trackNo = ((int) in.readValue((Integer.class.getClassLoader())));
        this.totalMsec = ((String) in.readValue((String.class.getClassLoader())));
        this.playing = ((boolean) in.readValue((boolean.class.getClassLoader())));
    }

    public Track() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getTrackNo() {
        return trackNo;
    }

    public void setTrackNo(int trackNo) {
        this.trackNo = trackNo;
    }

    public String getTotalMsec() {
        return totalMsec;
    }

    public void setTotalMsec(String totalMsec) {
        this.totalMsec = totalMsec;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(albumId);
        dest.writeValue(artistId);
        dest.writeValue(path);
        dest.writeValue(title);
        dest.writeValue(album);
        dest.writeValue(artist);
        dest.writeValue(duration);
        dest.writeValue(trackNo);
        dest.writeValue(totalMsec);
        dest.writeValue(playing);
    }

    public int describeContents() {
        return 0;
    }

}

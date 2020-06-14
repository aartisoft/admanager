
package com.admanager.musicplayer.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FileObject implements Parcelable {
    public final static Creator<FileObject> CREATOR = new Creator<FileObject>() {


        @SuppressWarnings({
                "unchecked"
        })
        public FileObject createFromParcel(Parcel in) {
            return new FileObject(in);
        }

        public FileObject[] newArray(int size) {
            return (new FileObject[size]);
        }

    };
    @SerializedName("filePath")
    @Expose
    private String filePath;
    @SerializedName("name")
    @Expose
    private String name;

    protected FileObject(Parcel in) {
        this.filePath = ((String) in.readValue((Long.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
    }

    public FileObject() {
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(filePath);
        dest.writeValue(name);
    }

    public int describeContents() {
        return 0;
    }
}

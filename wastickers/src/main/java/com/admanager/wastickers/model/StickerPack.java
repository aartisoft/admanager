package com.admanager.wastickers.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Locale;

public class StickerPack implements Parcelable {
    public static final Creator<StickerPack> CREATOR = new Creator<StickerPack>() {
        @Override
        public StickerPack createFromParcel(Parcel in) {
            return new StickerPack(in);
        }

        @Override
        public StickerPack[] newArray(int size) {
            return new StickerPack[size];
        }
    };
    public final String publisherEmail;
    public final String publisherWebsite;
    public final String privacyPolicyWebsite;
    public final String licenseAgreementWebsite;
    public String identifier;
    public String name;
    public String publisher;
    public String fileName;
    public String tryIconURL;
    public String iosAppStoreLink;
    public String androidPlayStoreLink;
    private List<Sticker> stickers;
    private long totalSize;
    private boolean isWhitelisted;

    public StickerPack(String identifier, String name, String publisher, String fileName, String packageName) {
        this.identifier = identifier;
        this.name = name;
        this.publisher = publisher;
        this.fileName = fileName.toLowerCase(Locale.ENGLISH).replaceAll(" ", "_");

        String url = "https://play.google.com/store/apps/details?id=" + packageName;
        this.publisherEmail = null;
        this.publisherWebsite = url;
        this.privacyPolicyWebsite = url;
        this.licenseAgreementWebsite = url;
        this.androidPlayStoreLink = url;

    }

    protected StickerPack(Parcel in) {
        identifier = in.readString();
        name = in.readString();
        publisher = in.readString();
        fileName = in.readString();
        tryIconURL = in.readString();
        publisherEmail = in.readString();
        publisherWebsite = in.readString();
        privacyPolicyWebsite = in.readString();
        licenseAgreementWebsite = in.readString();
        iosAppStoreLink = in.readString();
        stickers = in.createTypedArrayList(Sticker.CREATOR);
        totalSize = in.readLong();
        androidPlayStoreLink = in.readString();
        isWhitelisted = in.readByte() != 0;
    }

    public boolean getIsWhitelisted() {
        return isWhitelisted;
    }

    public void setIsWhitelisted(boolean isWhitelisted) {
        this.isWhitelisted = isWhitelisted;
    }

    public void setAndroidPlayStoreLink(String androidPlayStoreLink) {
        this.androidPlayStoreLink = androidPlayStoreLink;
    }

    public void setIosAppStoreLink(String iosAppStoreLink) {
        this.iosAppStoreLink = iosAppStoreLink;
    }

    public List<Sticker> getStickers() {
        return stickers;
    }

    public void setStickers(List<Sticker> stickers) {
        this.stickers = stickers;
        totalSize = 0;
        for (Sticker sticker : stickers) {
            totalSize += sticker.size;
        }
    }

    public long getTotalSize() {
        return totalSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(identifier);
        dest.writeString(name);
        dest.writeString(publisher);
        dest.writeString(fileName);
        dest.writeString(tryIconURL);
        dest.writeString(publisherEmail);
        dest.writeString(publisherWebsite);
        dest.writeString(privacyPolicyWebsite);
        dest.writeString(licenseAgreementWebsite);
        dest.writeString(iosAppStoreLink);
        dest.writeTypedList(stickers);
        dest.writeLong(totalSize);
        dest.writeString(androidPlayStoreLink);
        dest.writeByte((byte) (isWhitelisted ? 1 : 0));
    }
}

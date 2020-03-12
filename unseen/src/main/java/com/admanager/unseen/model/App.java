package com.admanager.unseen.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class App extends RealmObject {
    @PrimaryKey
    private String pack;
    private boolean fav;
    private boolean exist;
    private String name;
    private long lastOpened;
    private int counter;
    private String remoteImage;

    public App() {
    }

    public App(String pack, String name) {
        this(pack, name, null);
    }

    public App(String pack, String name, String remoteImage) {
        this.pack = pack;
        this.name = name;
        this.remoteImage = remoteImage;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLastOpened() {
        return lastOpened;
    }

    public void setLastOpened(long lastOpened) {
        this.lastOpened = lastOpened;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getRemoteImage() {
        return remoteImage;
    }

    public void setRemoteImage(String remoteImage) {
        this.remoteImage = remoteImage;
    }
}

package com.admanager.speedometeraltitude.model;

import com.admanager.speedometeraltitude.R;

public enum SpeedLimit {

    Walk(0, R.string.walk),
    Bicycle(1, R.string.bicycle),
    Motocycle(2, R.string.motocycle),
    Car(3, R.string.car);

    private int title;
    private int id;

    SpeedLimit(int id, int title) {
        this.id = id;
        this.title = title;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

package com.admanager.gpstimeaddresscoord.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "GpsTimeAddressCoord")
public class Addresses {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public double lat;
    public double lng;
    public String addressName;

}

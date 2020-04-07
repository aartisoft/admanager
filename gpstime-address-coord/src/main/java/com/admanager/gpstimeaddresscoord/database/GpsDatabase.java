package com.admanager.gpstimeaddresscoord.database;

import androidx.room.RoomDatabase;

import com.admanager.gpstimeaddresscoord.model.Addresses;

@androidx.room.Database(entities = {Addresses.class}, version = 1, exportSchema = false)
public abstract class GpsDatabase extends RoomDatabase {
    public abstract LocationDao locationDao();
}

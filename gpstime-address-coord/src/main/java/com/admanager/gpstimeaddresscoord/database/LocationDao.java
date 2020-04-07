package com.admanager.gpstimeaddresscoord.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.admanager.gpstimeaddresscoord.model.Addresses;

import java.util.List;

@Dao
public interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAddress(Addresses... addresses);

    @Query("delete from GpsTimeAddressCoord where id = :id and addressName = :addressName ")
    void deleteAddress(int id, String addressName);

    @Query("select * from GpsTimeAddressCoord order by id desc")
    List<Addresses> getAddressList();
}

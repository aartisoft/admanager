package com.admanager.gpstimeaddresscoord.common;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.room.Room;

import com.admanager.gpstimeaddresscoord.R;
import com.admanager.gpstimeaddresscoord.database.GpsDatabase;

public class Common {

    public static GpsDatabase getDatabase(Context context) {
        return Room.databaseBuilder(context, GpsDatabase.class, "Gps_Time_Addresses_Coodinate Database").build();
    }

    public static void copyText(Context context, String copyText, String label) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null)
            return;

        ClipData clip = ClipData.newPlainText(label, copyText);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, context.getResources().getString(R.string.coppied), Toast.LENGTH_SHORT).show();
    }

    public static void goLocation(Context context, double lat, double lng) {
        if (lat != 0 && lng != 0) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + lat + "," + lng));
            context.startActivity(intent);
        } else {
            Toast.makeText(context, R.string.gps_no_location_warning, Toast.LENGTH_SHORT).show();
        }
    }
}

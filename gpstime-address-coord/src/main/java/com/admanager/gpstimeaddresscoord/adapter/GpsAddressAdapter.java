package com.admanager.gpstimeaddresscoord.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.admanager.core.Consts;
import com.admanager.gpstimeaddresscoord.GPSTimeApp;
import com.admanager.gpstimeaddresscoord.R;
import com.admanager.gpstimeaddresscoord.common.Common;
import com.admanager.gpstimeaddresscoord.database.LocationDao;
import com.admanager.gpstimeaddresscoord.model.Addresses;
import com.admanager.recyclerview.BaseAdapter;
import com.admanager.recyclerview.BindableViewHolder;

public class GpsAddressAdapter extends BaseAdapter<Addresses, GpsAddressAdapter.GpsAddressViewHolder> {

    RefreshListener listener;

    public GpsAddressAdapter(Activity activity, RefreshListener listener) {
        super(activity, R.layout.adm_gps_coord_item);
        this.listener = listener;
    }

    @Override
    protected GpsAddressViewHolder createViewHolder(View view) {
        return new GpsAddressViewHolder(view);
    }

    public interface RefreshListener {
        void onRefresh();
    }

    public class GpsAddressViewHolder extends BindableViewHolder<Addresses> {

        private final CardView cardSaved;
        private TextView coordinates, addressName, savedAddress;
        private ImageView deleteLocation, goLocation, copyLocation;

        public GpsAddressViewHolder(@NonNull View itemView) {
            super(itemView);
            coordinates = itemView.findViewById(R.id.gps_coordinates);
            addressName = itemView.findViewById(R.id.gps_addressName);
            copyLocation = itemView.findViewById(R.id.gps_copy);
            goLocation = itemView.findViewById(R.id.gps_locate);
            cardSaved = itemView.findViewById(R.id.cardSaved);
            deleteLocation = itemView.findViewById(R.id.gps_delete);
            savedAddress = itemView.findViewById(R.id.gps_savedAddress);
        }

        @Override
        public void bindTo(final Activity activity, final Addresses model, int position) {
            addressName.setText(model.addressName);
            savedAddress.setText(model.savedAddress);
            String coord = model.lat + "," + model.lng;
            coordinates.setText(coord);
            configureStyle(activity);
            deleteLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDialog(activity, model);

                }
            });

            copyLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String copyTextBody = model.addressName + "\n\n" + model.lat + "," + model.lng;
                    Common.copyText(activity, copyTextBody, model.addressName);
                }
            });

            goLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Common.goLocation(activity, model.lat, model.lng);
                }
            });
        }

        private void configureStyle(Activity activity) {
            GPSTimeApp instance = GPSTimeApp.getInstance();
            if (instance == null) {
                Log.e(Consts.TAG, "init GPS-Address-Coord module in Application class");
            } else {
                if (instance.cardBgColor != 0) {
                    cardSaved.setBackgroundColor(ContextCompat.getColor(activity, instance.cardBgColor));
                }
                if (instance.textColor != 0) {
                    addressName.setTextColor(ContextCompat.getColor(activity, instance.textColor));
                    savedAddress.setTextColor(ContextCompat.getColor(activity, instance.textColor));
                    coordinates.setTextColor(ContextCompat.getColor(activity, instance.textColor));
                }
            }
        }

        private void showDeleteDialog(final Activity activity, final Addresses model) {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.gps_delete_title)
                    .setMessage(R.string.gps_delete_message)
                    .setPositiveButton(R.string.gps_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteItem(activity, model);
                            dialog.dismiss();
                        }
                    }).setNegativeButton(R.string.gps_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }

        private void deleteItem(final Activity activity, final Addresses model) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LocationDao dao = Common.getDatabase(activity).locationDao();
                    dao.deleteAddress(model.id, model.addressName);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onRefresh();
                            Toast.makeText(activity, activity.getResources().getString(R.string.gps_deleted), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        }
    }
}

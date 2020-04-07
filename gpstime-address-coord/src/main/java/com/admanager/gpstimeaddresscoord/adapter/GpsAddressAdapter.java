package com.admanager.gpstimeaddresscoord.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.admanager.gpstimeaddresscoord.R;
import com.admanager.gpstimeaddresscoord.common.Common;
import com.admanager.gpstimeaddresscoord.database.LocationDao;
import com.admanager.gpstimeaddresscoord.model.Addresses;
import com.admanager.recyclerview.BaseAdapter;
import com.admanager.recyclerview.BindableViewHolder;

public class GpsAddressAdapter extends BaseAdapter<Addresses, GpsAddressAdapter.GpsAddressViewHolder> {

    RefreshListener listener;

    public GpsAddressAdapter(Activity activity, RefreshListener listener) {
        super(activity, R.layout.gps_coord_item_addresses);
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

        private TextView coordinates, addressName;
        private ImageView deleteLocation, goLocation, copyLocation;

        public GpsAddressViewHolder(@NonNull View itemView) {
            super(itemView);
            coordinates = itemView.findViewById(R.id.gps_coordinates);
            addressName = itemView.findViewById(R.id.gps_addressName);
            copyLocation = itemView.findViewById(R.id.gps_copy);
            goLocation = itemView.findViewById(R.id.gps_locate);
            deleteLocation = itemView.findViewById(R.id.gps_delete);
        }

        @Override
        public void bindTo(final Activity activity, final Addresses model, int position) {
            addressName.setText(model.addressName);
            String coord = model.lat + "," + model.lng;
            coordinates.setText(coord);

            deleteLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(activity, model);
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

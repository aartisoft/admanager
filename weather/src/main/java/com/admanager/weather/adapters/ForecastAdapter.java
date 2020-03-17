package com.admanager.weather.adapters;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.admanager.recyclerview.BaseAdapter;
import com.admanager.recyclerview.BindableViewHolder;
import com.admanager.weather.R;
import com.admanager.weather.WeatherApp;
import com.admanager.weather.day.HourlyWeathers;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;

public class ForecastAdapter extends BaseAdapter<HourlyWeathers, ForecastAdapter.ViewHolder> {

    public ForecastAdapter(Activity activity) {
        super(activity, R.layout.item_forecast);
    }

    @Override
    protected ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    public class ViewHolder extends BindableViewHolder<HourlyWeathers> {
        private final WeatherApp weatherApp;
        private final LinearLayout itemRoot;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        private ImageView itemIcon;
        private TextView itemDate, itemWind, itemPressure, itemCelsius, itemHumidity, itemStatus;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemIcon = itemView.findViewById(R.id.item_icon);
            itemWind = itemView.findViewById(R.id.item_wind);
            itemPressure = itemView.findViewById(R.id.item_pressure);
            itemHumidity = itemView.findViewById(R.id.item_humidity);
            itemCelsius = itemView.findViewById(R.id.item_celcius);
            itemDate = itemView.findViewById(R.id.item_date);
            itemStatus = itemView.findViewById(R.id.item_status);
            itemRoot = itemView.findViewById(R.id.itemRoot);
            weatherApp = WeatherApp.getInstance();
        }

        @Override
        public void bindTo(Activity activity, HourlyWeathers model, int position) {
            if (weatherApp != null) {
                if (weatherApp.itemBg != 0) {
                    itemRoot.setBackgroundColor(ContextCompat.getColor(activity, weatherApp.itemBg));
                }
                if (weatherApp.textColor != 0) {
                    itemDate.setTextColor(ContextCompat.getColor(activity, weatherApp.textColor));
                    itemWind.setTextColor(ContextCompat.getColor(activity, weatherApp.textColor));
                    itemPressure.setTextColor(ContextCompat.getColor(activity, weatherApp.textColor));
                    itemCelsius.setTextColor(ContextCompat.getColor(activity, weatherApp.textColor));
                    itemHumidity.setTextColor(ContextCompat.getColor(activity, weatherApp.textColor));
                    itemStatus.setTextColor(ContextCompat.getColor(activity, weatherApp.textColor));
                }
            }
            Glide.with(activity).load(model.getImgUrl()).into(itemIcon);
            itemWind.setText(String.valueOf(model.getWindSpeed()));
            itemPressure.setText(String.valueOf(model.getPressure()));
            itemHumidity.setText(String.valueOf(model.getHumidity()));
            itemCelsius.setText(String.valueOf(model.getCelsius()));
            itemStatus.setText(model.getDescription());
            String mDate = dateFormat.format(model.getDate());
            itemDate.setText(mDate);
        }
    }
}

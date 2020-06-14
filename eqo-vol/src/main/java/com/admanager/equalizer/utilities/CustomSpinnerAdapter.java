package com.admanager.equalizer.utilities;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.admanager.equalizer.R;
import com.admanager.equalizer.models.EqualizerPreset;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {
    private final ArrayList<EqualizerPreset> equalizerPresetArrayList;
    private final Context context;

    private boolean showFirstItemEmpty = false;

    private EqualizerPreset equalizerPresetSelected;

    public CustomSpinnerAdapter(Context context, ArrayList brands) {
        this.equalizerPresetArrayList = brands;
        this.context = context;
    }

    public static void hideSpinnerDropDown(Spinner spinner) {
        try {
            Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
            method.setAccessible(true);
            method.invoke(spinner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        equalizerPresetArrayList.clear();
        notifyDataSetChanged();
    }

    public void setData(ArrayList<EqualizerPreset> arrayList) {
        equalizerPresetArrayList.addAll(arrayList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return equalizerPresetArrayList.size();
    }

    @Override
    public EqualizerPreset getItem(int position) {
        return getEqualizerPresetFromPositionNumber(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.custom_spinner_item, null);
        TextView textView = view.findViewById(R.id.spinner_item);

        String presetName = "";
        if (equalizerPresetSelected != null && !showFirstItemEmpty)
            presetName = equalizerPresetSelected.getPresetName();
        textView.setText(presetName);
        return textView;
    }

    @Override
    public View getDropDownView(final int position, View convertView, ViewGroup parent) {
        EqualizerPreset equalizerPreset = getEqualizerPresetFromPositionNumber(position);

        View view = View.inflate(context, R.layout.custom_spinner_dropdown_item, null);
        final TextView textView = view.findViewById(R.id.spinner_dropdown_item);

        if (equalizerPreset != null) {
            textView.setText(equalizerPreset.getPresetName());
        }

        //View view = super.getDropDownView(position, null, parent);
        // If this is the selected item position
        if (position == 0) {
            view.setBackgroundResource(R.drawable.drop_down_btn2);
        } else {
            // for other views
            view.setBackgroundColor(context.getResources().getColor(R.color.dropdown_list_item));

        }
        return view;
    }

    public void setSelectedPosition(int selectedPosition) {
        equalizerPresetSelected = getEqualizerPresetFromPositionNumber(selectedPosition);

        if (selectedPosition > 0) {
            EqualizerPreset equalizerPresetFirst = getEqualizerPresetFromPositionNumber(0);

            if (equalizerPresetSelected != null) equalizerPresetSelected.setPosition(0);
            if (equalizerPresetFirst != null) equalizerPresetFirst.setPosition(selectedPosition);
        }

        notifyDataSetChanged();
    }

    private EqualizerPreset getEqualizerPresetFromPositionNumber(int position) {
        for (int i = 0; i < equalizerPresetArrayList.size(); i++) {
            EqualizerPreset equalizerPreset = equalizerPresetArrayList.get(i);
            if (equalizerPreset.getPosition() == position) {
                return equalizerPreset;
            }
        }

        return null;
    }

    public boolean isShowFirstItemEmpty() {
        return showFirstItemEmpty;
    }

    public void setShowFirstItemEmpty(boolean showFirstItemEmpty) {
        this.showFirstItemEmpty = showFirstItemEmpty;
    }
}

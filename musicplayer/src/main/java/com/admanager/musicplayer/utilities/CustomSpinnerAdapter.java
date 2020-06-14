package com.admanager.musicplayer.utilities;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.admanager.musicplayer.R;
import com.admanager.musicplayer.models.SpinnerPreset;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {
    private final List<SpinnerPreset> spinnerPresetArrayList;
    private final Context context;

    private boolean showFirstItemEmpty = false;

    private SpinnerPreset spinnerPresetSelected;

    public CustomSpinnerAdapter(Context context, List<SpinnerPreset> brands) {
        this.spinnerPresetArrayList = brands;
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
        spinnerPresetArrayList.clear();
        notifyDataSetChanged();
    }

    public void add(SpinnerPreset spinnerPreset) {
        spinnerPresetArrayList.add(spinnerPreset);
        notifyDataSetChanged();
    }

    public void delete(SpinnerPreset spinnerPreset) {
        spinnerPresetArrayList.remove(spinnerPreset);
        notifyDataSetChanged();
    }

    public void setData(ArrayList<SpinnerPreset> arrayList) {
        spinnerPresetArrayList.addAll(arrayList);
        notifyDataSetChanged();
    }

    public List<SpinnerPreset> getSpinnerPresetArrayList() {
        return spinnerPresetArrayList;
    }

    @Override
    public int getCount() {
        return spinnerPresetArrayList.size();
    }

    @Override
    public SpinnerPreset getItem(int position) {
        return getEqualizerPresetFromPositionNumber(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.mp_custom_spinner_item, null);
        TextView textView = view.findViewById(R.id.spinner_item);
        textView.setGravity(Gravity.CENTER);

        String presetName;
        if (spinnerPresetSelected != null && !showFirstItemEmpty) {
            presetName = spinnerPresetSelected.getPresetName();
        } else {
            spinnerPresetSelected = getItem(0);
            presetName = spinnerPresetSelected.getPresetName();
        }
        textView.setText(presetName);
        return textView;
    }

    @Override
    public View getDropDownView(final int position, View convertView, ViewGroup parent) {
        SpinnerPreset spinnerPreset = getEqualizerPresetFromPositionNumber(position);

        View view = View.inflate(context, R.layout.mp_custom_spinner_dropdown_item, null);
        final TextView textView = view.findViewById(R.id.spinner_dropdown_item);

        if (spinnerPreset != null) {
            textView.setText(spinnerPreset.getPresetName());
        }

        //View view = super.getDropDownView(position, null, parent);
        // If this is the selected item position
        if (position == 0) {
            view.setBackgroundResource(R.drawable.mp_drop_down_btn2);
        } else {
            // for other views
            view.setBackgroundColor(context.getResources().getColor(R.color.mpdropdown_list_item));

        }
        return view;
    }

    public void setSelectedPosition(int selectedPosition) {
        spinnerPresetSelected = getEqualizerPresetFromPositionNumber(selectedPosition);

        if (selectedPosition > 0) {
            SpinnerPreset spinnerPresetFirst = getEqualizerPresetFromPositionNumber(0);

            if (spinnerPresetSelected != null) spinnerPresetSelected.setPosition(0);
            if (spinnerPresetFirst != null) spinnerPresetFirst.setPosition(selectedPosition);
        }

        notifyDataSetChanged();
    }

    private SpinnerPreset getEqualizerPresetFromPositionNumber(int position) {
        for (int i = 0; i < spinnerPresetArrayList.size(); i++) {
            SpinnerPreset spinnerPreset = spinnerPresetArrayList.get(i);
            if (spinnerPreset.getPosition() == position) {
                return spinnerPreset;
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

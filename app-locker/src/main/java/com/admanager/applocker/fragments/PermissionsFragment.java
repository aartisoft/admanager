package com.admanager.applocker.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import androidx.fragment.app.DialogFragment;

import com.admanager.applocker.R;
import com.admanager.applocker.activities.PasswordActivity;
import com.admanager.applocker.prefrence.Prefs;
import com.admanager.applocker.utils.AppLockInitializer;

public class PermissionsFragment extends DialogFragment implements View.OnClickListener {
    ToggleButton usage_access;
    ToggleButton password;
    ImageView usage_access_arrow;
    ImageView password_arrow;
    LinearLayout usage_access_container;
    LinearLayout password_container;

    public PermissionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_permissions, container, false);

        usage_access = v.findViewById(R.id.perm_usage_access_switch);
        password = v.findViewById(R.id.password_switch);

        usage_access_arrow = v.findViewById(R.id.perm_usage_access_arrow);
        password_arrow = v.findViewById(R.id.password_arrow);

        usage_access_container = v.findViewById(R.id.perm_usage_access_container);
        password_container = v.findViewById(R.id.password_container);

        boolean isUsageStats = AppLockInitializer.hasUsageStatsPermission(getContext());
        boolean isPasswordSet = Prefs.with(getContext()).isPasswordSet();

        usage_access_container.setOnClickListener(isUsageStats ? null : this);
        password_container.setOnClickListener(isPasswordSet ? null : this);

        usage_access.setChecked(isUsageStats);
        password.setChecked(isPasswordSet);

        usage_access_arrow.setVisibility(isUsageStats ? View.GONE : View.VISIBLE);
        password_arrow.setVisibility(isPasswordSet ? View.GONE : View.VISIBLE);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.perm_usage_access_container) {
            AppLockInitializer.goToUsageAccessPermissionSettings(getActivity());
        } else if (id == R.id.password_container) {
            PasswordActivity.startPasswordSet(getActivity(), true);
        }
        dismiss();
    }
}

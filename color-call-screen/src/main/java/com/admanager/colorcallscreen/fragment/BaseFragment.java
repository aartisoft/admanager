package com.admanager.colorcallscreen.fragment;

import android.support.v4.app.Fragment;

import androidx.navigation.NavController;
import androidx.navigation.NavDirections;

import com.admanager.colorcallscreen.activities.ColorCallScreenActivity;

public abstract class BaseFragment extends Fragment {

    public ColorCallScreenActivity getColorCallScreenActivity() {
        Object act = super.getActivity();
        if (act == null) {
            return null;
        }
        return (ColorCallScreenActivity) super.getActivity();
    }

    public void navigate(NavDirections directions) {
        getNavController().navigate(directions);
    }

    public void navigateUp() {
        getNavController().navigateUp();
    }

    public NavController getNavController() {
        ColorCallScreenActivity colorCallScreenActivity = getColorCallScreenActivity();
        if (colorCallScreenActivity == null) {
            return null;
        }
        return colorCallScreenActivity.navController;
    }
}
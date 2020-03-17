package com.admanager.applocker.fragments;

import androidx.fragment.app.Fragment;

import com.admanager.applocker.activities.HomeActivity;

public class BaseFragment extends Fragment {

    public HomeActivity getMainActivity() {
        Object act = super.getActivity();
        if (act == null) {
            return null;
        }
        return (HomeActivity) super.getActivity();
    }
}

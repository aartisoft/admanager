package com.admanager.popupenjoy;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.admanager.core.AdManager;
import com.bumptech.glide.Glide;

import java.util.List;

public class PopupEnjoyFragment extends DialogFragment implements View.OnClickListener {
    private static final String AD_SPECS = "enjoySpecs";
    private static final String TAG = "PopupEnjoyFragment";
    private TextView title;
    private ImageView imageView;
    private Button yes;
    private TextView no;
    private EnjoySpecs enjoySpecs;
    private AdManager adManager;
    private AdmPopupEnjoy.Ads ads;
    private AdmPopupEnjoy.Listener listener;

    public static PopupEnjoyFragment createInstance(EnjoySpecs enjoySpecs, AdmPopupEnjoy.Ads ads, AdmPopupEnjoy.Listener listener) {
        PopupEnjoyFragment fragment = new PopupEnjoyFragment();
        fragment.setCancelable(false);
        fragment.enjoySpecs = enjoySpecs;
        fragment.ads = ads;
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_enjoy_layout, parent, false);
        getDialog().setCanceledOnTouchOutside(false);
        if (ads != null) {
            adManager = ads.createAdManagerBuilder(getActivity())
                    .listener(new AdManager.Listener() {
                        @Override
                        public void finishedAll() {
                            if (listener != null) {
                                listener.completed(false);
                            }
                        }

                        @Override
                        public void initializedAll(List<Boolean> loaded) {

                        }
                    })
                    .build();
            ads.loadBottom(getActivity(), (LinearLayout) view.findViewById(R.id.container));
        }

        title = view.findViewById(R.id.title);
        imageView = view.findViewById(R.id.image_view);
        yes = view.findViewById(R.id.yes);
        no = view.findViewById(R.id.no);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        if (savedInstanceState != null && savedInstanceState.getSerializable(AD_SPECS) != null) {
            enjoySpecs = (EnjoySpecs) savedInstanceState.getSerializable(AD_SPECS);
        }

        return view;

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(AD_SPECS, enjoySpecs);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!TextUtils.isEmpty(enjoySpecs.getTitle())) {
            title.setText(enjoySpecs.getTitle());
        }
        if (!TextUtils.isEmpty(enjoySpecs.getYes())) {
            yes.setText(enjoySpecs.getYes());
        }
        if (!TextUtils.isEmpty(enjoySpecs.getNo())) {
            no.setText(enjoySpecs.getNo());
        }

        if (TextUtils.isEmpty(enjoySpecs.getImageUrl())) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(enjoySpecs.getImageUrl())
                    .into(imageView);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == yes.getId()) {
            if (adManager != null) {
                adManager.show();
            }
            try {
                dismiss();
            } catch (Exception ignore) {
                //rare situation
            }
        } else if (id == no.getId()) {
            if (adManager != null) {
                adManager.show();
            }
            try {
                dismiss();
            } catch (Exception ignore) {
                //rare situtation
            }
        }
    }

}

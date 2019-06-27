package com.admanager.popupad;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.admanager.core.AdmUtils;
import com.admanager.userad.R;
import com.bumptech.glide.Glide;

public class PopupAdFragment extends DialogFragment implements View.OnClickListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener, MediaPlayer.OnErrorListener {
    private static final String AD_SPECS = "adSpecs";
    private static final String TAG = "PopupAdFragment";
    private ImageView logo;
    private TextView title;
    private TextView body;
    private RelativeLayout videoViewContainer;
    private VideoView videoView;
    private ProgressBar videoLoading;
    private ImageView imageView;
    private Button mute;
    private Button yes;
    private Button no;
    private MediaPlayer mp;
    private AdSpecs adSpecs;

    private boolean muted = true;

    public static PopupAdFragment createInstance(AdSpecs adSpecs) {
        PopupAdFragment fragment = new PopupAdFragment();
        fragment.setCancelable(false);
        fragment.adSpecs = adSpecs;
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
        View view = inflater.inflate(R.layout.popup_ad_layout, parent, false);
        getDialog().setCanceledOnTouchOutside(false);

        logo = view.findViewById(R.id.logo);
        title = view.findViewById(R.id.title);
        body = view.findViewById(R.id.body);
        videoViewContainer = view.findViewById(R.id.video_view_container);
        videoLoading = view.findViewById(R.id.video_loading);
        videoView = view.findViewById(R.id.video_view);
        imageView = view.findViewById(R.id.image_view);
        mute = view.findViewById(R.id.mute);
        yes = view.findViewById(R.id.yes);
        no = view.findViewById(R.id.no);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        mute.setOnClickListener(this);

        if (savedInstanceState != null && savedInstanceState.getSerializable(AD_SPECS) != null) {
            adSpecs = (AdSpecs) savedInstanceState.getSerializable(AD_SPECS);
        }

        return view;

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(AD_SPECS, adSpecs);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title.setText(adSpecs.getTitle());
        body.setText(adSpecs.getMessage());
        yes.setText(adSpecs.getYes());
        no.setText(adSpecs.getNo());

        if (TextUtils.isEmpty(adSpecs.getVideoUrl())) {
            videoViewContainer.setVisibility(View.GONE);
        } else {
            videoViewContainer.setVisibility(View.VISIBLE);
            mute.setVisibility(View.GONE);
            videoLoading.setVisibility(View.VISIBLE);

            videoView.setVideoURI(Uri.parse(adSpecs.getVideoUrl()));
            videoView.setOnPreparedListener(this);
            videoView.setOnErrorListener(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                videoView.setOnInfoListener(this);
            }
            videoView.requestFocus();
        }

        if (TextUtils.isEmpty(adSpecs.getImageUrl())) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(adSpecs.getImageUrl())
                    .into(imageView);
        }

        if (TextUtils.isEmpty(adSpecs.getLogoUrl())) {
            logo.setVisibility(View.GONE);
        } else {
            logo.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(adSpecs.getLogoUrl())
                    .into(logo);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == yes.getId()) {
            AdmUtils.openLink(getContext(), adSpecs.getUrl());
            try {
                dismiss();
            } catch (Exception ignore) {
                //rare situtation
            }
        } else if (id == no.getId()) {
            try {
                dismiss();
            } catch (Exception ignore) {
                //rare situtation
            }
        } else if (id == mute.getId()) {
            if (muted) {
                unmute();
            } else {
                mute();
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        this.mp = mp;
        mute.setVisibility(View.VISIBLE);
        videoLoading.setVisibility(View.GONE);
        try {
            mp.start();
            unmute();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void mute() {
        if (mp != null && mp.isPlaying()) {
            mp.setVolume(0f, 0f);
            mute.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.sound_on_icon));
            muted = true;
        }
    }

    private void unmute() {
        if (mp != null && mp.isPlaying()) {
            mp.setVolume(1f, 1f);
            mute.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.mute_icon));
            muted = false;
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "onInfo: " + what + " -> " + extra);
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "onError: " + what + " -> " + extra);
        videoViewContainer.setVisibility(View.GONE);
        return true;
    }
}

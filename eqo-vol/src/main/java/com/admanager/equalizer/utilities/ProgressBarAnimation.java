package com.admanager.equalizer.utilities;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.SeekBar;

public class ProgressBarAnimation extends Animation {
    private SeekBar seekbar;
    private float from;
    private float to;

    public ProgressBarAnimation(SeekBar seekbar, float from, float to) {
        super();
        this.seekbar = seekbar;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        seekbar.setProgress((int) value);
    }

}
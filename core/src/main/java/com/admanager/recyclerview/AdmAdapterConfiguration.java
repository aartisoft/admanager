package com.admanager.recyclerview;

import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;

public class AdmAdapterConfiguration<NATIVE_TYPE> {
    private static final String TAG = "AdmAdapterConf";

    private int gridSize = 1;
    private int density = 3;
    private int customNativeLayout;
    private boolean densityChanged;
    private boolean nativeInGrid;
    private NATIVE_TYPE type;

    public AdmAdapterConfiguration() {
    }

    public AdmAdapterConfiguration<NATIVE_TYPE> gridSize(@IntRange(from = 1, to = 99) int gridSize) {
        this.gridSize = gridSize;
        return this;
    }

    public AdmAdapterConfiguration<NATIVE_TYPE> type(NATIVE_TYPE type) {
        this.type = type;
        return this;
    }

    public AdmAdapterConfiguration<NATIVE_TYPE> customNativeLayout(@LayoutRes int customNativeLayout) {
        this.customNativeLayout = customNativeLayout;
        return this;
    }

    public AdmAdapterConfiguration<NATIVE_TYPE> nativeInGrid() {
        this.nativeInGrid = true;
        return this;
    }

    public AdmAdapterConfiguration<NATIVE_TYPE> density(@IntRange(from = 0, to = 99) int density) {
        this.density = density;
        this.densityChanged = true;
        return this;
    }

    int getGridSize() {
        return gridSize;
    }

    NATIVE_TYPE getType() {
        return type;
    }

    int getDensity() {
        return density;
    }

    int getCustomNativeLayout() {
        return customNativeLayout;
    }

    boolean isNativeInGrid() {
        return nativeInGrid;
    }

    int getDensityForGrid() {
        if (densityChanged) {
            return density;
        }
        return 1;
    }
}

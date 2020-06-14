package com.admanager.equalizer.models;

public class EqualizerPreset {
    private short presetNumber;
    private String presetName;
    private int position;

    public short getPresetNumber() {
        return presetNumber;
    }

    public void setPresetNumber(short presetNumber) {
        this.presetNumber = presetNumber;
    }

    public String getPresetName() {
        return presetName;
    }

    public void setPresetName(String presetName) {
        this.presetName = presetName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}

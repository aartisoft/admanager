package com.admanager.unseen.notiservice.converters;

import java.io.Serializable;

public class ConverterData implements Serializable {
    private String packageName;
    private String type;
    private String title;
    private int color;

    public ConverterData(String packageName, String type, String title, int color) {
        this.packageName = packageName;
        this.type = type;
        this.title = title;
        this.color = color;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

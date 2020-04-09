package com.admanager.recyclerview;

public interface ClickListener<T> {
    void clicked(T model, int position);
}

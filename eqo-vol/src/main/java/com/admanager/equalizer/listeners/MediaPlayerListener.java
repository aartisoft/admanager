package com.admanager.equalizer.listeners;

public interface MediaPlayerListener {
    void onPrepared();

    boolean onError(int what, int extra);
}

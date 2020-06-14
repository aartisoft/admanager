package com.admanager.musicplayer.listeners;

public interface MPMediaPlayerListener {
    void onPrepared();

    boolean onError(int what, int extra);
}

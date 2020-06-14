package com.admanager.equalizer.listeners;

public interface IPlayer {
    void progressChanged(int progress, long currentDuration, long totalDuration);

    void onCompletion();

    void onBufferingUpdate(int percent);

    void onPrepared();

    boolean onError(int what, int extra);
}

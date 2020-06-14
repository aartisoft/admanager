package com.admanager.musicplayer.listeners;

import com.admanager.musicplayer.models.Track;

public interface MPIPlayer {
    void progressChanged(int progress, long currentDuration, long totalDuration);

    void onCompletion();

    void onStart(Track track);

    void onPause(Track track);

    void onBufferingUpdate(int percent);

    void onPrepared();

    boolean onError(int what, int extra);
}

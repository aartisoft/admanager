package com.admanager.equalizer.listeners;

import com.admanager.equalizer.models.Music;

import java.util.ArrayList;
import java.util.List;

public class UpdateBroadcastReceiverListener {
    private static UpdateBroadcastReceiverListener updateBroadcastReceiverListener;

    /* renamed from: a */
    public final List<receivedBroadcastInterface> receivedBroadcastList = new ArrayList<>();

    public static UpdateBroadcastReceiverListener getInstance() {
        if (updateBroadcastReceiverListener == null) {
            updateBroadcastReceiverListener = new UpdateBroadcastReceiverListener();
        }
        return updateBroadcastReceiverListener;
    }

    /* renamed from: com.music.sound.speaker.volume.booster.equalizer.ui.view.yp$a */
    public interface receivedBroadcastInterface {
        /* renamed from: a */
        void receivedBroadcast(Music music);
    }
}

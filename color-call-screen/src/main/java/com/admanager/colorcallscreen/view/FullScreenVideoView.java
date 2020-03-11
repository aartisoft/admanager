package com.admanager.colorcallscreen.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.AttributeSet;
import android.widget.VideoView;

public class FullScreenVideoView extends VideoView {
    private boolean fullscreen = true;

    public FullScreenVideoView(Context context) {
        super(context);
    }

    public FullScreenVideoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public FullScreenVideoView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        //        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, com.admanager.colorcallscreen.view.FullScreenVideoView);
        //        this.fullscreen = obtainStyledAttributes.getBoolean(0, false);
        //        obtainStyledAttributes.recycle();
    }

    protected void onMeasure(int i, int i2) {
        if (this.fullscreen) {
            setMeasuredDimension(getDefaultSize(0, i), getDefaultSize(0, i2));
        } else {
            super.onMeasure(i, i2);
        }
    }

    public interface Listener {
        void onInfo();

        void onPrepared(MediaPlayer mediaPlayer);
    }

    class CustomOnPreparedListener implements OnPreparedListener {
        final Listener listener;

        CustomOnPreparedListener(Listener listener) {
            this.listener = listener;
        }

        public final void onPrepared(MediaPlayer mediaPlayer) {
            mediaPlayer.setLooping(true);
            if (this.listener != null) {
                this.listener.onPrepared(mediaPlayer);
            }
        }
    }

    class CustomOnInfoListener implements OnInfoListener {
        final Listener listener;

        CustomOnInfoListener(Listener listener) {
            this.listener = listener;
        }

        public final boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
            if (i != 3) {
                return false;
            }
            if (this.listener != null) {
                this.listener.onInfo();
            }
            return true;
        }
    }
}
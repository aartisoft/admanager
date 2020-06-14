package com.admanager.musicplayer.tasks;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import com.admanager.musicplayer.listeners.MPMediaPlayerListener;

import java.util.ArrayList;

public class MediaPlayerManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    //private static final String TAG = "MPTag";
    private static MediaPlayerManager instance;
    private final ArrayList<MPMediaPlayerListener> MPMediaPlayerListeners = new ArrayList<>();
    private boolean initialized = false;
    private MediaPlayer player;
    private String filePath = null;
    private Uri uri = null;
    private int mediaFileLengthInMilliseconds = 0;
    private boolean prepared = false;
    private boolean preparing = false;
    private int playPositionInMilliSeconds = 0;

    public MediaPlayerManager() {

    }

    public static MediaPlayerManager getInstance() {
        if (instance == null) {
            instance = new MediaPlayerManager();
            instance.initialized = false;
        }

        return instance;
    }

    public void addPlayerListener(MPMediaPlayerListener playerListener) {
        MPMediaPlayerListeners.add(playerListener);
    }

    public void removePlayerListener(MPMediaPlayerListener playerListener) {
        MPMediaPlayerListeners.remove(playerListener);
    }

    public void initialize() {
        if (!initialized) {
            initialized = true;

            player = new MediaPlayer();

            /*player.setOnSeekCompleteListener(arg0 -> {

                if(player!=null && player.isPlaying())
                    player.start();
            });*/

            filePath = null;
            mediaFileLengthInMilliseconds = 0;
        }
    }

    public void setUri(Context context, Uri newUri) {
        if (newUri != null && player != null) {
            if (uri != null && uri != newUri) {
                player.reset();
            }

            try {
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(context, newUri);

                prepared = false;

                preparing = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.uri = newUri;
    }

    public void reset() {
        if (player != null) {
            player.reset();
        }
    }

    /****
     * This method is used to play media player.
     * This method must be called only in first activity which is splash activity and settings activity that set background music on or off.
     */
    public void play() {
        if (player != null) {
            if (prepared) {
                player.start();
            } else {
                try {
                    player.prepareAsync();
                    player.setOnPreparedListener(this);
                    player.setOnErrorListener(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Called when MediaPlayer is ready
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        if (player != null) {
            prepared = true;

            preparing = false;

            mediaFileLengthInMilliseconds = getDuration();
            if (playPositionInMilliSeconds > 0) {
                seekTo(playPositionInMilliSeconds);

                playPositionInMilliSeconds = 0;
            }
            player.start();
            for (MPMediaPlayerListener mPlayerListener : MPMediaPlayerListeners) {
                mPlayerListener.onPrepared();
            }
        }
    }

    /****
     * This method is used to pause media player.
     * This method must be called only in settings activity that set background music on or off.
     */
    public void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    /*****
     * This method is used to release media player.
     * This method must be called in main activity which created media player.
     */
    public void release() {
        if (player != null) {
            player.stop();

            player.release();

            player = null;

            instance = null;

            filePath = null;

            mediaFileLengthInMilliseconds = 0;
        }
    }

    public void seekTo(int msec) {
        if (player != null) {
            player.seekTo(msec);
        }
    }

    public int getDuration() {
        if (player != null) {
            return player.getDuration();
        }

        return 0;
    }

    public int getCurrentPosition() {
        if (player != null) {
            return player.getCurrentPosition();
        }

        return 0;
    }

    public int getMediaFileLengthInMilliseconds() {
        if (player != null) {
            return mediaFileLengthInMilliseconds;
        }

        return 0;
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        if (player != null) {
            player.setOnCompletionListener(listener);
        }
    }

    public void setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener listener) {
        if (player != null) {
            player.setOnBufferingUpdateListener(listener);
        }
    }

    public boolean isPlaying() {
        if (player != null) {

            return (player.isPlaying() || preparing);
        }

        return false;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String newFilePath) {
        if (newFilePath != null && player != null) {
            newFilePath = newFilePath.replaceAll(" ", "%20");

            if (filePath != null && !filePath.equalsIgnoreCase(newFilePath)) {
                player.reset();
            }

            try {
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(newFilePath);

                prepared = false;

                preparing = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.filePath = newFilePath;
    }

    public Uri getUri() {
        return uri;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Log.i(TAG, "what="+what+", "+"extra="+extra);

        for (MPMediaPlayerListener mPlayerListener : MPMediaPlayerListeners) {
            mPlayerListener.onError(what, extra);
        }

        if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN || what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            player.reset();
        }

        return true;
    }

    public boolean isPreparing() {
        return preparing;
    }

    public int getAudioSession() {
        return player.getAudioSessionId();
    }

    public int getPlayPositionInMilliSeconds() {
        return playPositionInMilliSeconds;
    }

    public void setPlayPositionInMilliSeconds(int playPositionInMilliSeconds) {
        this.playPositionInMilliSeconds = playPositionInMilliSeconds;
    }
}

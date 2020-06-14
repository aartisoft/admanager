package com.admanager.musicplayer.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Virtualizer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.admanager.musicplayer.R;
import com.admanager.musicplayer.activities.MPMusicActivity;
import com.admanager.musicplayer.listeners.MPIPlayer;
import com.admanager.musicplayer.listeners.MPMediaPlayerListener;
import com.admanager.musicplayer.models.Track;
import com.admanager.musicplayer.tasks.MediaPlayerManager;
import com.admanager.musicplayer.utilities.Constants;
import com.admanager.musicplayer.utilities.MediaUtils;
import com.admanager.musicplayer.utilities.SharedPrefUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MPMediaPlayerListener, AudioManager.OnAudioFocusChangeListener {
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_PLAY = "ACTION_PLAY";
    private final ArrayList<MPIPlayer> mPlayerListeners = new ArrayList<>();
    private final Handler handler = new Handler();
    private final Random random = new Random();
    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();
    public Equalizer equalizer;
    private MediaPlayerManager mediaPlayerManager;
    private List<Track> trackArrayList = new ArrayList<>();
    private int index = 0;
    private Track track;
    private AudioManager mAudioManager;
    private boolean pausedForAudioFocus = false;
    private boolean newTrack = false;
    private BassBoost bassBoost;
    private Visualizer visualizer;
    private Virtualizer virtualizer;
    private int progress;

    public MediaPlayerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initMediaPlayer();

        trackArrayList = SharedPrefUtils.getPlayerCurrentTrackList(this);

        index = SharedPrefUtils.getPlayerCurrentPosition(this);

        if (trackArrayList != null && trackArrayList.size() > 0 && index >= 0 && index < trackArrayList.size()) {
            track = trackArrayList.get(index);
            newTrack = true;

            int seekbarPosition = SharedPrefUtils.getTrackSeekbarPosition(this);

            int playPositionInMilliSeconds = (int) (track.getDuration() / 100) * seekbarPosition;

            if (playPositionInMilliSeconds > 0) {
                mediaPlayerManager.setPlayPositionInMilliSeconds(playPositionInMilliSeconds);
            }
        }
    }

    private void initMediaPlayer() {
        mediaPlayerManager = MediaPlayerManager.getInstance();
        mediaPlayerManager.initialize();

        mediaPlayerManager.setOnCompletionListener(this);
        mediaPlayerManager.setOnBufferingUpdateListener(this);

        mediaPlayerManager.addPlayerListener(this);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }

        if (equalizer == null) {
            this.equalizer = new Equalizer(Integer.MAX_VALUE, mediaPlayerManager.getAudioSession());
            setEqualizerEnabled(true);
        }

        if (bassBoost == null) {
            this.bassBoost = new BassBoost(Integer.MAX_VALUE, mediaPlayerManager.getAudioSession());
            bassBoost.setEnabled(true);
        }

        if (virtualizer == null) {
            this.virtualizer = new Virtualizer(Integer.MAX_VALUE, mediaPlayerManager.getAudioSession());
        }
    }

    public void setEqualizerBandLevel(short equalizerBandIndex, short bandLevel) {
        try {
            if (equalizer == null) {
                equalizer = new Equalizer(Integer.MAX_VALUE, mediaPlayerManager.getAudioSession());
                setEqualizerEnabled(true);
            }

            equalizer.setBandLevel(equalizerBandIndex, bandLevel);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e2) {
            e2.printStackTrace();
        } catch (UnsupportedOperationException e3) {
            e3.printStackTrace();
        }
    }

    public void setBassBoostStrength(short strength) {
        if (strength == 0) {
            bassBoost.setStrength((short) 0);

            bassBoost.setEnabled(false);
        } else if (strength > 0 && strength <= 1000) {
            if (!bassBoost.getEnabled())
                bassBoost.setEnabled(true);

            bassBoost.setStrength(strength);
        }
    }

    public void setVirtualizerStrength(short strength) {
        virtualizer.setStrength(strength);
    }

    /* renamed from: b */
    public final void setEqualizerEnabled(boolean enabled) {
        try {
            if (equalizer != null) {
                equalizer.setEnabled(enabled);
            }
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        } catch (IllegalStateException e2) {
            e2.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (track != null) track.setPlaying(false);

        Constants.REPLAY_TYPE replay_type = SharedPrefUtils.getReplay(this);

        if (replay_type == Constants.REPLAY_TYPE.NO_REPLAY) {
            setNoReplayForCompletion();
        } else if (replay_type == Constants.REPLAY_TYPE.REPLAY_ALL) {
            setReplayAllForCompletion();
        } else if (replay_type == Constants.REPLAY_TYPE.REPLAY_ONE) {
            setReplayOneForCompletion();
        }
    }

    private void setNoReplayForCompletion() {
        Track nextTrack = getNextTrack();
        if (nextTrack != null) {
            track = nextTrack;
            newTrack = true;

            play();
        } else {
            for (MPIPlayer mPlayerListener : mPlayerListeners) {
                mPlayerListener.onCompletion();

                showNotification();
            }
        }
    }

    private void setReplayAllForCompletion() {
        Track nextTrack = getNextTrack();
        if (nextTrack != null) {
            track = nextTrack;
            newTrack = true;

            play();
        } else {
            if (trackArrayList.size() > 0) {
                index = 0;
                track = trackArrayList.get(0);
                newTrack = true;

                play();
            }
        }
    }

    private void setReplayOneForCompletion() {
        play();
    }

    private void setNewTrack() {
        if (track == null) return;

        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, track.getId());

        mediaPlayerManager.setUri(this, uri);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        for (MPIPlayer mPlayerListener : mPlayerListeners) {
            mPlayerListener.onBufferingUpdate(percent);
        }
    }

    public void addPlayerListener(MPIPlayer playerListener) {
        mPlayerListeners.add(playerListener);
    }

    public void removePlayerListener(MPIPlayer playerListener) {
        mPlayerListeners.remove(playerListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();

            switch (action) {
                case ACTION_START_FOREGROUND_SERVICE:
                    //initMediaPlayer();
                    //Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                case ACTION_PAUSE:
                    pause();
                    //Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_PLAY:
                    play();
                    //Toast.makeText(getApplicationContext(), "You click Play button.", Toast.LENGTH_LONG).show();
                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /* Used to build and start foreground service. */
    private void showNotification() {
        // Create notification default intent.
        Intent notificationIntent = new Intent(this, MPMusicActivity.class);
        //notificationIntent.putExtra(Constants.MEDIA_TYPE_NAME, getMediaType());
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final RemoteViews views = new RemoteViews(getPackageName(), R.layout.mp_notification);
        final RemoteViews bigViews = new RemoteViews(getPackageName(), R.layout.mp_notification_expanded);

        String mediaName;
        if (track != null) {
            if (TextUtils.isEmpty(track.getTitle()) && TextUtils.isEmpty(track.getArtist())) {
                mediaName = getResources().getString(R.string.mp_not_started_music_yet);
            } else {
                mediaName = track.getTitle() + " / " + track.getArtist();

                boolean isPlaying = track.isPlaying();
                mediaName = mediaName + " " + (isPlaying ? getResources().getString(R.string.mp_playing) : getResources().getString(R.string.mp_paused));
            }
        } else {
            mediaName = getResources().getString(R.string.mp_not_started_music_yet);
        }

        views.setTextViewText(R.id.notMusicName, mediaName);
        bigViews.setTextViewText(R.id.notMusicName, mediaName);

        views.setTextViewText(R.id.notTotalMsec, MediaUtils.getTimeWithFormatForMilliSecond(getMediaFileLengthInMilliseconds()));
        bigViews.setTextViewText(R.id.notTotalMsec, MediaUtils.getTimeWithFormatForMilliSecond(getMediaFileLengthInMilliseconds()));

        // Create notification builder.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_MEDIA_PLAYER_SERVICE_CHANNEL_ID);

        // Set Icon
        builder.setSmallIcon(R.mipmap.logo);
        builder.setTicker(getString(R.string.mp_app_name));
        builder.setContentTitle(getString(R.string.mp_app_name));
        builder.setCustomContentView(views);
        builder.setCustomBigContentView(bigViews);
        // Make the notification max priority.
        builder.setPriority(Notification.PRIORITY_MAX);
        // Make head-up notification.
        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        }

        // Add Play button intent in notification.
        /*Intent playIntent = new Intent(this, MediaPlayerService.class);
        playIntent.setAction(ACTION_PLAY);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);
        NotificationCompat.Action playAction = new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", pendingPlayIntent);
        builder.addAction(playAction);

        // Add Pause button intent in notification.
        Intent pauseIntent = new Intent(this, MediaPlayerService.class);
        pauseIntent.setAction(ACTION_PAUSE);
        PendingIntent pendingPrevIntent = PendingIntent.getService(this, 0, pauseIntent, 0);
        NotificationCompat.Action prevAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", pendingPrevIntent);
        builder.addAction(prevAction);*/

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_MEDIA_PLAYER_SERVICE_CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null, null);
            channel.enableLights(false);
            channel.enableVibration(false);
            if (notificationmanager != null) notificationmanager.createNotificationChannel(channel);
        }

        // Build the notification.
        Notification notification = builder.build();

        // Start foreground service.
        startForeground(1, notification);
    }

    private void stopForegroundService() {
        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }

    /**
     * Method which updates the SeekBar primary progress by current song playing position
     */
    private void progressUpdater() {

        if (isPlaying()) {
            progress = (int) (((float) getCurrentPosition() / getMediaFileLengthInMilliseconds()) * 100);

            for (MPIPlayer mPlayerListener : mPlayerListeners) {
                mPlayerListener.progressChanged(progress, getCurrentPosition(), getMediaFileLengthInMilliseconds());
            }

            Runnable runnable = this::progressUpdater;
            handler.postDelayed(runnable, 500);
        }
    }

    public int getMediaFileLengthInMilliseconds() {
        return mediaPlayerManager.getMediaFileLengthInMilliseconds();
    }

    public void seekTo(int playPositionInMillisecconds) {
        mediaPlayerManager.seekTo(playPositionInMillisecconds);
    }

    public void play() {
        if (track == null) return;

        track.setPlaying(true);

        if (newTrack) {
            setNewTrack();
        }

        mediaPlayerManager.play();

        if (newTrack) {
            SharedPrefUtils.addRecentTrack(this, track);
        }

        newTrack = false;

        progressUpdater();

        showNotification();

        for (MPIPlayer mPlayerListener : mPlayerListeners) {
            mPlayerListener.onStart(track);
        }
    }

    public void pause() {
        if (track == null) return;

        track.setPlaying(false);

        mediaPlayerManager.pause();

        newTrack = false;

        showNotification();

        for (MPIPlayer mPlayerListener : mPlayerListeners) {
            mPlayerListener.onPause(track);
        }
    }

    @Override
    public void onPrepared() {
        for (MPIPlayer mPlayerListener : mPlayerListeners) {
            mPlayerListener.onPrepared();
        }

        progressUpdater();

        showNotification();
    }

    @Override
    public boolean onError(int what, int extra) {
        for (MPIPlayer mPlayerListener : mPlayerListeners) {
            mPlayerListener.onError(what, extra);
        }

        return false;
    }

    public int getCurrentPosition() {
        return mediaPlayerManager.getCurrentPosition();
    }

    public void saveToCurrentQueue() {
        SharedPrefUtils.setPlayerCurrentTrackList(this, trackArrayList);

        SharedPrefUtils.setPlayerCurrentPosition(this, index);

        SharedPrefUtils.setTrackSeekbarPosition(this, progress);

        updateOriginalQueue();
    }

    private void updateOriginalQueue() {
        ArrayList<Long> currentIdList = SharedPrefUtils.getPlayerCurrentIdList(this);

        ArrayList<Long> originalIdList = SharedPrefUtils.getPlayerOriginalIdList(this);

        //Deleted Ids
        List<Long> deletedIdList = new ArrayList<>();
        for (long originalId : originalIdList) {
            if (!currentIdList.contains(originalId)) {
                deletedIdList.add(originalId);
            }
        }

        for (long deletedId : deletedIdList) {
            originalIdList.remove(deletedId);
        }

        //Added ids
        List<Long> addedIdList = new ArrayList<>();
        for (long currentId : currentIdList) {
            if (!originalIdList.contains(currentId)) {
                addedIdList.add(currentId);
            }
        }

        if (addedIdList.size() > 0) {
            originalIdList.addAll(addedIdList);
        }

        SharedPrefUtils.setPlayerOriginalIdList(this, originalIdList);
    }

    public void saveToOriginalQueue() {
        SharedPrefUtils.setPlayerOriginalTrackList(this, trackArrayList);
    }

    public void setShuffle(boolean isShuffled) {
        if (trackArrayList.size() == 0) return;

        if (isShuffled) {
            List<Track> shuffleTrackList = new ArrayList<>();

            Set<Integer> positions = new HashSet<>();
            int size = trackArrayList.size();
            for (int i = 0; i < trackArrayList.size(); i++) {
                int randomPosition = random.nextInt(size);
                while (positions.contains(randomPosition)) {
                    randomPosition = random.nextInt(size);
                }

                Track currentTrack = trackArrayList.get(randomPosition);
                positions.add(randomPosition);
                shuffleTrackList.add(currentTrack);
            }

            trackArrayList = shuffleTrackList;

            index = getIndex(track.getId());
        } else {
            List<Track> originalTrackList = new ArrayList<>();

            ArrayList<Long> originalIdList = SharedPrefUtils.getPlayerOriginalIdList(this);

            for (long originalId : originalIdList) {
                Track originalTrack = getTrack(originalId);
                if (originalTrack != null) {
                    originalTrackList.add(originalTrack);
                }
            }

            trackArrayList = originalTrackList;

            index = getIndex(track.getId());
        }
    }

    private Track getTrack(long id) {
        for (Track track2 : trackArrayList) {
            if (track2.getId() == id) {
                return track2;
            }
        }

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaPlayerManager.removePlayerListener(this);

        mediaPlayerManager.release();

        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(this);
        }

        if (equalizer != null) {
            equalizer.setControlStatusListener(null);
            equalizer.setEnableStatusListener(null);
            equalizer.setParameterListener(null);
            equalizer.setEnabled(false);
            equalizer.release();
            equalizer = null;
        }

        if (bassBoost != null) {
            bassBoost.setControlStatusListener(null);
            bassBoost.setEnableStatusListener(null);
            bassBoost.setParameterListener(null);
            bassBoost.setEnabled(false);
            bassBoost.release();
            bassBoost = null;
        }

        try {
            if (virtualizer != null) {
                virtualizer.setControlStatusListener(null);
                virtualizer.setEnableStatusListener(null);
                virtualizer.setParameterListener(null);
                virtualizer.setEnabled(false);
                virtualizer.release();
                virtualizer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setPauseForVisualizer();

        stopForegroundService();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange <= 0) {
            //LOSS -> PAUSE
            pauseMusicIfAudioFocus();
        } else {
            //GAIN -> PLAY
            resumeMusicIfAudioFocus();
        }
    }

    private void pauseMusicIfAudioFocus() {
        if (isPlaying()) {
            pausedForAudioFocus = true;
            pause();
        }
    }

    private void resumeMusicIfAudioFocus() {
        if (pausedForAudioFocus) {
            play();
            pausedForAudioFocus = false;
        }
    }

    public boolean isPlaying() {
        return mediaPlayerManager.isPlaying();
    }

    public Track getTrack() {
        return track;
    }

    public void playTrack(Track playTrack) {
        if (playTrack == null) return;

        if (isPlaying()) {
            pause();

            for (MPIPlayer mPlayerListener : mPlayerListeners) {
                mPlayerListener.onCompletion();

                showNotification();
            }
        }

        this.track = playTrack;
        newTrack = true;

        if (trackArrayList.size() == 0) {
            trackArrayList.add(track);

            index = 0;
        } else /*if(trackArrayList.size() > 0)*/ {
            if (trackArrayList.size() > index) {
                trackArrayList.add(index, track);

                removeDuplicate(index, track);

                index = getIndex(track.getId());
            }
        }

        play();
    }

    private int getIndex(long id) {
        int i = 0;
        for (Track track1 : trackArrayList) {
            if (track1.getId() == id) {
                return i;
            }

            i++;
        }

        return 0;
    }

    private void removeDuplicate(int currentIndex, Track currentTrack) {
        int duplicateTrackIndex = -1;
        int i = 0;
        for (Track curTrack : trackArrayList) {
            if (i != currentIndex && curTrack.getId() == currentTrack.getId()) {
                duplicateTrackIndex = i;
                break;
            }

            i++;
        }

        if (duplicateTrackIndex > -1) {
            trackArrayList.remove(duplicateTrackIndex);
        }
    }

    public boolean clear() {
        if (isPlaying()) {
            for (Track currentTrack : trackArrayList) {
                if (currentTrack.getId() == track.getId()) {
                    pause();

                    for (MPIPlayer mPlayerListener : mPlayerListeners) {
                        mPlayerListener.onCompletion();

                        showNotification();
                    }
                }
            }
        }

        index = 0;

        trackArrayList.clear();

        track = null;

        return true;
    }

    public Track getNextTrack() {
        int size = trackArrayList.size();

        if (size == 0) return null;

        if ((index + 1) < size) {
            index++;
        } else return null;

        if (index >= size) return null;

        return trackArrayList.get(index);
    }

    public Track getPreviousTrack() {
        int size = trackArrayList.size();

        if (size == 0) return null;

        if (index > 0) {
            index--;
        } else return null;

        if (index >= size) return null;

        return trackArrayList.get(index);
    }

    public void playNext(Track track) {
        if (track == null) return;

        int size = trackArrayList.size();

        if (trackArrayList.size() == 0) {
            trackArrayList.add(track);
        } else if ((index + 1) == size) {
            trackArrayList.add(track);

            removeDuplicate(index + 1, track);
        } else if ((index + 1) < size) {
            trackArrayList.add(index + 1, track);

            removeDuplicate(index + 1, track);
        }
    }

    public void addToQueue(Track addTrack) {
        if (addTrack == null) return;

        if (exist(addTrack)) return;

        if (track == null) {
            index = 0;
            track = addTrack;
            newTrack = true;
        }

        trackArrayList.add(addTrack);
    }

    private boolean exist(Track currrentTrack) {
        for (Track track1 : trackArrayList) {
            if (track1.getId() == currrentTrack.getId()) {
                return true;
            }
        }

        return false;
    }

    public void addToQueue(List<Track> trackList) {
        for (Track addTrack : trackList) {
            if (!exist(addTrack)) trackArrayList.add(addTrack);
        }

        if (track == null && trackArrayList.size() > 0) {
            index = 0;
            track = trackArrayList.get(0);
            newTrack = true;
        }
    }

    public List<Track> getTrackArrayList() {
        return trackArrayList;
    }

    public void setTrackArrayList(List<Track> trackList) {
        trackArrayList = trackList;
    }

    public void playPrevious() {
        Constants.REPLAY_TYPE replay_type = SharedPrefUtils.getReplay(this);

        if (replay_type == Constants.REPLAY_TYPE.REPLAY_ONE) {
            newTrack = true;
            play();

            return;
        }

        Track previousTrack = getPreviousTrack();

        if (previousTrack != null) {
            if (track != null) track.setPlaying(false);

            track = previousTrack;
            newTrack = true;

            play();
        } else {
            newTrack = true;
            play();
        }
    }

    public void playNext() {
        Constants.REPLAY_TYPE replay_type = SharedPrefUtils.getReplay(this);

        if (replay_type == Constants.REPLAY_TYPE.REPLAY_ONE) return;

        Track nextTrack = getNextTrack();

        if (nextTrack != null) {
            if (track != null) track.setPlaying(false);

            track = nextTrack;
            newTrack = true;

            play();
        } else {
            if (replay_type == Constants.REPLAY_TYPE.REPLAY_ALL) {
                if (trackArrayList.size() > 0) {
                    if (track != null) track.setPlaying(false);

                    index = 0;
                    track = trackArrayList.get(0);
                    newTrack = true;

                    play();
                }
            }
        }
    }

    public void playCurrent() {
        int size = trackArrayList.size();

        if (index >= size) return;

        track = trackArrayList.get(index);
        newTrack = true;

        play();
    }

    public boolean removeTrack(Track removeTrack) {
        boolean found = false;
        boolean paused = false;

        for (Track currentTrack : trackArrayList) {
            if (currentTrack.getId() == removeTrack.getId()) {
                found = true;
                removeTrack = currentTrack;

                //If playing track is deleted then pause and complete

                if (isPlaying() && track.getId() == removeTrack.getId()) {
                    paused = true;

                    pause();

                    for (MPIPlayer mPlayerListener : mPlayerListeners) {
                        mPlayerListener.onCompletion();

                        showNotification();
                    }
                }

                break;
            }
        }

        if (found) {
            trackArrayList.remove(removeTrack);

            if (trackArrayList.size() == 0) {
                index = 0;
                track = null;
            } else {
                if (index >= trackArrayList.size()) {
                    index--;
                    track = trackArrayList.get(index);
                    newTrack = true;
                } else {
                    if (track.getId() == removeTrack.getId()) {
                        track = trackArrayList.get(index);
                        newTrack = true;
                    } else {
                        index = getIndex(track.getId());
                    }
                }

                if (paused) play();
            }

            return true;
        }

        return false;
    }

    public void play(int position) {
        if (trackArrayList == null || trackArrayList.size() == 0) return;

        if (position >= trackArrayList.size()) return;

        if (isPlaying()) {
            pause();

            for (MPIPlayer mPlayerListener : mPlayerListeners) {
                mPlayerListener.onCompletion();

                showNotification();
            }
        }

        index = position;
        track = trackArrayList.get(index);
        newTrack = true;

        play();
    }

    public int getAudioSession() {
        return mediaPlayerManager.getAudioSession();
    }

    public boolean moveToFirst(int position) {
        if (position < 1) return false;
        if (trackArrayList == null || trackArrayList.size() <= 1) return false;

        Track currentTrack = trackArrayList.get(position);
        if (currentTrack == null) return false;

        trackArrayList.remove(position);
        trackArrayList.add(0, currentTrack);

        index = getIndex(track.getId());

        return true;
    }

    public boolean moveToLast(int position) {
        if (position < 0) return false;
        if (trackArrayList == null || trackArrayList.size() <= 1) return false;
        if ((position + 1) == trackArrayList.size()) return false;

        Track currentTrack = trackArrayList.get(position);
        if (currentTrack == null) return false;

        trackArrayList.remove(position);
        trackArrayList.add(currentTrack);

        index = getIndex(track.getId());

        return true;
    }

    public boolean moveUp(int position) {
        if (position < 1) return false;
        if (trackArrayList == null || trackArrayList.size() <= 1) return false;

        Track currentTrack = trackArrayList.get(position);
        if (currentTrack == null) return false;

        Track previousTrack = trackArrayList.get(position - 1);

        trackArrayList.set(position - 1, currentTrack);
        trackArrayList.set(position, previousTrack);

        index = getIndex(track.getId());

        return true;
    }

    public boolean moveDown(int position) {
        if (position < 0) return false;
        if (trackArrayList == null || trackArrayList.size() <= 1) return false;
        if ((position + 1) == trackArrayList.size()) return false;

        Track currentTrack = trackArrayList.get(position);
        if (currentTrack == null) return false;

        Track nextTrack = trackArrayList.get(position + 1);

        trackArrayList.set(position, nextTrack);
        trackArrayList.set(position + 1, currentTrack);

        index = getIndex(track.getId());

        return true;
    }

    public int getIndex() {
        return index;
    }

    public void setPauseForVisualizer() {
        if (visualizer != null) {
            try {
                visualizer.setEnabled(false);
            } catch (IllegalStateException unused) {
                unused.printStackTrace();
            }

            visualizer.release();
            visualizer = null;
        }
    }

    public void createVisualizer() {
        if (visualizer == null) {
            try {
                visualizer = new Visualizer(mediaPlayerManager.getAudioSession());

                visualizer.setEnabled(false);

                visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

                visualizer.setEnabled(true);
            } catch (IllegalStateException | UnsupportedOperationException unused) {
                unused.printStackTrace();
            }
        }
    }

    public int getLevelFromVisualizer() {
        int level = 0;

        if (mAudioManager != null && mAudioManager.isMusicActive() && visualizer != null && visualizer.getEnabled()) {
            int initProgress = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (initProgress == 0) {
                return 0;
            }

            int[] captureSizeRange = Visualizer.getCaptureSizeRange();
            int captureSize = captureSizeRange[0];
            if (captureSizeRange[1] > captureSize) captureSize = captureSizeRange[1];

            byte[] waveFormBytes = new byte[captureSize];
            byte[] fftBytes = new byte[captureSize];

            try {
                int resultWaweForm = visualizer.getWaveForm(waveFormBytes);
                if (resultWaweForm == 0) {
                    int[] waveFormInts = new int[waveFormBytes.length];
                    for (int i = 0; i < waveFormBytes.length; i++) {
                        waveFormInts[i] = (waveFormBytes[i] & 0xFF) - 128;
                    }

                    int maxWaveForm = 0;
                    for (int i = 0; i < 50; i++) {
                        if (Math.abs(waveFormInts[i]) > maxWaveForm) {
                            maxWaveForm = Math.abs(waveFormInts[i]);
                        }
                    }

                    level = (int) ((maxWaveForm * 1.0f) * (16.0f / 128.0f));
                } else {
                    int resultFftForm = visualizer.getFft(fftBytes);
                    if (resultFftForm == 0) {
                        int[] fftInts = new int[fftBytes.length];
                        for (int i = 0; i < fftBytes.length; i++) {
                            fftInts[i] = fftBytes[i];
                        }

                        int maxFftForm = 0;
                        for (int i = 0; i < 50; i++) {
                            if (Math.abs(fftInts[i]) > maxFftForm) {
                                maxFftForm = Math.abs(fftInts[i]);
                            }
                        }

                        level = (int) ((maxFftForm * 1.0f) * (16.0f / 128.0f));
                    } else {

                    }
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } else {

        }

        return level;
    }

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }
}

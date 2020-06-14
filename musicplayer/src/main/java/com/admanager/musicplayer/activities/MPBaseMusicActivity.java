package com.admanager.musicplayer.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.admanager.musicplayer.R;
import com.admanager.musicplayer.listeners.MPIPlayer;
import com.admanager.musicplayer.models.Genre;
import com.admanager.musicplayer.models.Playlist;
import com.admanager.musicplayer.models.Preset;
import com.admanager.musicplayer.models.Track;
import com.admanager.musicplayer.services.MediaPlayerService;
import com.admanager.musicplayer.tasks.GetTracks;
import com.admanager.musicplayer.utilities.Constants;
import com.admanager.musicplayer.utilities.ContextUtils;
import com.admanager.musicplayer.utilities.SharedPrefUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class MPBaseMusicActivity extends MPBaseActivity implements MPIPlayer {
    private static final int MUSIC_ACTIVITY_REQUEST_CODE = 1;

    // To invoke the bound service, first make sure that this value
    // is not null.
    protected MediaPlayerService mediaPlayerService;
    // Don't attempt to unbind from the service unless the client has received some
    // information about the service's state.
    protected boolean mShouldUnbind;

    protected ImageView maPlayImage;

    protected ImageView maTrackImage;
    protected TextView maTitle, maArtist;

    protected Uri actionUri = null;
    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mediaPlayerService = ((MediaPlayerService.LocalBinder) service).getService();

            mediaPlayerService.addPlayerListener(MPBaseMusicActivity.this);

            controlTrack();

            loadEqualizerBassBoost();

            // Tell the user about this for our demo.
            //Toast.makeText(Binding.this, R.string.local_service_connected, Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mediaPlayerService = null;
            //Toast.makeText(Binding.this, R.string.local_service_disconnected,Toast.LENGTH_SHORT).show();
        }
    };

    public void playNext(Track track) {
        if (track == null) return;

        if (mediaPlayerService == null) return;

        mediaPlayerService.playNext(track);

        if (ContextUtils.isContextValid(this)) {
            Toast toast = Toast.makeText(this, getString(R.string.mp_will_be_played_next, track.getTitle()), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void addToQueue(Track track) {
        if (track == null) return;

        if (mediaPlayerService == null) return;

        mediaPlayerService.addToQueue(track);

        if (ContextUtils.isContextValid(this)) {
            Toast toast = Toast.makeText(this, getString(R.string.mp_is_added_to_queue, track.getTitle()), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //Artist Tracks
    public void addArtistTracksToQueue(Track artistTrack) {
        if (artistTrack == null) return;

        if (mediaPlayerService == null) return;

        List<Track> trackList = GetTracks.getAllTracksForArtist(this, artistTrack.getArtistId());

        mediaPlayerService.addToQueue(trackList);

        if (ContextUtils.isContextValid(this)) {
            Toast toast = Toast.makeText(this, getString(R.string.mp_artist_tracks_are_added, artistTrack.getArtist()), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void playArtistTracks(Track artistTrack) {
        if (artistTrack == null) return;

        if (mediaPlayerService == null) return;

        List<Track> trackList = GetTracks.getAllTracksForArtist(this, artistTrack.getArtistId());

        mediaPlayerService.clear();

        mediaPlayerService.addToQueue(trackList);

        mediaPlayerService.playCurrent();
    }

    //Album Tracks
    public void playAlbumTracks(Track albumTrack) {
        if (albumTrack == null) return;

        if (mediaPlayerService == null) return;

        List<Track> trackList = GetTracks.getAllTracksForAlbum(this, albumTrack.getAlbumId());

        mediaPlayerService.clear();

        mediaPlayerService.addToQueue(trackList);

        mediaPlayerService.playCurrent();
    }

    public void addAlbumTracksToQueue(Track albumTrack) {
        if (albumTrack == null) return;

        if (mediaPlayerService == null) return;

        List<Track> trackList = GetTracks.getAllTracksForAlbum(this, albumTrack.getAlbumId());

        mediaPlayerService.addToQueue(trackList);

        if (ContextUtils.isContextValid(this)) {
            Toast toast = Toast.makeText(this, getString(R.string.mp_album_tracks_are_added, albumTrack.getAlbum()), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //Genre Tracks
    public void playGenreTracks(Genre genre) {
        if (genre == null) return;

        if (mediaPlayerService == null) return;

        List<Track> trackList = GetTracks.getAllTracksForGenre(this, genre.getAudioIds());

        mediaPlayerService.clear();

        mediaPlayerService.addToQueue(trackList);

        mediaPlayerService.playCurrent();
    }

    public void addGenreTracksToQueue(Genre genre) {
        if (genre == null) return;

        if (mediaPlayerService == null) return;

        List<Track> trackList = GetTracks.getAllTracksForGenre(this, genre.getAudioIds());

        mediaPlayerService.addToQueue(trackList);

        if (ContextUtils.isContextValid(this)) {
            Toast toast = Toast.makeText(this, getString(R.string.mp_genre_tracks_are_added, genre.getName()), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //Playlist Tracks
    public void playPlaylistTracks(Playlist playlist) {
        if (playlist == null) return;

        if (mediaPlayerService == null) return;

        List<Long> idList = playlist.getTrackIds();
        List<Track> trackList = GetTracks.getTrackList(this, idList);

        mediaPlayerService.clear();

        mediaPlayerService.addToQueue(trackList);

        mediaPlayerService.playCurrent();
    }

    public void addPlaylistTracksToQueue(Playlist playlist) {
        if (playlist == null) return;

        if (mediaPlayerService == null) return;

        List<Long> idList = playlist.getTrackIds();
        List<Track> trackList = GetTracks.getTrackList(this, idList);

        mediaPlayerService.addToQueue(trackList);

        if (ContextUtils.isContextValid(this)) {
            Toast toast = Toast.makeText(this, getString(R.string.mp_playlist_tracks_are_added, playlist.getName()), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void playOrPause() {
        if (mediaPlayerService == null) return;

        if (mediaPlayerService.isPlaying()) {
            mediaPlayerService.pause();

            maPlayImage.setImageResource(R.drawable.mp_ic_sp_play);
        } else {
            if (mediaPlayerService.getTrack() != null) {
                mediaPlayerService.play();

                maPlayImage.setImageResource(R.drawable.mp_ic_sp_stop);
            }
        }
    }

    protected void playPrevious() {
        if (mediaPlayerService == null) return;

        mediaPlayerService.playPrevious();

        controlTrack();
    }

    protected void playNext() {
        if (mediaPlayerService == null) return;

        mediaPlayerService.playNext();

        controlTrack();
    }

    protected void finishActivityWithResult() {
        Intent intent = new Intent();

        setResult(RESULT_OK, intent);

        finish();
    }

    protected void controlTrack() {
        if (mediaPlayerService == null) return;

        if (SharedPrefUtils.getFirstActionView(this)) {
            SharedPrefUtils.setFirstActionView(this, false);

            Track track = GetTracks.getTrack(this, actionUri);

            if (track != null && playTrack(track)) {
                SharedPrefUtils.setFirstActionView(this, false);

                gotoMusicActivity();
            }

            return;
        }

        if (mediaPlayerService.isPlaying()) {
            maPlayImage.setImageResource(R.drawable.mp_ic_sp_stop);
        } else {
            maPlayImage.setImageResource(R.drawable.mp_ic_sp_play);
        }

        Track track = mediaPlayerService.getTrack();

        if (track != null) {
            setTrackInformation(track);
        }
    }

    protected void gotoMusicActivityWithAction() {
        Intent intent = new Intent(this, MPMusicActivity.class);
        intent.putExtra(Constants.MUSIC_TABS_NAME, Constants.MUSIC_TABS.MUSIC);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivityForResult(intent, MUSIC_ACTIVITY_REQUEST_CODE);
    }

    protected void gotoMusicActivity() {
        Intent intent = new Intent(this, MPMusicActivity.class);
        intent.putExtra(Constants.MUSIC_TABS_NAME, Constants.MUSIC_TABS.MUSIC);

        startActivityForResult(intent, MUSIC_ACTIVITY_REQUEST_CODE);
    }

    public void gotoMusicActivity(Constants.MUSIC_TABS music_tabs) {
        Intent intent = new Intent(this, MPMusicActivity.class);
        intent.putExtra(Constants.MUSIC_TABS_NAME, music_tabs);

        startActivityForResult(intent, MUSIC_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MUSIC_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            controlTrack();
        }
    }

    public boolean playTrack(Track track) {
        if (mediaPlayerService == null || track == null) return false;

        mediaPlayerService.playTrack(track);

        maPlayImage.setImageResource(R.drawable.mp_ic_sp_stop);

        setTrackInformation(track);

        return true;
    }

    protected void doBindService() {
        // Attempts to establish a connection with the service.  We use an
        // explicit class name because we want a specific service
        // implementation that we know will be running in our own process
        // (and thus won't be supporting component replacement by other
        // applications).
        if (bindService(new Intent(this, MediaPlayerService.class),
                mConnection, Context.BIND_AUTO_CREATE)) {
            mShouldUnbind = true;

        } else {
            //Log.e("MY_APP_TAG", "Error: The requested service doesn't " +"exist, or this client isn't allowed access to it.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        doUnbindService();

        if (mediaPlayerService != null) {
            mediaPlayerService.removePlayerListener(this);

            mediaPlayerService = null;
        }
    }

    private void doUnbindService() {
        if (mShouldUnbind) {
            // Release information about the service's state.
            unbindService(mConnection);
            mShouldUnbind = false;
        }
    }

    @Override
    public void progressChanged(int progress, long currentDuration, long totalDuration) {

    }

    @Override
    public void onStart(Track track) {
        setTrackInformation(track);

        maPlayImage.setImageResource(R.drawable.mp_ic_sp_stop);
    }

    private void setTrackInformation(Track track) {
        String imagePath = GetTracks.getAlbumImagePath(this, track.getAlbumId());
        if (!TextUtils.isEmpty(imagePath)) {
            RequestOptions options = new RequestOptions();
            options = options.diskCacheStrategy(DiskCacheStrategy.ALL);
            options = options.centerCrop();
            options = options.dontAnimate();

            Glide.with(this)
                    .load(imagePath)
                    .apply(options)
                    .into(maTrackImage);
        } else {
            maTrackImage.setImageResource(R.drawable.mp_ic_music);
        }

        maTitle.setText(track.getTitle());

        maArtist.setText(track.getArtist());
    }

    @Override
    public void onPause(Track track) {
        maPlayImage.setImageResource(R.drawable.mp_ic_sp_play);
    }

    @Override
    public void onCompletion() {
        maPlayImage.setImageResource(R.drawable.mp_ic_sp_play);
    }

    @Override
    public void onBufferingUpdate(int percent) {

    }

    @Override
    public void onPrepared() {

    }

    @Override
    public boolean onError(int what, int extra) {
        return false;
    }

    @Override
    public void onBackPressed() {
        finishActivityWithResult();
    }

    private void loadEqualizerBassBoost() {
        if (mediaPlayerService == null) return;

        boolean firstSetEqualizer = SharedPrefUtils.getFirstSetEqualizer(this);

        if (!firstSetEqualizer) return;

        SharedPrefUtils.setFirstSetEqualizer(this, false);

        //Equalizer
        ArrayList<Preset> presetArrayList = SharedPrefUtils.getPresetArrayList(this);
        String presetName = SharedPrefUtils.getPresetName(this);
        int presetNumber = SharedPrefUtils.getPresetNumber(this);

        boolean foundPreset = false;
        Preset preset = null;
        for (Preset curPreset : presetArrayList) {
            if (curPreset.getPresetName().toLowerCase().equalsIgnoreCase(presetName.toLowerCase())) {
                foundPreset = true;
                preset = curPreset;
            }
        }

        if (foundPreset) {
            List<Short> bandLevels = preset.getBandLevels();
            for (short i = 0; i < bandLevels.size(); i++) {
                mediaPlayerService.equalizer.setBandLevel(i, bandLevels.get(i));
            }
        } else {
            mediaPlayerService.equalizer.usePreset((short) presetNumber);
        }

        //Bass Boost
        boolean bassBoostOpen = SharedPrefUtils.getBassBoostOpen(this);
        if (bassBoostOpen) {
            int bassBoostLevel = SharedPrefUtils.getBassBoostLevel(this);
            mediaPlayerService.setBassBoostStrength((short) (bassBoostLevel * 10));
        }
    }
}

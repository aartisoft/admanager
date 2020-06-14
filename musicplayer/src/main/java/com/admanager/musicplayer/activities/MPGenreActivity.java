package com.admanager.musicplayer.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.musicplayer.R;
import com.admanager.musicplayer.adapters.CustomTracks_2ListAdapter;
import com.admanager.musicplayer.models.Genre;
import com.admanager.musicplayer.models.Track;
import com.admanager.musicplayer.tasks.GetTracks;
import com.admanager.musicplayer.utilities.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class MPGenreActivity extends MPBaseMusicActivity {
    private Genre genre;

    private CustomTracks_2ListAdapter mfRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mp_activity_genre);

        doBindService();

        maPlayImage = findViewById(R.id.maPlayImage);

        maTrackImage = findViewById(R.id.maTrackImage);
        maTitle = findViewById(R.id.maTitle);
        maArtist = findViewById(R.id.maArtist);

        TextView gaTitleText = findViewById(R.id.gaTitleText);
        ImageView gaAlbumImage = findViewById(R.id.gaAlbumImage);

        findViewById(R.id.gaBackLayout).setOnClickListener(view -> finishActivityWithResult());
        findViewById(R.id.gaOptionLayout).setOnClickListener(this::showOptionsGenre);

        findViewById(R.id.maTrackImageLayout).setOnClickListener(view -> gotoMusicActivity(Constants.MUSIC_TABS.MUSIC));
        findViewById(R.id.maTitleArtistLayout).setOnClickListener(view -> gotoMusicActivity(Constants.MUSIC_TABS.MUSIC));
        findViewById(R.id.maEqualizerLayout).setOnClickListener(view -> gotoMusicActivity(Constants.MUSIC_TABS.MUSIC));
        findViewById(R.id.maPlayImageLayout).setOnClickListener(view -> playOrPause());
        findViewById(R.id.maPlayPreviousImageLayout).setOnClickListener(view -> playPrevious());
        findViewById(R.id.maPlayNextImageLayout).setOnClickListener(view -> playNext());

        Bundle data = getIntent().getBundleExtra(Constants.BUNDLE_NAME);
        if (data != null) {
            genre = data.getParcelable(Constants.GENRE_OBJECT);
            if (genre != null) {
                gaTitleText.setText(genre.getName());

                String imagePath = GetTracks.getAlbumImagePathForAudioId(this, genre.getAudioIds());
                if (!TextUtils.isEmpty(imagePath)) {
                    RequestOptions options = new RequestOptions();
                    options = options.diskCacheStrategy(DiskCacheStrategy.ALL);
                    options = options.centerCrop();
                    options = options.dontAnimate();

                    Glide.with(this)
                            .load(imagePath)
                            .apply(options)
                            .into(gaAlbumImage);
                }
            }
        }

        RecyclerView gaRecyclerViewList = findViewById(R.id.gaRecyclerViewMusic);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //faRecyclerViewList.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager gaRecyclerViewLayoutManager = new LinearLayoutManager(this);
        gaRecyclerViewList.setLayoutManager(gaRecyclerViewLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        gaRecyclerViewList.addItemDecoration(itemDecoration);

        // specify an adapter (see also next example)
        mfRecyclerViewAdapter = new CustomTracks_2ListAdapter(new ArrayList<>(), (view2, pos) -> {
            long viewId = view2.getId();

            if (viewId == R.id.mlTrackNoLayout) {
                playTrack(pos);
            } else if (viewId == R.id.mlTitleLayout) {
                playTrack(pos);
            } else if (viewId == R.id.mlTotalMsecLayout) {
                playTrack(pos);
            } else if (viewId == R.id.mlOptionLayout) {
                showOptions(pos, view2);
            } else {
                playTrack(pos);
            }
        });
        gaRecyclerViewList.setAdapter(mfRecyclerViewAdapter);

        loadTracks();
    }

    private void showOptionsGenre(View view) {
        //creating a popup menu
        PopupMenu popup = new PopupMenu(this, view);
        //inflating menu from xml resource
        popup.inflate(R.menu.album_menu_2);
        //adding click listener
        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.action_play_all) {
                playGenreTracks(genre);
            } else if (item.getItemId() == R.id.action_add_all) {
                addGenreTracksToQueue(genre);
            } else if (item.getItemId() == R.id.action_add_to_playlist) {
                addGenreTracksToPlaylist(genre);
            }

            return true;
        });
        //displaying the popup
        popup.show();
    }

    private void showOptions(int position, View view) {
        Track track = mfRecyclerViewAdapter.getItem(position);

        if (track == null) return;

        //creating a popup menu
        PopupMenu popup = new PopupMenu(this, view);
        //inflating menu from xml resource
        popup.inflate(R.menu.track_menu);
        //adding click listener
        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.action_play_now) {
                playTrack(position);
            } else if (item.getItemId() == R.id.action_add_to_playlist) {
                addToPlaylist(track);
            }

            return true;
        });
        //displaying the popup
        popup.show();
    }

    private void loadTracks() {
        if (genre != null) {
            AsyncTask.execute(this::loadTracksAsyncTask);
        }
    }

    private void loadTracksAsyncTask() {
        List<Track> trackList = GetTracks.getAllTracksForGenre(this, genre.getAudioIds());

        runOnUiThread(() -> {

            if (trackList != null && trackList.size() > 0) {
                mfRecyclerViewAdapter.setData(trackList);
            }
        });
    }

    private void playTrack(int pos) {
        Track track = mfRecyclerViewAdapter.getItem(pos);

        if (track == null) return;

        playTrack(track);

        gotoMusicActivity();
    }
}

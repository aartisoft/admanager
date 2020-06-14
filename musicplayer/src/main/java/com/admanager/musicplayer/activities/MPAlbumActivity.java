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
import com.admanager.musicplayer.models.Track;
import com.admanager.musicplayer.tasks.GetTracks;
import com.admanager.musicplayer.utilities.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class MPAlbumActivity extends MPBaseMusicActivity {
    private Track albumTrack;

    private CustomTracks_2ListAdapter mfRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mp_activity_album);

        doBindService();

        maPlayImage = findViewById(R.id.maPlayImage);

        maTrackImage = findViewById(R.id.maTrackImage);
        maTitle = findViewById(R.id.maTitle);
        maArtist = findViewById(R.id.maArtist);

        TextView alaTitleText = findViewById(R.id.alaTitleText);
        ImageView alaAlbumImage = findViewById(R.id.alaAlbumImage);

        findViewById(R.id.alaBackLayout).setOnClickListener(view -> finishActivityWithResult());
        findViewById(R.id.alaOptionLayout).setOnClickListener(this::showOptionsAlbum);

        findViewById(R.id.maTrackImageLayout).setOnClickListener(view2 -> gotoMusicActivity(Constants.MUSIC_TABS.MUSIC));
        findViewById(R.id.maTitleArtistLayout).setOnClickListener(view3 -> gotoMusicActivity(Constants.MUSIC_TABS.MUSIC));
        findViewById(R.id.maEqualizerLayout).setOnClickListener(view4 -> gotoMusicActivity(Constants.MUSIC_TABS.MUSIC));
        findViewById(R.id.maPlayImageLayout).setOnClickListener(view5 -> playOrPause());
        findViewById(R.id.maPlayPreviousImageLayout).setOnClickListener(view6 -> playPrevious());
        findViewById(R.id.maPlayNextImageLayout).setOnClickListener(view7 -> playNext());

        Bundle data = getIntent().getBundleExtra(Constants.BUNDLE_NAME);
        if (data != null) {
            albumTrack = data.getParcelable(Constants.TRACK_OBJECT);
            if (albumTrack != null) {
                alaTitleText.setText(albumTrack.getAlbum());

                String imagePath = GetTracks.getAlbumImagePath(this, albumTrack.getAlbumId());
                if (!TextUtils.isEmpty(imagePath)) {
                    RequestOptions options = new RequestOptions();
                    options = options.diskCacheStrategy(DiskCacheStrategy.ALL);
                    options = options.centerCrop();
                    options = options.dontAnimate();

                    Glide.with(this)
                            .load(imagePath)
                            .apply(options)
                            .into(alaAlbumImage);
                }
            }
        }

        RecyclerView alaRecyclerViewList = findViewById(R.id.alaRecyclerViewMusic);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //faRecyclerViewList.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager aaRecyclerViewLayoutManager = new LinearLayoutManager(this);
        alaRecyclerViewList.setLayoutManager(aaRecyclerViewLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        alaRecyclerViewList.addItemDecoration(itemDecoration);

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
        alaRecyclerViewList.setAdapter(mfRecyclerViewAdapter);

        loadTracks();
    }

    private void showOptionsAlbum(View view) {
        //creating a popup menu
        PopupMenu popup = new PopupMenu(this, view);
        //inflating menu from xml resource
        popup.inflate(R.menu.album_menu_2);
        //adding click listener
        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.action_play_all) {
                playAlbumTracks(albumTrack);
            } else if (item.getItemId() == R.id.action_add_all) {
                addAlbumTracksToQueue(albumTrack);
            } else if (item.getItemId() == R.id.action_add_to_playlist) {
                addAlbumTracksToPlaylist(albumTrack);
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
        if (albumTrack != null) {
            AsyncTask.execute(this::loadTracksAsyncTask);
        }
    }

    private void loadTracksAsyncTask() {
        List<Track> trackList = GetTracks.getAllTracksForAlbum(this, albumTrack.getAlbumId());

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

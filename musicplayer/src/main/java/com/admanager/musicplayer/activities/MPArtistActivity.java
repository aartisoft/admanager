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

public class MPArtistActivity extends MPBaseMusicActivity {
    private Track artistTrack;

    private CustomTracks_2ListAdapter mfRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mp_activity_artist);

        doBindService();

        maPlayImage = findViewById(R.id.maPlayImage);

        maTrackImage = findViewById(R.id.maTrackImage);
        maTitle = findViewById(R.id.maTitle);
        maArtist = findViewById(R.id.maArtist);

        TextView araTitleText = findViewById(R.id.araTitleText);
        ImageView araAlbumImage = findViewById(R.id.araAlbumImage);

        findViewById(R.id.araBackLayout).setOnClickListener(view -> finishActivityWithResult());
        findViewById(R.id.araOptionLayout).setOnClickListener(this::showOptionsArtist);

        findViewById(R.id.maTrackImageLayout).setOnClickListener(view2 -> gotoMusicActivity(Constants.MUSIC_TABS.MUSIC));
        findViewById(R.id.maTitleArtistLayout).setOnClickListener(view3 -> gotoMusicActivity(Constants.MUSIC_TABS.MUSIC));
        findViewById(R.id.maEqualizerLayout).setOnClickListener(view4 -> gotoMusicActivity(Constants.MUSIC_TABS.MUSIC));
        findViewById(R.id.maPlayImageLayout).setOnClickListener(view5 -> playOrPause());
        findViewById(R.id.maPlayPreviousImageLayout).setOnClickListener(view6 -> playPrevious());
        findViewById(R.id.maPlayNextImageLayout).setOnClickListener(view7 -> playNext());

        Bundle data = getIntent().getBundleExtra(Constants.BUNDLE_NAME);
        if (data != null) {
            artistTrack = data.getParcelable(Constants.TRACK_OBJECT);
            if (artistTrack != null) {
                araTitleText.setText(artistTrack.getArtist());

                String imagePath = GetTracks.getAlbumImagePath(this, artistTrack.getAlbumId());
                if (!TextUtils.isEmpty(imagePath)) {
                    RequestOptions options = new RequestOptions();
                    options = options.diskCacheStrategy(DiskCacheStrategy.ALL);
                    options = options.centerCrop();
                    options = options.dontAnimate();

                    Glide.with(this)
                            .load(imagePath)
                            .apply(options)
                            .into(araAlbumImage);
                }
            }
        }

        RecyclerView araRecyclerViewList = findViewById(R.id.araRecyclerViewMusic);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //faRecyclerViewList.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager faRecyclerViewLayoutManager = new LinearLayoutManager(this);
        araRecyclerViewList.setLayoutManager(faRecyclerViewLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        araRecyclerViewList.addItemDecoration(itemDecoration);

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
        araRecyclerViewList.setAdapter(mfRecyclerViewAdapter);

        loadTracks();
    }

    private void showOptionsArtist(View view) {
        //creating a popup menu
        PopupMenu popup = new PopupMenu(this, view);
        //inflating menu from xml resource
        popup.inflate(R.menu.album_menu_2);
        //adding click listener
        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.action_play_all) {
                playArtistTracks(artistTrack);
            } else if (item.getItemId() == R.id.action_add_all) {
                addArtistTracksToQueue(artistTrack);
            } else if (item.getItemId() == R.id.action_add_to_playlist) {
                addArtistTracksToPlaylist(artistTrack);
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
        if (artistTrack != null) {
            AsyncTask.execute(this::loadTracksAsyncTask);
        }
    }

    private void loadTracksAsyncTask() {
        List<Track> trackList = GetTracks.getAllTracksForArtist(this, artistTrack.getArtistId());

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

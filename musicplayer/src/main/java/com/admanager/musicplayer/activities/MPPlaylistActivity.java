package com.admanager.musicplayer.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.musicplayer.R;
import com.admanager.musicplayer.adapters.CustomTracks_2ListAdapter;
import com.admanager.musicplayer.models.Playlist;
import com.admanager.musicplayer.models.Track;
import com.admanager.musicplayer.tasks.GetTracks;
import com.admanager.musicplayer.utilities.Constants;
import com.admanager.musicplayer.utilities.ContextUtils;
import com.admanager.musicplayer.utilities.SharedPrefUtils;

import java.util.ArrayList;
import java.util.List;

public class MPPlaylistActivity extends MPBaseMusicActivity {
    private Playlist playlist;

    private CustomTracks_2ListAdapter mfRecyclerViewAdapter;

    private TextView paTitleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mp_activity_playlist);

        doBindService();

        maPlayImage = findViewById(R.id.maPlayImage);

        maTrackImage = findViewById(R.id.maTrackImage);
        maTitle = findViewById(R.id.maTitle);
        maArtist = findViewById(R.id.maArtist);

        paTitleText = findViewById(R.id.paTitleText);

        findViewById(R.id.paBackLayout).setOnClickListener(view -> finishActivityWithResult());
        findViewById(R.id.paOptionLayout).setOnClickListener(this::showOptionsPlaylist);

        findViewById(R.id.maTrackImageLayout).setOnClickListener(view2 -> gotoMusicActivity(Constants.MUSIC_TABS.MUSIC));
        findViewById(R.id.maTitleArtistLayout).setOnClickListener(view3 -> gotoMusicActivity(Constants.MUSIC_TABS.MUSIC));
        findViewById(R.id.maEqualizerLayout).setOnClickListener(view4 -> gotoMusicActivity(Constants.MUSIC_TABS.MUSIC));
        findViewById(R.id.maPlayImageLayout).setOnClickListener(view5 -> playOrPause());
        findViewById(R.id.maPlayPreviousImageLayout).setOnClickListener(view6 -> playPrevious());
        findViewById(R.id.maPlayNextImageLayout).setOnClickListener(view7 -> playNext());

        Bundle data = getIntent().getBundleExtra(Constants.BUNDLE_NAME);
        if (data != null) {
            playlist = data.getParcelable(Constants.PLAYLIST_OBJECT);
            if (playlist != null) {
                paTitleText.setText(playlist.getName());
            }
        }

        RecyclerView faRecyclerViewList = findViewById(R.id.paRecyclerViewMusic);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //faRecyclerViewList.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager faRecyclerViewLayoutManager = new LinearLayoutManager(this);
        faRecyclerViewList.setLayoutManager(faRecyclerViewLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        faRecyclerViewList.addItemDecoration(itemDecoration);

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
        faRecyclerViewList.setAdapter(mfRecyclerViewAdapter);

        loadTracks();
    }

    private void showOptionsPlaylist(View view) {
        //creating a popup menu
        PopupMenu popup = new PopupMenu(this, view);
        //inflating menu from xml resource
        popup.inflate(R.menu.playlist_menu_2);
        //adding click listener
        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.action_play_all) {
                playPlaylistTracks(playlist);
            } else if (item.getItemId() == R.id.action_add_all) {
                addPlaylistTracksToQueue(playlist);
            } else if (item.getItemId() == R.id.action_edit) {
                editPlaylistAction(playlist);
            } else if (item.getItemId() == R.id.action_delete_playlist) {
                deletePlaylistAction(playlist);
            }

            return true;
        });
        //displaying the popup
        popup.show();
    }

    private void editPlaylistAction(Playlist playlist) {
        if (ContextUtils.isContextValid(this) && playlist != null) {
            String oldPlaylistName = playlist.getName();
            final EditText taskEditText = new EditText(this);
            taskEditText.setText(playlist.getName());
            taskEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            taskEditText.requestFocus();

            android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.mp_rename_playlist))
                    .setMessage(getResources().getString(R.string.mp_enter_new_playlist_name))
                    .setView(taskEditText)
                    .setPositiveButton(getResources().getString(R.string.mp_update), (dialog1, which) ->
                    {
                        String newPlaylistName = String.valueOf(taskEditText.getText());

                        editPlaylistActionYes(oldPlaylistName, newPlaylistName);
                    })
                    .setNegativeButton(getResources().getString(R.string.mp_cancel), null)
                    .create();
            dialog.show();
        }
    }

    private void editPlaylistActionYes(String oldPlaylistName, String newPlaylistName) {
        boolean edited = false;

        ArrayList<Playlist> playlistArrayList = SharedPrefUtils.getPlaylistArrayList(this);
        for (Playlist playlist1 : playlistArrayList) {
            if (oldPlaylistName.toLowerCase().equalsIgnoreCase(playlist1.getName().toLowerCase())) {
                playlist1.setName(newPlaylistName);

                SharedPrefUtils.setPlaylistArrayList(this, playlistArrayList);

                playlist.setName(newPlaylistName);
                paTitleText.setText(newPlaylistName);

                edited = true;

                break;
            }
        }

        if (edited) {
            if (ContextUtils.isContextValid(this)) {
                Toast toast = Toast.makeText(this, getString(R.string.mp_playlist_updated), Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            if (ContextUtils.isContextValid(this)) {
                Toast toast = Toast.makeText(this, getString(R.string.mp_playlist_not_updated), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void deletePlaylistAction(Playlist playlist) {
        if (ContextUtils.isContextValid(this) && playlist != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(getResources().getString(R.string.mp_delete_playlist));
            builder.setMessage(getResources().getString(R.string.mp_are_you_sure_delete_playlist, playlist.getName()));

            builder.setPositiveButton(getResources().getString(R.string.mp_yes), (dialog, which) -> deletePlaylistActionYes(playlist));

            builder.setNegativeButton(getResources().getString(R.string.mp_no), (dialog, which) -> {

                if (dialog != null && ContextUtils.isContextValid(this)) {
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void deletePlaylistActionYes(Playlist playlist) {
        if (deletePlaylist(playlist)) {
            if (ContextUtils.isContextValid(this) && playlist != null) {
                Toast toast = Toast.makeText(this, getString(R.string.mp_playlist_deleted, playlist.getName()), Toast.LENGTH_SHORT);
                toast.show();

                finishActivityWithResult();
            }
        }
    }

    private void showOptions(int position, View view) {
        Track track = mfRecyclerViewAdapter.getItem(position);

        if (track == null) return;

        //creating a popup menu
        PopupMenu popup = new PopupMenu(this, view);
        //inflating menu from xml resource
        popup.inflate(R.menu.playlist_track_menu);
        //adding click listener
        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.action_play_now) {
                playTrack(position);
            } else if (item.getItemId() == R.id.action_move_to_first) {
                moveToFirst(position, track);
            } else if (item.getItemId() == R.id.action_move_to_last) {
                moveToLast(position, track);
            } else if (item.getItemId() == R.id.action_move_up) {
                moveUp(position, track);
            } else if (item.getItemId() == R.id.action_move_down) {
                moveDown(position, track);
            } else if (item.getItemId() == R.id.action_remove_from_playlist) {
                removeFromPlaylist(position, track);
            }

            return true;
        });
        //displaying the popup
        popup.show();
    }

    private void savePlaylist() {
        ArrayList<Playlist> playlistArrayList = SharedPrefUtils.getPlaylistArrayList(this);
        for (Playlist playlist1 : playlistArrayList) {
            if (playlist.getName().toLowerCase().equalsIgnoreCase(playlist1.getName().toLowerCase())) {
                List<Long> idLists = new ArrayList<>();
                for (int i = 0; i < mfRecyclerViewAdapter.getItemCount(); i++) {
                    Track track = mfRecyclerViewAdapter.getItem(i);
                    idLists.add(track.getId());
                }

                playlist1.setTrackIds(idLists);
                playlist.setTrackIds(idLists);

                SharedPrefUtils.setPlaylistArrayList(this, playlistArrayList);
            }
        }
    }

    private void moveToFirst(int position, Track track) {
        if (position < 1) return;
        if (track == null) return;

        if (mfRecyclerViewAdapter.getItemCount() <= 1) return;

        mfRecyclerViewAdapter.remove(position);
        mfRecyclerViewAdapter.insert(0, track);

        savePlaylist();
    }

    private void moveToLast(int position, Track track) {
        if (position < 0) return;
        if (track == null) return;

        if (mfRecyclerViewAdapter.getItemCount() <= 1) return;
        if ((position + 1) == mfRecyclerViewAdapter.getItemCount()) return;

        mfRecyclerViewAdapter.remove(position);
        mfRecyclerViewAdapter.add(track);
        mfRecyclerViewAdapter.notifyDataSetChanged();

        savePlaylist();
    }

    private void moveUp(int position, Track track) {
        if (position < 1) return;
        if (track == null) return;

        if (mfRecyclerViewAdapter.getItemCount() <= 1) return;

        Track previousTrack = mfRecyclerViewAdapter.getItem(position - 1);

        mfRecyclerViewAdapter.update(position - 1, track);
        mfRecyclerViewAdapter.update(position, previousTrack);

        savePlaylist();
    }

    private void moveDown(int position, Track track) {
        if (position < 0) return;
        if (track == null) return;

        if (mfRecyclerViewAdapter.getItemCount() <= 1) return;
        if ((position + 1) == mfRecyclerViewAdapter.getItemCount()) return;

        Track nextTrack = mfRecyclerViewAdapter.getItem(position + 1);

        mfRecyclerViewAdapter.update(position, nextTrack);
        mfRecyclerViewAdapter.update(position + 1, track);

        savePlaylist();
    }

    private void removeFromPlaylist(int position, Track track) {
        if (removeFromPlaylist(playlist, track)) {
            mfRecyclerViewAdapter.remove(position);
            mfRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    private void loadTracks() {
        if (playlist != null) {
            AsyncTask.execute(this::loadTracksAsyncTask);
        }
    }

    private void loadTracksAsyncTask() {
        runOnUiThread(() -> mfRecyclerViewAdapter.clear());

        List<Long> idList = playlist.getTrackIds();
        List<Track> trackList = GetTracks.getTrackList(this, idList);

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

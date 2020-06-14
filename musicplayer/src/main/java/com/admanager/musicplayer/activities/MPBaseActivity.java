package com.admanager.musicplayer.activities;

import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.admanager.musicplayer.R;
import com.admanager.musicplayer.models.Genre;
import com.admanager.musicplayer.models.Playlist;
import com.admanager.musicplayer.models.Track;
import com.admanager.musicplayer.tasks.GetTracks;
import com.admanager.musicplayer.utilities.ContextUtils;
import com.admanager.musicplayer.utilities.SharedPrefUtils;

import java.util.ArrayList;
import java.util.List;

public class MPBaseActivity extends AppCompatActivity {
    protected int pauseCount = 0;
    private LinearLayout customBannerLayout;
    private ImageView customBannerImage;
    private int selectedItem = 0;

    public void addGenreTracksToPlaylist(Genre genre) {
        if (genre == null) return;

        List<Track> trackList = GetTracks.getAllTracksForGenre(this, genre.getAudioIds());

        addTracksToPlaylist(trackList);
    }

    public void addAlbumTracksToPlaylist(Track albumTrack) {
        if (albumTrack == null) return;

        List<Track> trackList = GetTracks.getAllTracksForAlbum(this, albumTrack.getAlbumId());

        addTracksToPlaylist(trackList);
    }

    public void addArtistTracksToPlaylist(Track artistTrack) {
        if (artistTrack == null) return;

        List<Track> trackList = GetTracks.getAllTracksForArtist(this, artistTrack.getArtistId());

        addTracksToPlaylist(trackList);
    }

    public void addTracksToPlaylist(List<Track> trackList) {
        if (trackList == null || trackList.size() == 0) return;

        if (ContextUtils.isContextValid(this)) {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
            builderSingle.setTitle(getString(R.string.mp_add_to_playlist));

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice);
            List<Playlist> playlistList = SharedPrefUtils.getPlaylistArrayList(this);
            if (playlistList != null && playlistList.size() > 0) {
                for (Playlist playlist : playlistList) {
                    arrayAdapter.add(playlist.getName());
                }
            }

            builderSingle.setNegativeButton(getString(R.string.mp_cancel_uppercase), (dialog, which) -> dialog.dismiss());

            builderSingle.setSingleChoiceItems(arrayAdapter, 0, (dialog, which) -> selectedItem = which);

            builderSingle.setPositiveButton(getString(R.string.mp_add_uppercase), (dialog, which) ->
            {
                if (arrayAdapter != null && arrayAdapter.getCount() > 0 && selectedItem >= 0) {
                    String playlistName = arrayAdapter.getItem(selectedItem);

                    addToPlaylist(playlistName, trackList);
                } else {
                    if (ContextUtils.isContextValid(this)) {
                        Toast toast = Toast.makeText(this, getString(R.string.mp_playlist_must_be_created), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });

            builderSingle.setNeutralButton(getString(R.string.mp_create_uppercase), (dialog, which) -> createPlaylist(trackList));

            // create alert dialog
            AlertDialog alertDialog = builderSingle.create();
            alertDialog.setCancelable(false);

            // show it
            alertDialog.show();
        }
    }

    public void addToPlaylist(Track track) {
        if (track == null) return;

        if (ContextUtils.isContextValid(this)) {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
            builderSingle.setTitle(getString(R.string.mp_add_to_playlist));

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice);
            List<Playlist> playlistList = SharedPrefUtils.getPlaylistArrayList(this);
            if (playlistList != null && playlistList.size() > 0) {
                for (Playlist playlist : playlistList) {
                    arrayAdapter.add(playlist.getName());
                }
            }

            builderSingle.setNegativeButton(getResources().getString(R.string.mp_cancel_uppercase), (dialog, which) -> dialog.dismiss());

            builderSingle.setSingleChoiceItems(arrayAdapter, 0, (dialog, which) -> selectedItem = which);

            builderSingle.setPositiveButton(getResources().getString(R.string.mp_add_uppercase), (dialog, which) ->
            {
                if (arrayAdapter != null && arrayAdapter.getCount() > 0 && selectedItem >= 0) {
                    String playlistName = arrayAdapter.getItem(selectedItem);

                    addToPlaylist(playlistName, track);
                } else {
                    if (ContextUtils.isContextValid(this) && track != null) {
                        Toast toast = Toast.makeText(this, getResources().getString(R.string.mp_playlist_must_be_created), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });

            builderSingle.setNeutralButton(getResources().getString(R.string.mp_create_uppercase), (dialog, which) -> createPlaylist(track));

            // create alert dialog
            AlertDialog alertDialog = builderSingle.create();
            alertDialog.setCancelable(false);

            // show it
            alertDialog.show();
        }
    }

    private void addToPlaylist(String playlistName, Track track) {
        ArrayList<Playlist> playlistList = SharedPrefUtils.getPlaylistArrayList(this);

        boolean added = false;
        if (playlistList != null && playlistList.size() > 0 && track != null) {
            for (Playlist playlist : playlistList) {
                if (playlist.getName().toLowerCase().equalsIgnoreCase(playlistName.toLowerCase())) {
                    List<Long> trackIdList = playlist.getTrackIds();

                    if (trackIdList != null) {
                        boolean existTrack = false;
                        for (Long id : trackIdList) {
                            if (id == track.getId()) {
                                existTrack = true;
                                break;
                            }
                        }

                        if (!existTrack) {
                            trackIdList.add(track.getId());

                            SharedPrefUtils.setPlaylistArrayList(this, playlistList);

                            added = true;
                        }
                    }

                    break;
                }
            }
        }

        if (added) {
            if (ContextUtils.isContextValid(this) && track != null) {
                Toast toast = Toast.makeText(this, getString(R.string.mp_track_added_to_playlist, track.getTitle(), playlistName), Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            if (ContextUtils.isContextValid(this) && track != null) {
                Toast toast = Toast.makeText(this, getString(R.string.mp_track_not_added_to_playlist, track.getTitle(), playlistName), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void addToPlaylist(String playlistName, List<Track> trackList) {
        ArrayList<Playlist> playlistList = SharedPrefUtils.getPlaylistArrayList(this);

        boolean added = false;
        if (playlistList != null && playlistList.size() > 0 && trackList != null && trackList.size() > 0) {
            for (Playlist playlist : playlistList) {
                if (playlist.getName().toLowerCase().equalsIgnoreCase(playlistName.toLowerCase())) {
                    List<Long> idList = playlist.getTrackIds();

                    for (Track track : trackList) {
                        if (track != null && !existTrack(track, idList)) {
                            idList.add(track.getId());

                            added = true;
                        }
                    }

                    SharedPrefUtils.setPlaylistArrayList(this, playlistList);

                    break;
                }
            }
        }

        if (added) {
            if (ContextUtils.isContextValid(this) && trackList != null && trackList.size() > 0) {
                Toast toast = Toast.makeText(this, "Tracks are added to " + playlistName + ".", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            if (ContextUtils.isContextValid(this) && trackList != null && trackList.size() > 0) {
                Toast toast = Toast.makeText(this, "Tracks are not added to " + playlistName + "!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private boolean existTrack(Track track, List<Long> trackIdList) {
        for (Long id : trackIdList) {
            if (track.getId() == id) {
                return true;
            }
        }

        return false;
    }

    private void createPlaylist(Track track) {
        if (ContextUtils.isContextValid(this)) {
            final EditText taskEditText = new EditText(this);
            taskEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            taskEditText.requestFocus();

            android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.mp_create_a_playlist))
                    .setMessage(getResources().getString(R.string.mp_enter_playlist_name))
                    .setView(taskEditText)
                    .setPositiveButton(getResources().getString(R.string.mp_ok), (dialog1, which) ->
                    {
                        String playlistName = String.valueOf(taskEditText.getText());

                        createPlaylist(playlistName, track);
                    })
                    .setNegativeButton(getResources().getString(R.string.mp_cancel), null)
                    .create();
            dialog.show();
        }
    }

    private void createPlaylist(List<Track> trackList) {
        if (ContextUtils.isContextValid(this)) {
            final EditText taskEditText = new EditText(this);
            taskEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            taskEditText.requestFocus();

            android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.mp_create_a_playlist))
                    .setMessage(getResources().getString(R.string.mp_enter_playlist_name))
                    .setView(taskEditText)
                    .setPositiveButton(getResources().getString(R.string.mp_ok), (dialog1, which) ->
                    {
                        String playlistName = String.valueOf(taskEditText.getText());

                        createPlaylist(playlistName, trackList);
                    })
                    .setNegativeButton(getResources().getString(R.string.mp_cancel), null)
                    .create();
            dialog.show();
        }
    }

    public void createPlaylist(String playlistName, List<Track> trackList) {
        if (TextUtils.isEmpty(playlistName)) return;

        if (trackList == null || trackList.size() == 0) return;

        Playlist playlist = new Playlist();
        playlist.setName(playlistName);

        List<Long> idList = new ArrayList<>();
        for (Track track : trackList) {
            idList.add(track.getId());
        }

        playlist.setTrackIds(idList);

        if (SharedPrefUtils.createPlaylist(this, playlist)) {
            if (ContextUtils.isContextValid(this)) {
                Toast toast = Toast.makeText(this, getString(R.string.mp_playlist_created_tracks_added, playlistName, trackList.size()), Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            if (ContextUtils.isContextValid(this)) {
                Toast toast = Toast.makeText(this, getString(R.string.mp_playlist_exists, playlistName), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public void createPlaylist(String playlistName, Track track) {
        Playlist playlist = new Playlist();
        playlist.setName(playlistName);

        List<Long> idList = new ArrayList<>();
        idList.add(track.getId());
        playlist.setTrackIds(idList);

        if (SharedPrefUtils.createPlaylist(this, playlist)) {
            if (ContextUtils.isContextValid(this)) {
                Toast toast = Toast.makeText(this, getString(R.string.mp_playlist_added_one_track_add, playlistName), Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            if (ContextUtils.isContextValid(this)) {
                Toast toast = Toast.makeText(this, getString(R.string.mp_playlist_exists, playlistName), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public boolean removeFromPlaylist(Playlist playlist, Track track) {
        boolean removed = false;

        if (playlist != null && track != null) {
            ArrayList<Playlist> playlistList = SharedPrefUtils.getPlaylistArrayList(this);

            for (Playlist playlist1 : playlistList) {
                if (playlist1.getName().toLowerCase().equalsIgnoreCase(playlist.getName().toLowerCase())) {
                    List<Long> idList = playlist1.getTrackIds();

                    if (idList != null) {
                        for (Long id : idList) {
                            if (id == track.getId()) {
                                removed = true;
                                idList.remove(id);
                                playlist1.setTrackIds(idList);
                                playlist.setTrackIds(idList);
                                SharedPrefUtils.setPlaylistArrayList(this, playlistList);
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (removed) {
            if (ContextUtils.isContextValid(this)) {
                Toast toast = Toast.makeText(this, getString(R.string.mp_track_removed_from_playlist, track.getTitle(), playlist.getName()), Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            if (ContextUtils.isContextValid(this) && track != null && playlist != null) {
                Toast toast = Toast.makeText(this, getString(R.string.mp_track_not_removed_from_playlist, track.getTitle(), playlist.getName()), Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        return removed;
    }

    public boolean deletePlaylist(Playlist playlist) {
        boolean deleted = false;

        ArrayList<Playlist> playlistArrayList = SharedPrefUtils.getPlaylistArrayList(this);

        for (Playlist playlist1 : playlistArrayList) {
            if (playlist != null && playlist1 != null && playlist.getName() != null && playlist1.getName() != null &&
                    playlist.getName().toLowerCase().equalsIgnoreCase(playlist1.getName().toLowerCase())) {
                playlistArrayList.remove(playlist1);

                SharedPrefUtils.setPlaylistArrayList(this, playlistArrayList);

                deleted = true;

                break;
            }
        }

        if (deleted) {
            if (ContextUtils.isContextValid(this) && playlist != null) {
                Toast toast = Toast.makeText(this, getString(R.string.mp_playlist_deleted, playlist.getName()), Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            if (ContextUtils.isContextValid(this) && playlist != null) {
                Toast toast = Toast.makeText(this, getString(R.string.mp_playlist_not_deleted, playlist.getName()), Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        return deleted;
    }
}
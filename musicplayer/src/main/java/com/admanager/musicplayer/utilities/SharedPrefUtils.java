package com.admanager.musicplayer.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.admanager.musicplayer.models.Playlist;
import com.admanager.musicplayer.models.Preset;
import com.admanager.musicplayer.models.Track;
import com.admanager.musicplayer.tasks.GetTracks;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SharedPrefUtils {
    // Shared Preferences Variables
    private static final String SETTINGS_NAME = "settings";

    private static final String NOTIFICATION_START_NAME = "notification";

    private static final String FIRST_START_NAME = "first_start";

    private static final String RECENT_TRACK_ARRAYLIST = "recent_track_arraylist";

    private static final String PLAYLIST_ARRAYLIST = "playlist_arraylist";

    private static final String PLAYER_CURRENT_LIST_ARRAYLIST = "player_current_list_arraylist";

    private static final String PLAYER_ORIGINAL_LIST_ARRAYLIST = "player_original_list_arraylist";

    private static final String PRESET_ARRAYLIST = "preset_arraylist";

    private static final String PLAYER_CURRENT_POSITION_NAME = "player_current_position";

    private static final String TRACK_SEEKBAR_POSITION_NAME = "track_seekbar_position";

    private static final String SHUFFLE_NAME = "shuffle";
    private static final String REPLAY_NAME = "replay";

    private static final String BASS_BOOST_OPEN_NAME = "bass_boost_open";
    private static final String BASS_BOOST_LEVEL_NAME = "bass_boost_level";

    private static final String FIRST_SET_EQUALIZER_NAME = "first_set_equalizer";

    private static final String PRESET_NAME = "preset";
    private static final String PRESET_NUMBER_NAME = "preset_number";

    private static final int MAX_RECENT_SIZE = 10;

    private static final String FIRST_SET_ACTION_VIEW = "first_set_action_view";

    // HashSet
    private static HashSet<String> getHashSet(Context context, String key) {
        HashSet<String> hashSet = new HashSet<>();
        if (context != null) {
            SharedPreferences settings = context.getSharedPreferences(SETTINGS_NAME, 0);
            hashSet = (HashSet<String>) settings.getStringSet(key, new HashSet<>());
        }

        return hashSet;
    }

    private static void setHashSet(Context context, String key, HashSet<String> hashSet) {
        if (context != null) {
            SharedPreferences settings = context.getSharedPreferences(SETTINGS_NAME, 0);
            SharedPreferences.Editor editorFavorites = settings.edit();
            editorFavorites.remove(key);
            editorFavorites.apply();
            editorFavorites.putStringSet(key, hashSet);
            editorFavorites.apply();
        }
    }

    // Boolean
    private static boolean getBoolean(Context context, String key, boolean defaultValue) {
        boolean returnValue = defaultValue;
        if (context != null) {
            SharedPreferences settings = context.getSharedPreferences(SETTINGS_NAME, 0);
            returnValue = settings.getBoolean(key, defaultValue);
        }

        return returnValue;
    }

    private static void setBoolean(Context context, String key, boolean value) {
        if (context != null) {
            SharedPreferences settings = context.getSharedPreferences(SETTINGS_NAME, 0);

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(key, value);
            editor.apply();
        }
    }

    // String
    private static String getString(Context context, String key, String defaultValue) {
        String returnValue = defaultValue;
        if (context != null) {
            SharedPreferences settings = context.getSharedPreferences(SETTINGS_NAME, 0);
            returnValue = settings.getString(key, defaultValue);
        }

        return returnValue;
    }

    private static void setString(Context context, String key, String value) {
        if (context != null) {
            SharedPreferences settings = context.getSharedPreferences(SETTINGS_NAME, 0);

            SharedPreferences.Editor editor = settings.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }

    // Int
    private static int getInt(Context context, String key, int defaultValue) {
        int returnValue = defaultValue;
        if (context != null) {
            SharedPreferences settings = context.getSharedPreferences(SETTINGS_NAME, 0);
            returnValue = settings.getInt(key, defaultValue);
        }

        return returnValue;
    }

    private static void setInt(Context context, String key, int value) {
        if (context != null) {
            SharedPreferences settings = context.getSharedPreferences(SETTINGS_NAME, 0);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(key, value);
            editor.apply();
        }
    }

    // Float
    private static float getFloat(Context context, String key, float defaultValue) {
        float returnValue = defaultValue;
        if (context != null) {
            SharedPreferences settings = context.getSharedPreferences(SETTINGS_NAME, 0);
            returnValue = settings.getFloat(key, defaultValue);
        }

        return returnValue;
    }

    private static void setFloat(Context context, String key, float value) {
        if (context != null) {
            SharedPreferences settings = context.getSharedPreferences(SETTINGS_NAME, 0);

            SharedPreferences.Editor editor = settings.edit();
            editor.putFloat(key, value);
            editor.apply();
        }
    }

    private static ArrayList<Long> getTrackIdArrayList(Context context, String key) {
        ArrayList<Long> arrayList = new ArrayList<>();
        if (context != null) {
            SharedPreferences mPrefs = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = mPrefs.getString(key, "");
            if (TextUtils.isEmpty(json)) {
                arrayList = new ArrayList<>();
            } else {
                Type type = new TypeToken<ArrayList<Long>>() {
                }.getType();
                arrayList = gson.fromJson(json, type);
            }
        }
        return arrayList;
    }

    private static void setTrackIdArrayList(Context context, String key, ArrayList<Long> arrayList) {
        if (context != null) {
            SharedPreferences mPrefs = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(arrayList);
            prefsEditor.putString(key, json);
            prefsEditor.apply();
        }
    }

    private static ArrayList<Playlist> getPlaylistArrayList(Context context, String key) {
        ArrayList<Playlist> arrayList = new ArrayList<>();
        if (context != null) {
            SharedPreferences mPrefs = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = mPrefs.getString(key, "");
            if (TextUtils.isEmpty(json)) {
                arrayList = new ArrayList<>();
            } else {
                Type type = new TypeToken<ArrayList<Playlist>>() {
                }.getType();
                arrayList = gson.fromJson(json, type);
            }
        }
        return arrayList;
    }

    private static void setPlaylistArrayList(Context context, String key, ArrayList<Playlist> arrayList) {
        if (context != null) {
            SharedPreferences mPrefs = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(arrayList);
            prefsEditor.putString(key, json);
            prefsEditor.apply();
        }
    }

    private static ArrayList<Preset> getPresetArrayList(Context context, String key) {
        ArrayList<Preset> arrayList = new ArrayList<>();
        if (context != null) {
            SharedPreferences mPrefs = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = mPrefs.getString(key, "");
            if (TextUtils.isEmpty(json)) {
                arrayList = new ArrayList<>();
            } else {
                Type type = new TypeToken<ArrayList<Preset>>() {
                }.getType();
                arrayList = gson.fromJson(json, type);
            }
        }
        return arrayList;
    }

    private static void setPresetArrayList(Context context, String key, ArrayList<Preset> arrayList) {
        if (context != null) {
            SharedPreferences mPrefs = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(arrayList);
            prefsEditor.putString(key, json);
            prefsEditor.apply();
        }
    }

    // Project Specific
    public static boolean getStartNotification(Context context) {
        return getBoolean(context, NOTIFICATION_START_NAME, true);
    }

    public static void setStartNotification(Context context, boolean value) {
        setBoolean(context, NOTIFICATION_START_NAME, value);
    }

    public static boolean getFirstStarted(Context context) {
        return getBoolean(context, FIRST_START_NAME, false);
    }

    public static void setFirstStarted(Context context, boolean value) {
        setBoolean(context, FIRST_START_NAME, value);
    }

    public static boolean getFirstSetEqualizer(Context context) {
        return getBoolean(context, FIRST_SET_EQUALIZER_NAME, false);
    }

    public static void setFirstSetEqualizer(Context context, boolean value) {
        setBoolean(context, FIRST_SET_EQUALIZER_NAME, value);
    }

    public static boolean getFirstActionView(Context context) {
        return getBoolean(context, FIRST_SET_ACTION_VIEW, false);
    }

    public static void setFirstActionView(Context context, boolean value) {
        setBoolean(context, FIRST_SET_ACTION_VIEW, value);
    }


    //Shuffle
    public static boolean getShuffle(Context context) {
        return getBoolean(context, SHUFFLE_NAME, false);
    }

    public static void setShuffle(Context context, boolean value) {
        setBoolean(context, SHUFFLE_NAME, value);
    }

    public static Constants.REPLAY_TYPE getReplay(Context context) {
        String enumString = getString(context, REPLAY_NAME, Constants.REPLAY_TYPE.NO_REPLAY.toString());

        return Constants.REPLAY_TYPE.toEnum(enumString);
    }

    public static void setReplay(Context context, Constants.REPLAY_TYPE value) {
        setString(context, REPLAY_NAME, value.toString());
    }

    //Recent Track
    public static List<Track> getRecentTrackArrayList(Context context) {
        ArrayList<Long> idList = getTrackIdArrayList(context, RECENT_TRACK_ARRAYLIST);

        return GetTracks.getTrackList(context, idList);
    }

    public static void setRecentTrackArrayList(Context context, List<Track> arrayList) {
        ArrayList<Long> idList = new ArrayList<>();

        for (Track track : arrayList) {
            idList.add(track.getId());
        }

        setTrackIdArrayList(context, RECENT_TRACK_ARRAYLIST, idList);
    }

    public static List<Track> getRecentTrackArraySearchList(Context context, String query) {
        if (TextUtils.isEmpty(query)) return getRecentTrackArrayList(context);

        ArrayList<Track> trackList = new ArrayList<>();

        for (Track track : getRecentTrackArrayList(context)) {
            if (track.getTitle().toLowerCase().contains(query.toLowerCase())) {
                trackList.add(track);
            }
        }

        return trackList;
    }

    public static void addRecentTrack(Context context, Track track) {
        List<Track> recentTracks = getRecentTrackArrayList(context);

        if (recentTracks == null) return;

        if (track == null) return;

        int size = recentTracks.size();
        if (size > 0) {
            Track lastTrack = recentTracks.get(size - 1);

            if (lastTrack != null && lastTrack.getPath() != null && track.getPath() != null && lastTrack.getPath().equalsIgnoreCase(track.getPath()))
                return;
        }

        for (Track track1 : recentTracks) {
            if (track1 != null && track1.getPath() != null && track.getPath() != null && track1.getPath().equalsIgnoreCase(track.getPath())) {
                recentTracks.remove(track1);
                recentTracks.add(track);
                setRecentTrackArrayList(context, recentTracks);
                return;
            }
        }

        if (size < MAX_RECENT_SIZE) {
            recentTracks.add(track);

            setRecentTrackArrayList(context, recentTracks);
        } else {
            recentTracks.add(track);

            recentTracks.remove(0);

            setRecentTrackArrayList(context, recentTracks);
        }
    }

    //Playlist
    public static ArrayList<Playlist> getPlaylistArrayList(Context context) {
        return getPlaylistArrayList(context, PLAYLIST_ARRAYLIST);
    }

    public static void setPlaylistArrayList(Context context, ArrayList<Playlist> arrayList) {
        setPlaylistArrayList(context, PLAYLIST_ARRAYLIST, arrayList);
    }

    public static ArrayList<Playlist> getPlaylistArraySearchList(Context context, String query) {
        if (TextUtils.isEmpty(query)) return getPlaylistArrayList(context);

        ArrayList<Playlist> playlistArrayList = new ArrayList<>();

        for (Playlist playlist : getPlaylistArrayList(context)) {
            if (playlist.getName().toLowerCase().contains(query.toLowerCase())) {
                playlistArrayList.add(playlist);
            }
        }

        return playlistArrayList;
    }

    public static boolean createPlaylist(Context context, Playlist playlist) {
        ArrayList<Playlist> playlistArrayList = getPlaylistArrayList(context);

        if (playlistArrayList == null) return false;

        for (Playlist playlist1 : playlistArrayList) {
            if (playlist1.getName().toLowerCase().equalsIgnoreCase(playlist.getName().toLowerCase())) {
                return false;
            }
        }

        playlistArrayList.add(playlist);

        setPlaylistArrayList(context, playlistArrayList);

        return true;
    }

    public static Playlist getPlaylist(Context context, String playlistName) {
        ArrayList<Playlist> playlistArrayList = getPlaylistArrayList(context);

        if (playlistArrayList != null && playlistArrayList.size() > 0) {
            for (Playlist playlist : playlistArrayList) {
                if (playlist.getName().toLowerCase().equalsIgnoreCase(playlistName.toLowerCase())) {
                    return playlist;
                }
            }
        }

        return null;
    }

    //Player Current List
    public static ArrayList<Long> getPlayerCurrentIdList(Context context) {
        return getTrackIdArrayList(context, PLAYER_CURRENT_LIST_ARRAYLIST);
    }

    public static List<Track> getPlayerCurrentTrackList(Context context) {
        ArrayList<Long> idList = getTrackIdArrayList(context, PLAYER_CURRENT_LIST_ARRAYLIST);

        return GetTracks.getTrackList(context, idList);
    }

    public static void setPlayerCurrentTrackList(Context context, List<Track> arrayList) {
        ArrayList<Long> idList = new ArrayList<>();

        for (Track track : arrayList) {
            idList.add(track.getId());
        }

        setTrackIdArrayList(context, PLAYER_CURRENT_LIST_ARRAYLIST, idList);
    }

    //Player Original List
    public static ArrayList<Long> getPlayerOriginalIdList(Context context) {
        return getTrackIdArrayList(context, PLAYER_ORIGINAL_LIST_ARRAYLIST);
    }

    public static void setPlayerOriginalIdList(Context context, ArrayList<Long> idList) {
        setTrackIdArrayList(context, PLAYER_ORIGINAL_LIST_ARRAYLIST, idList);
    }

    public static List<Track> getPlayerOriginalTrackList(Context context) {
        ArrayList<Long> idList = getTrackIdArrayList(context, PLAYER_ORIGINAL_LIST_ARRAYLIST);

        return GetTracks.getTrackList(context, idList);
    }

    public static void setPlayerOriginalTrackList(Context context, List<Track> arrayList) {
        ArrayList<Long> idList = new ArrayList<>();

        for (Track track : arrayList) {
            idList.add(track.getId());
        }

        setTrackIdArrayList(context, PLAYER_ORIGINAL_LIST_ARRAYLIST, idList);
    }

    //Player Current Row Position
    public static int getPlayerCurrentPosition(Context context) {
        return getInt(context, PLAYER_CURRENT_POSITION_NAME, 0);
    }

    public static void setPlayerCurrentPosition(Context context, int value) {
        setInt(context, PLAYER_CURRENT_POSITION_NAME, value);
    }

    public static int getTrackSeekbarPosition(Context context) {
        return getInt(context, TRACK_SEEKBAR_POSITION_NAME, 0);
    }

    public static void setTrackSeekbarPosition(Context context, int value) {
        setInt(context, TRACK_SEEKBAR_POSITION_NAME, value);
    }

    //Bass Boost
    public static boolean getBassBoostOpen(Context context) {
        return getBoolean(context, BASS_BOOST_OPEN_NAME, false);
    }

    public static void setBassBoostOpen(Context context, boolean value) {
        setBoolean(context, BASS_BOOST_OPEN_NAME, value);
    }

    public static int getBassBoostLevel(Context context) {
        return getInt(context, BASS_BOOST_LEVEL_NAME, 0);
    }

    public static void setBassBoostLevel(Context context, int value) {
        setInt(context, BASS_BOOST_LEVEL_NAME, value);
    }

    //Playlist
    public static ArrayList<Preset> getPresetArrayList(Context context) {
        return getPresetArrayList(context, PRESET_ARRAYLIST);
    }

    public static void setPresetArrayList(Context context, ArrayList<Preset> arrayList) {
        setPresetArrayList(context, PRESET_ARRAYLIST, arrayList);
    }

    //Preset Name
    public static String getPresetName(Context context) {
        return getString(context, PRESET_NAME, Constants.CUSTOM_PRESET_NAME);
    }

    public static void setPresetName(Context context, String value) {
        setString(context, PRESET_NAME, value);
    }

    public static int getPresetNumber(Context context) {
        return getInt(context, PRESET_NUMBER_NAME, 0);
    }

    public static void setPresetNumber(Context context, int value) {
        setInt(context, PRESET_NUMBER_NAME, value);
    }
}

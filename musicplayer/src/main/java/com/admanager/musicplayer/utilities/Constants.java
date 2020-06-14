package com.admanager.musicplayer.utilities;

public class Constants {
    public static final String MUSIC_TABS_NAME = "music_tabs";
    public static final String[] PERMISSIONS_STORAGE = new String[]{
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final int REQUEST_CODE_READ_AND_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    public static final String NOTIFICATION_MEDIA_PLAYER_SERVICE_CHANNEL_ID = "not_mp_service";
    public static final String BUNDLE_NAME = "bundle";
    public static final String FILE_OBJECT = "file_object";
    public static final String TRACK_OBJECT = "track_object";
    public static final String GENRE_OBJECT = "genre_object";
    public static final String PLAYLIST_OBJECT = "playlist_object";
    public static final String ACTION_URI_NAME = "action_uri";
    public static final int CUSTOM_PRESET_NUMBER = 1001;
    public static final String CUSTOM_PRESET_NAME = "Custom";
    public static final String ROOT_NAME = "root";
    public static final String INTERNAL_STORAGE_NAME = "Internal Storage";
    public static final String EXTERNAL_STORAGE_NAME = "External Storage";
    public static final String[] SOUND_FILE_TYPE = {"mp3", "mid", "wav", "m4a"};

    public enum REPLAY_TYPE {
        NO_REPLAY, REPLAY_ALL, REPLAY_ONE;

        public static REPLAY_TYPE toEnum(String enumString) {
            try {
                return valueOf(enumString);
            } catch (Exception ex) {
                // For error cases
                return NO_REPLAY;
            }
        }
    }

    public enum MUSIC_TABS {MUSIC, EQUALIZER, BASS_BOOST, VOLUME}
}

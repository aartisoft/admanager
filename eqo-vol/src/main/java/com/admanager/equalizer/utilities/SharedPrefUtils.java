package com.admanager.equalizer.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;

public class SharedPrefUtils {
    // Shared Preferences Variables
    private static final String SETTINGS_NAME = "settings";

    private static final String NOTIFICATION_START_NAME = "notification";
    private static final String FIRST_START_NAME = "first_start";

    private static final String PLAYER_PACKAGE_NAME = "package";
    private static final String PLAYER_STATE = "state";
    private static final String PLAYER_SESSION = "session";

    private static final String TRACK_TITLE = "track";
    private static final String TRACK_ARTIST = "artist";
    private static final String TRACK_ALBUM_ID = "album_id";

    private static final String PERSONALIZED_ENABLED = "personalized_enabled";
    private static final String USER_CONSENT = "userConsent";

    private static final String RECORD_FILE_NUMBER = "record_file_number";

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

    // Long
    private static long getLong(Context context, String key, long defaultValue) {
        long returnValue = defaultValue;
        if (context != null) {
            SharedPreferences settings = context.getSharedPreferences(SETTINGS_NAME, 0);
            returnValue = settings.getLong(key, defaultValue);
        }

        return returnValue;
    }

    private static void setLong(Context context, String key, long value) {
        if (context != null) {
            SharedPreferences settings = context.getSharedPreferences(SETTINGS_NAME, 0);

            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(key, value);
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

    public static String getPlayerPackageName(Context context) {
        return getString(context, PLAYER_PACKAGE_NAME, "");
    }

    public static void setPlayerPackageName(Context context, String value) {
        setString(context, PLAYER_PACKAGE_NAME, value);
    }

    public static boolean getPlayerState(Context context) {
        return getBoolean(context, PLAYER_STATE, true);
    }

    public static void setPlayerState(Context context, boolean value) {
        setBoolean(context, PLAYER_STATE, value);
    }

    public static String getTrackTitle(Context context) {
        return getString(context, TRACK_TITLE, "");
    }

    public static void setTrackTitle(Context context, String value) {
        setString(context, TRACK_TITLE, value);
    }

    public static String getTrackArtist(Context context) {
        return getString(context, TRACK_ARTIST, "");
    }

    public static void setTrackArtist(Context context, String value) {
        setString(context, TRACK_ARTIST, value);
    }

    public static long getTrackAlbumId(Context context) {
        return getLong(context, TRACK_ALBUM_ID, 0);
    }

    public static void setTrackAlbumId(Context context, long value) {
        setLong(context, TRACK_ALBUM_ID, value);
    }

    public static int getPlayerSession(Context context) {
        return getInt(context, PLAYER_SESSION, 0);
    }

    public static void setPlayerSession(Context context, int value) {
        setInt(context, PLAYER_SESSION, value);
    }

    public static boolean getUserConsent(Context context) {
        return getBoolean(context, USER_CONSENT, false);
    }

    public static void setUserConsent(Context context, boolean value) {
        setBoolean(context, USER_CONSENT, value);
    }

    public static boolean getPersonalizedEnabled(Context context) {
        return getBoolean(context, PERSONALIZED_ENABLED, false);
    }

    public static void setPersonalizedEnabled(Context context, boolean value) {
        setBoolean(context, PERSONALIZED_ENABLED, value);
    }

    public static int getRecordFileNumber(Context context) {
        return getInt(context, RECORD_FILE_NUMBER, 0);
    }

    public static void setRecordFileNumber(Context context, int value) {
        setInt(context, RECORD_FILE_NUMBER, value);
    }
}

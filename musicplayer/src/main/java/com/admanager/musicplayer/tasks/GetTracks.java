package com.admanager.musicplayer.tasks;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.LongSparseArray;

import com.admanager.musicplayer.models.Genre;
import com.admanager.musicplayer.models.Track;
import com.admanager.musicplayer.utilities.ContextUtils;
import com.admanager.musicplayer.utilities.MediaUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GetTracks {
    private static final List<Track> trackList = new ArrayList<>();

    private static final List<Genre> genreList = new ArrayList<>();

    private static final LongSparseArray<String> albumImageHashMap = new LongSparseArray<>();

    public static void refreshDatas(Context context) {
        trackList.clear();
        getAllTracks(context);

        genreList.clear();
        getAllGenres(context);

        albumImageHashMap.clear();
    }

    public static List<Track> getAllTracksForGenre(Context context, List<Long> audioIds) {
        List<Track> genreTrackList = new ArrayList<>();

        for (Track track : getAllTracks(context)) {
            if (audioIds != null && audioIds.size() > 0) {
                for (long audioId : audioIds) {
                    if (track.getId() == audioId) {
                        genreTrackList.add(track);
                    }
                }
            }
        }

        return genreTrackList;
    }

    public static List<Track> getAllTracksForAlbum(Context context, long albumId) {
        List<Track> albumTrackList = new ArrayList<>();

        for (Track track : getAllTracks(context)) {
            if (track.getAlbumId() == albumId) {
                albumTrackList.add(track);
            }
        }

        return albumTrackList;
    }

    public static List<Track> getAllTracksForArtist(Context context, long artistId) {
        List<Track> artistTrackList = new ArrayList<>();

        for (Track track : getAllTracks(context)) {
            if (track.getArtistId() == artistId) {
                artistTrackList.add(track);
            }
        }

        return artistTrackList;
    }

    public static List<Track> getAllTracks(Context context, String query) {
        if (TextUtils.isEmpty(query)) return getAllTracks(context);

        List<Track> searchTrackList = new ArrayList<>();

        for (Track track : getAllTracks(context)) {
            if (track.getTitle() != null && track.getTitle().toLowerCase().contains(query.toLowerCase())) {
                searchTrackList.add(track);
            }
        }

        return searchTrackList;
    }

    public static List<Track> getAllTracks(Context context) {
        if (trackList.size() > 0) return trackList;

        if (!ContextUtils.isContextValid(context)) return trackList;

        ContentResolver contentResolver = context.getContentResolver();
        Uri mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor mediaCursor = contentResolver.query(mediaUri, null, null, null, null);

        // if the cursor is null.
        if (mediaCursor != null && mediaCursor.moveToFirst()) {
            //get Columns
            int idColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int albumIdColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int artistIdColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);
            int pathColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int titleColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int albumColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int artistColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int durationColumn = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                durationColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            }
            int trackNoColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Media.TRACK);

            // Store the title, id and artist name in Song Array list.
            do {
                long id = mediaCursor.getLong(idColumn);
                long albumId = mediaCursor.getLong(albumIdColumn);
                long artistId = mediaCursor.getLong(artistIdColumn);
                String path = mediaCursor.getString(pathColumn);
                String title = mediaCursor.getString(titleColumn);
                String album = mediaCursor.getString(albumColumn);
                String artist = mediaCursor.getString(artistColumn);
                long duration = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    duration = mediaCursor.getLong(durationColumn);
                }
                int trackNo = mediaCursor.getInt(trackNoColumn);

                Track track = new Track();

                track.setId(id);
                track.setAlbumId(albumId);
                track.setArtistId(artistId);
                track.setPath(path);
                track.setTitle(title);
                track.setAlbum(album);
                track.setArtist(artist);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    track.setDuration(duration);
                    track.setTotalMsec(MediaUtils.getTimeWithFormatForMilliSecond(duration));
                }

                track.setTrackNo(trackNo);
                trackList.add(track);
            }
            while (mediaCursor.moveToNext());

            // For best practices, close the cursor after use.
            mediaCursor.close();
        }

        return trackList;
    }

    public static Track getTrack(Context context, Uri actionUri) {
        Track track = new Track();

        if (!ContextUtils.isContextValid(context)) return track;

        ContentResolver contentResolver = context.getContentResolver();

        Cursor mediaCursor = contentResolver.query(actionUri, null, null, null, null);

        // if the cursor is null.
        if (mediaCursor != null && mediaCursor.moveToFirst()) {
            //get Columns
            int idColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int albumIdColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int artistIdColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);
            int pathColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int titleColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int albumColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int artistColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int durationColumn = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                durationColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            }
            int trackNoColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Media.TRACK);

            long id = mediaCursor.getLong(idColumn);
            long albumId = mediaCursor.getLong(albumIdColumn);
            long artistId = mediaCursor.getLong(artistIdColumn);
            String path = mediaCursor.getString(pathColumn);
            String title = mediaCursor.getString(titleColumn);
            String album = mediaCursor.getString(albumColumn);
            String artist = mediaCursor.getString(artistColumn);
            long duration = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                duration = mediaCursor.getLong(durationColumn);
            }
            int trackNo = mediaCursor.getInt(trackNoColumn);

            track.setId(id);
            track.setAlbumId(albumId);
            track.setArtistId(artistId);
            track.setPath(path);
            track.setTitle(title);
            track.setAlbum(album);
            track.setArtist(artist);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                track.setDuration(duration);
                track.setTotalMsec(MediaUtils.getTimeWithFormatForMilliSecond(duration));
            }

            track.setTrackNo(trackNo);

            // For best practices, close the cursor after use.
            mediaCursor.close();
        }

        return track;
    }

    public static List<Track> getAllArtists(Context context, String query) {
        if (TextUtils.isEmpty(query)) return getAllArtists(context);

        List<Track> artistList = new ArrayList<>();

        for (Track track : getAllArtists(context)) {
            if (track.getArtist() != null && track.getArtist().toLowerCase().contains(query.toLowerCase())) {
                artistList.add(track);
            }
        }

        return artistList;
    }

    public static List<Track> getAllArtists(Context context) {
        List<Track> artistList = new ArrayList<>();

        Set<Long> artistIds = new HashSet<>();

        List<Track> currentTrackList = getAllTracks(context);
        if (currentTrackList != null && currentTrackList.size() > 0) {
            for (Track track : currentTrackList) {
                if (!artistIds.contains(track.getArtistId())) {
                    artistList.add(track);

                    artistIds.add(track.getArtistId());
                }
            }
        }

        return artistList;
    }

    public static List<Track> getAllAlbums(Context context, String query) {
        if (TextUtils.isEmpty(query)) return getAllAlbums(context);

        List<Track> albumList = new ArrayList<>();

        for (Track track : getAllAlbums(context)) {
            if (track.getAlbum() != null && track.getAlbum().toLowerCase().contains(query.toLowerCase())) {
                albumList.add(track);
            }
        }

        return albumList;
    }

    public static List<Track> getAllAlbums(Context context) {
        List<Track> albumList = new ArrayList<>();

        Set<Long> albumIds = new HashSet<>();

        List<Track> currentTrackList = getAllTracks(context);
        if (currentTrackList != null && currentTrackList.size() > 0) {
            for (Track track : currentTrackList) {
                if (!albumIds.contains(track.getAlbumId())) {
                    albumList.add(track);

                    albumIds.add(track.getAlbumId());
                }
            }
        }

        return albumList;
    }

    public static List<Genre> getAllGenres(Context context, String query) {
        if (TextUtils.isEmpty(query)) return getAllGenres(context);

        List<Genre> genreList = new ArrayList<>();

        for (Genre genre : getAllGenres(context)) {
            if (genre.getName() != null && genre.getName().toLowerCase().contains(query.toLowerCase())) {
                genreList.add(genre);
            }
        }

        return genreList;
    }

    public static List<Genre> getAllGenres(Context context) {
        if (genreList.size() > 0) return genreList;

        if (!ContextUtils.isContextValid(context)) return genreList;

        LongSparseArray<List<Long>> genreHashMap = new LongSparseArray<>();
        String[] genresProjection = {
                MediaStore.Audio.Genres.Members.AUDIO_ID,
                MediaStore.Audio.Genres.Members.GENRE_ID
        };
        Cursor mediaCursor = context.getContentResolver().query(Uri.parse("content://media/external/audio/genres/all/members"), genresProjection, null, null, null);
        if (mediaCursor != null && mediaCursor.moveToFirst()) {
            int audioIdColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Genres.Members.AUDIO_ID);
            int genreIdColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Genres.Members.GENRE_ID);

            do {
                long audioId = mediaCursor.getLong(audioIdColumn);
                long genreId = mediaCursor.getLong(genreIdColumn);

                if (genreHashMap.indexOfKey(genreId) >= 0) {
                    List<Long> audioIds = genreHashMap.get(genreId);
                    audioIds.add(audioId);
                    genreHashMap.put(genreId, audioIds);
                } else {
                    List<Long> audioIds = new ArrayList<>();
                    audioIds.add(audioId);
                    genreHashMap.put(genreId, audioIds);
                }
            }
            while (mediaCursor.moveToNext());

            mediaCursor.close();
        }

        ContentResolver cr = context.getContentResolver();
        Uri uri = MediaStore.Audio.Genres.getContentUri("external");
        String idColumnName = MediaStore.Audio.Media._ID;
        String nameColumnName = MediaStore.Audio.Genres.NAME;
        final String[] columns = {idColumnName, nameColumnName};
        mediaCursor = cr.query(uri, columns, null, null, null);

        // if the cursor is null.
        if (mediaCursor != null && mediaCursor.moveToFirst()) {
            int idColumn = mediaCursor.getColumnIndex(idColumnName);
            int nameColumn = mediaCursor.getColumnIndex(nameColumnName);

            do {
                long id = mediaCursor.getLong(idColumn);
                String name = mediaCursor.getString(nameColumn);

                Genre genre = new Genre();
                genre.setGenreId(id);
                genre.setName(name);
                genre.setAudioIds(genreHashMap.get(id));

                genreList.add(genre);
            }
            while (mediaCursor.moveToNext());

            Collections.sort(genreList, (object1, object2) -> object1.getName().toLowerCase().compareTo(object2.getName().toLowerCase()));

            mediaCursor.close();
        }

        return genreList;
    }

    public static String getAlbumImagePath(Context context, long albumId) {
        String imagePath = albumImageHashMap.get(albumId);

        if (!TextUtils.isEmpty(imagePath)) return imagePath;

        if (!ContextUtils.isContextValid(context)) return null;

        Cursor artCursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.AlbumColumns.ALBUM_ART},
                MediaStore.Audio.Media._ID + " =?",
                new String[]{String.valueOf(albumId)},
                null);

        if (artCursor != null && artCursor.moveToNext() && !TextUtils.isEmpty(artCursor.getString(0))) {
            imagePath = "file://" + artCursor.getString(0);
            albumImageHashMap.put(albumId, imagePath);
        } else {
            imagePath = null;
        }

        if (artCursor != null) artCursor.close();

        return imagePath;
    }

    public static String getAlbumImagePathForAudioId(Context context, List<Long> audioIds) {
        List<Track> currentTrackList = getAllTracks(context);
        if (currentTrackList != null && currentTrackList.size() > 0) {
            for (Track track : currentTrackList) {
                if (audioIds != null && audioIds.size() > 0) {
                    for (long audioId : audioIds) {
                        if (track != null && track.getId() == audioId) {
                            return getAlbumImagePath(context, track.getAlbumId());
                        }
                    }
                }
            }
        }

        return "";
    }

    public static Track getTrack(Context context, String fileName) {
        List<Track> currentTrackList = getAllTracks(context);

        if (TextUtils.isEmpty(fileName)) return null;

        if (currentTrackList == null || currentTrackList.size() == 0) return null;

        for (Track track : currentTrackList) {
            if (track.getPath() != null && track.getPath().toLowerCase().contains(fileName.toLowerCase())) {
                return track;
            }
        }

        return null;
    }

    public static List<Track> getTrackList(Context context, List<Long> idList) {
        if (idList == null || idList.size() == 0) return new ArrayList<>();

        List<Track> currentTrackList = getAllTracks(context);

        if (currentTrackList == null || currentTrackList.size() == 0) return new ArrayList<>();

        List<Track> idTrackList = new ArrayList<>();

        if (idList != null && idList.size() > 0) {
            for (long id : idList) {
                for (Track track : currentTrackList) {
                    if (id == track.getId()) {
                        idTrackList.add(track);
                    }
                }
            }
        }

        return idTrackList;
    }
}

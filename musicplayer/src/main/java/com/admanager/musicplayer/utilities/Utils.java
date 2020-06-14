package com.admanager.musicplayer.utilities;

import java.io.File;

public class Utils {
    public static boolean isSoundFile(File file) {
        String[] soundFileTypes = Constants.SOUND_FILE_TYPE;
        if (soundFileTypes != null && soundFileTypes.length > 0) {
            for (String extension : soundFileTypes) {
                if (file.getPath().endsWith(extension)) {
                    return true;
                }
            }
        }

        return false;
    }
}

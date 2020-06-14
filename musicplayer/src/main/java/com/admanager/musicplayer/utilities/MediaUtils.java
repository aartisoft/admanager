package com.admanager.musicplayer.utilities;

import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import java.util.HashMap;

public class MediaUtils {
    public static long getDuration(String durationStr) {
        durationStr = durationStr.substring(2);
        long duration = 0L;
        Object[][] indexes = new Object[][]{{"H", 3600}, {"M", 60}, {"S", 1}};
        for (Object[] indexArray : indexes) {
            int index = durationStr.indexOf((String) indexArray[0]);
            if (index != -1) {
                String value = durationStr.substring(0, index);
                duration += Integer.parseInt(value) * (int) indexArray[1] * 1000;
                durationStr = durationStr.substring(value.length() + 1);
            }
        }

        return duration;
    }

    public static String findTotalMsec(String filePath) {
        String totalMsec = "";

        try {
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();

            metaRetriever.setDataSource(filePath, new HashMap<>());

            String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (!TextUtils.isEmpty(duration)) {
                long dur = Long.parseLong(duration);
                totalMsec = getTimeWithFormatForMilliSecond((int) dur);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return totalMsec;
    }

    public static String getTimeWithFormatForMilliSecond(long timeInMilliSeconds) {
        long seconds = timeInMilliSeconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        String prefix = "";
        if (hours > 0) prefix = hours % 24 + ":";

        return prefix + addZeroToHead(minutes % 60, 2) + ":" + addZeroToHead(seconds % 60, 2);
    }

    public static String addZeroToHead(long number, int digit) {
        StringBuilder str = new StringBuilder();
        int currentDigit = 0;

        if (number >= 0 && number < 10) currentDigit = 1;
        else if (number < 100) currentDigit = 2;
        else if (number < 1000) currentDigit = 3;

        if (currentDigit > 0) {
            for (int i = 0; i < digit - currentDigit; i++) {
                str.insert(0, "0");
            }
        }

        str.append(number);

        return str.toString();
    }
}

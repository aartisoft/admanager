package com.admanager.unseen.utils;

import java.util.Iterator;
import java.util.List;

/**
 * Created by a on 28.03.2017.
 */
public class CustomTextUtils {
    public static String adjust(CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }
        String r;
        if (charSequence instanceof String) {
            r = (String) charSequence;
        } else {
            r = charSequence.toString();
        }
        return r.replaceAll("(\\s+$|^\\s+)", "").replaceAll("\n+", "\n");
    }

    public static void removeIfHasNot(List<String> list, String str) {
        Iterator listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            if (!((String) listIterator.next()).contains(str)) {
                listIterator.remove();
            }
        }
    }
}

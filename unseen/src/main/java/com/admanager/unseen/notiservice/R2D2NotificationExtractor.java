package com.admanager.unseen.notiservice;

import android.app.Notification;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.admanager.unseen.notiservice.models.AppNotification;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by a on 28.03.2017.
 */
public class R2D2NotificationExtractor implements INotificationExtractor {
    private static final String TAG = "R2D2NE";

    R2D2NotificationExtractor() {
    }

    public final AppNotification extract(Context context, Notification notification) {
        AppNotification wan = new AppNotification();
        List<String> arrayList = new ArrayList<>();
        try {
            Object obj = notification.bigContentView;
            if (obj == null) {
                obj = notification.contentView;
            }
            if (obj == null) {
                return null;
            }
            Field declaredField = obj.getClass().getDeclaredField("mActions");
            declaredField.setAccessible(true);
            Iterator it = ((ArrayList) declaredField.get(obj)).iterator();
            while (it.hasNext()) {
                Parcelable parcelable = (Parcelable) it.next();
                Parcel obtain = Parcel.obtain();
                parcelable.writeToParcel(obtain, 0);
                obtain.setDataPosition(0);
                if (obtain.readInt() == 2) {
                    obtain.readInt();
                    String readString = obtain.readString();
                    if (readString != null) {
                        if (readString.equals("setText")) {
                            obtain.readInt();
                            arrayList.add(((CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(obtain)).toString().trim());
                        } else {
                            readString.equals("setTime");
                        }
                        obtain.recycle();
                    }
                }
            }
            if (arrayList.size() > 1) {
                wan.title = (String) arrayList.get(0);
                if (arrayList.size() == 2) {
                    wan.messages = arrayList.subList(1, arrayList.size());
                } else {
                    Log.i(TAG, "Removing unused line: " + arrayList.get(1));
                    wan.messages = arrayList.subList(2, arrayList.size());
                }
            }
            return wan;
        } catch (Exception e) {
            Log.e("NotificationClassifier", e.toString());
        }
        return null;
    }

    public final String getName() {
        return "R2D2";
    }
}

package com.admanager.unseen.notiservice;

import android.app.Notification;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.admanager.unseen.notiservice.models.AppNotification;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by a on 28.04.2017.
 */
public class JediNotificationExtractor implements INotificationExtractor {
    public static final String TAG = "JediNE";

    private static Bundle getBundle(Notification notification) {
        if (Build.VERSION.SDK_INT >= 19) {
            return notification.extras;
        }
        try {
            Field declaredField = notification.getClass().getDeclaredField("extras");
            declaredField.setAccessible(true);
            return (Bundle) declaredField.get(notification);
        } catch (Exception e) {
            Log.w(TAG, "Failed to access extras on Jelly Bean.");
            return null;
        }
    }

    private static void readFromViews(AppNotification wan, Context context, Notification n) {
        TextView textView = null;
        try {
            int size;
            TextView textView2;
            RemoteViews remoteViews = n.bigContentView == null ? n.contentView : n.bigContentView;
            ViewGroup viewGroup = (ViewGroup) ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(remoteViews.getLayoutId(), null);
            remoteViews.reapply(context, viewGroup);
            ArrayList a = new ScanView(TextView.class).scan(viewGroup);
            for (size = a.size() - 1; size >= 0; size--) {
                textView2 = (TextView) a.get(size);
                if (textView2.isClickable() || textView2.getVisibility() != View.VISIBLE) {
                    a.remove(size);
                    break;
                }
            }
            for (size = a.size() - 1; size >= 0; size--) {
                textView2 = (TextView) a.get(size);
                String charSequence = textView2.getText().toString();
                if (textView2.getTextSize() == 12.0f || charSequence.matches("^(\\s*|)$") || charSequence.matches("^\\d{1,2}:\\d{1,2}(\\s?\\w{2}|)$")) {
                    a.remove(size);
                }
            }
            if (a.size() != 0) {
                Iterator it = a.iterator();
                while (it.hasNext()) {
                    textView2 = (TextView) it.next();
                    if (textView != null && textView2.getTextSize() <= textView.getTextSize()) {
                        textView2 = textView;
                    }
                    textView = textView2;
                }
                a.remove(textView);
                if (wan.title != null || wan.title.equals("")) {
                    wan.title = (String) textView.getText();
                }
                if (a.size() != 0) {
                    size = a.size();
                    List<String> arrayList = new ArrayList();
                    for (int i = 0; i < size; i++) {
                        arrayList.add((String) ((TextView) a.get(i)).getText());
                    }
                    wan.messages = arrayList;
                }
            }
        } catch (Throwable e) {
            Log.e(TAG, "Failed while trying to read from views", e);
        }
    }

    public final AppNotification extract(Context context, Notification notification) {
        AppNotification wan = new AppNotification();
        Bundle a = getBundle(notification);
        if (a != null) {
            try {
                if (Build.VERSION.SDK_INT >= 19) {
                    wan.title = (String) a.getCharSequence("android.title");
                    CharSequence charSequence = a.getCharSequence("android.text");
                    CharSequence[] charSequenceArray = a.getCharSequenceArray("android.textLines");
                    if (charSequenceArray == null || charSequenceArray.length <= 0) {
                        if (charSequence != null) {
                            if (!TextUtils.isEmpty(charSequence)) {
                                List<String> asList = Arrays.asList((charSequence.toString()).split("\n"));
                                new StringBuilder("Set the messages from the EXTRA_TEXT: ").append(asList);
                                wan.messages = asList;
                            }
                        }
                    } else if (Build.VERSION.SDK_INT >= 21) {
                        List<String> arrayList = new ArrayList<>();
                        for (CharSequence charSequence2 : charSequenceArray) {
                            arrayList.add(charSequence2.toString());
                        }
                        new StringBuilder("Set the messages from the EXTRA_TEXT_LINES: ").append(arrayList);
                        wan.messages = arrayList;
                    } else {
                        Log.w(TAG, "The extras \"text lines\" is not empty. Probably this is the follow-up notificationt that we can discard: " + Arrays.asList(charSequenceArray));
                    }
                }
            } catch (Throwable e) {
                Log.w(TAG, "No luck with extras, let's see with some hardcore", e);
            }
        }
        if (TextUtils.isEmpty(wan.title) || wan.messages == null || wan.messages.size() == 0) {
            try {
                readFromViews(wan, context, notification);
            } catch (Throwable e2) {
                Log.w(TAG, "No luck with hardcore extracting notification data, nothing to do here :(", e2);
            }
        }
        return wan;
    }

    public final String getName() {
        return "Jedi";
    }

    public static class ScanView<T extends View> {
        private final ArrayList<T> list = new ArrayList<>();
        private final Class<T> clazz;

        public ScanView(Class<T> cls) {
            this.clazz = cls;
        }

        public final ArrayList<T> scan(ViewGroup viewGroup) {
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (childAt != null) {
                    if (this.clazz.isAssignableFrom(childAt.getClass())) {
                        this.list.add((T) childAt);
                    } else if (childAt instanceof ViewGroup) {
                        scan((ViewGroup) childAt);
                    }
                }
            }
            return this.list;
        }
    }
}

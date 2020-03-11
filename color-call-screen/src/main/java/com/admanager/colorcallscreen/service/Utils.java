package com.admanager.colorcallscreen.service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;

import com.admanager.colorcallscreen.model.ContactBean;

public class Utils {
    public static ContactBean readContact(Context context, String number) {
        ContactBean contactBean = new ContactBean(null, number, "number", null);
        Cursor contactLookup = null;
        try {
            Uri uri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(number));

            ContentResolver contentResolver = context.getContentResolver();
            contactLookup = contentResolver.query(uri, new String[]{
                            BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.PHOTO_URI},
                    null, null, null);

            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                String photo = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                String name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                String id = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
                Log.d("XXXXXY2", "id: " + id + " name:" + name);
                return new ContactBean(id, name, number, photo);

            }
        } catch (Throwable ignore) {
            ignore.printStackTrace();
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return contactBean;
    }

    public static ContactBean findContactById(Context context, String contactId) {
        Cursor contactLookup = null;
        try {
            contactLookup = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{
                            BaseColumns._ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.PHOTO_URI, ContactsContract.CommonDataKinds.Phone.NUMBER},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[]{contactId}, null);

            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                String photo = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                String name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                String number = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String id = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                Log.d("XXXXXY3", "id: " + id + " name:" + name);
                return new ContactBean(id, name, number, photo);

            }
        } catch (Throwable ignore) {
            ignore.printStackTrace();
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return null;
    }
}
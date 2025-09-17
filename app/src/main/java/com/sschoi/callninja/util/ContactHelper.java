package com.sschoi.callninja.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactHelper {

    public static boolean isContact(Context context, String number) {
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
                new String[]{number},
                null
        );

        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    public static boolean isFavorite(Context context, String number) {
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.STARRED, ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
                new String[]{number},
                null
        );

        boolean starred = false;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                starred = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.STARRED)) > 0;
            }
            cursor.close();
        }
        return starred;
    }
}

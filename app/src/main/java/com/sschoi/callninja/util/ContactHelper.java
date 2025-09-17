package com.sschoi.callninja.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactHelper {

    // 연락처인지 확인
    public static boolean isContact(Context context, String number) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.NUMBER}, null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        return exists;
    }

    // 즐겨찾기인지 확인 (STARRED 컬럼 사용)
    public static boolean isFavorite(Context context, String number) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.STARRED}, null, null, null);
        boolean favorite = false;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int starred = cursor.getInt(cursor.getColumnIndex(ContactsContract.PhoneLookup.STARRED));
                favorite = starred > 0;
            }
            cursor.close();
        }
        return favorite;
    }
}

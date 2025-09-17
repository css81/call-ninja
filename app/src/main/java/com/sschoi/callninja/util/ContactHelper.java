package com.sschoi.callninja.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.HashSet;
import java.util.Set;

/**
 * Helper class to access the device contacts and favorite numbers.
 */
public class ContactHelper {

    private Context context;

    public ContactHelper(Context context) {
        this.context = context;
    }

    /**
     * Get all phone numbers from the device contacts.
     *
     * @return Set of phone numbers
     */
    public Set<String> getAllContactNumbers() {
        Set<String> numbers = new HashSet<>();
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                null, null, null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(cursor.getColumnIndexOrThrow(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                numbers.add(cleanNumber(number));
            }
            cursor.close();
        }

        return numbers;
    }

    /**
     * Get all favorite (starred) phone numbers from the device contacts.
     *
     * @return Set of favorite phone numbers
     */
    public Set<String> getFavoriteNumbers() {
        Set<String> numbers = new HashSet<>();
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.STARRED + "=?",
                new String[]{"1"},
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(cursor.getColumnIndexOrThrow(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                numbers.add(cleanNumber(number));
            }
            cursor.close();
        }

        return numbers;
    }

    /**
     * Normalize phone numbers by removing spaces, dashes, and other non-numeric characters.
     */
    private String cleanNumber(String number) {
        if (number == null) return "";
        return number.replaceAll("[^\\d+]", "");
    }
}

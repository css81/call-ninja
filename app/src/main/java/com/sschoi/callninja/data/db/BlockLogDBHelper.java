package com.sschoi.callninja.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.sschoi.callninja.data.model.BlockLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SQLite DB Helper for storing blocked call logs.
 */
public class BlockLogDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "blocklog.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "BlockLog";

    public BlockLogDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "phoneNumber TEXT, " +
                "blockTime TEXT" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Insert a new blocked call log into the database.
     */
    public void insertLog(String phoneNumber) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phoneNumber", phoneNumber);
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        values.put("blockTime", timeStamp);
        db.insert(TABLE_NAME, null, values);
    }

    /**
     * Retrieve all blocked call logs.
     */
    public List<BlockLog> getAllLogs() {
        List<BlockLog> logs = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, phoneNumber, blockTime FROM " + TABLE_NAME + " ORDER BY id DESC", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String phone = cursor.getString(1);
                String time = cursor.getString(2);
                logs.add(new BlockLog(id, phone, time));
            }
            cursor.close();
        }
        return logs;
    }
}

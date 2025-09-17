package com.sschoi.callninja.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlockLogDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "block_log.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "block_log";

    public BlockLogDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, number TEXT, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // 차단 로그 추가
    public void insertBlockLog(String number) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // 최근 차단 기록 가져오기
    public Cursor getAllBlockedLogs() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, "timestamp DESC");
    }
}

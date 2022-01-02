package com.example.receipt_test;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class myDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "MY_RECEIPT";
    private static final int DB_VERSION = 1;

    public myDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 建立資料庫
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DB_NAME + "(" +
                "Receipt_Year integer NOT NULL, " +
                "Receipt_Month text NOT NULL, " +
                "Receipt_Day text NOT NULL, " +
                "Receipt_Number text NOT NULL PRIMARY KEY," +
                "Receipt_Interval text NOT NULL)"
                );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        onCreate(db);
    }
}

package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PictureDatabase extends SQLiteOpenHelper {
    private static final String TAG = "PictureDatabase";


    public static String DATABASE_NAME = "picture.db";
    public static String TABLE_ITEM = "picture";
    public static int DATABASE_VERSION = 1;

    public PictureDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        String SQL = "create table if not exists picture("
                + "_id integer PRIMARY KEY autoincrement, "
                + "picturepath text)";
        db.execSQL(SQL);
    }

    public void onOpen(SQLiteDatabase db) {
        println("onOpen 호출됨");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        println("onUpgrade 호출됨 : " + oldVersion + " -> " + newVersion);

        if (newVersion > 1) {
            db.execSQL("DROP TABLE IF EXISTS meow");
        }
    }

    public void println(String data) {
        Log.d("DatabaseHelper", data);
    }
}

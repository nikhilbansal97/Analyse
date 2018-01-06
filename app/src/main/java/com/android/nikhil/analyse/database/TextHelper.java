package com.android.nikhil.analyse.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by NIKHIL on 02-01-2018.
 */

public class TextHelper extends SQLiteOpenHelper {

    private static final String TAG = "TextHelper";
    public static final String DATABASE_NAME = "texts.db";
    public static final int DATABASE_VERSION = 1;

    public TextHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_QUERY = "CREATE TABLE " + ContractClass.TextProvider.TABLE_NAME + " ( "
                + ContractClass.TextProvider.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ContractClass.TextProvider.COLUMN_MAIN_TEXT + " TEXT NOT NULL, "
                + ContractClass.TextProvider.COLUMN_EMOTION + " TEXT, "
                + ContractClass.TextProvider.COLUMN_ENTITY + " TEXT, "
                + ContractClass.TextProvider.COLUMN_CONCEPT + " TEXT);";
        db.execSQL(CREATE_QUERY);
        Log.d(TAG, "onCreate: Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

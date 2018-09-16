package com.android.nikhil.analyse.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * Created by NIKHIL on 02-01-2018.
 */

class TextHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

  override fun onCreate(db: SQLiteDatabase) {
    val CREATE_QUERY = ("CREATE TABLE " + ContractClass.TextProvider.TABLE_NAME + " ( "
        + ContractClass.TextProvider.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + ContractClass.TextProvider.COLUMN_MAIN_TEXT + " TEXT NOT NULL, "
        + ContractClass.TextProvider.COLUMN_EMOTION + " TEXT, "
        + ContractClass.TextProvider.COLUMN_ENTITY + " TEXT, "
        + ContractClass.TextProvider.COLUMN_CONCEPT + " TEXT);")
    db.execSQL(CREATE_QUERY)
    Log.d(TAG, "onCreate: Table created")
  }

  override fun onUpgrade(
    db: SQLiteDatabase,
    oldVersion: Int,
    newVersion: Int
  ) {
  }

  companion object {

    private val TAG = "TextHelper"
    val DATABASE_NAME = "texts.db"
    val DATABASE_VERSION = 1
  }
}

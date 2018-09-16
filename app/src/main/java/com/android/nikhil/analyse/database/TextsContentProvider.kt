package com.android.nikhil.analyse.database

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.util.Log

/**
 * Created by NIKHIL on 02-01-2018.
 */

class TextsContentProvider : ContentProvider() {
  private var mHelper: TextHelper? = null

  override fun onCreate(): Boolean {
    mHelper = TextHelper(context)
    return true
  }

  override fun query(
    uri: Uri,
    projection: Array<String>?,
    selection: String?,
    selectionArgs: Array<String>?,
    sortOrder: String?
  ): Cursor? {
    var selection = selection
    var selectionArgs = selectionArgs

    val cursor: Cursor
    val database = mHelper!!.readableDatabase

    val code = matcher.match(uri)

    when (code) {
      CODE_DIR -> cursor = database.query(
          ContractClass.TextProvider.TABLE_NAME,
          projection, null, null, null, null, null
      )
      CODE_ITEM -> {
        selection = ContractClass.TextProvider.COLUMN_ID + "=?"
        selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
        cursor = database.query(
            ContractClass.TextProvider.TABLE_NAME,
            projection, selection, selectionArgs, null, null, null
        )
      }
      else -> throw IllegalArgumentException("Invalid uri : " + uri.toString())
    }
    cursor.setNotificationUri(context!!.contentResolver, uri)
    Log.d(TAG, "query: data queried.")
    return cursor
  }

  override fun getType(uri: Uri): String? {
    val code = matcher.match(uri)
    return when (code) {
      CODE_DIR -> ContractClass.CONTENT_DIR_TYPE
      CODE_ITEM -> ContractClass.CONTENT_ITEM_TYPE
      else -> throw IllegalArgumentException("Invalid Uri : " + uri.toString())
    }
  }

  override fun insert(
    uri: Uri,
    values: ContentValues?
  ): Uri? {
    val database = mHelper!!.writableDatabase
    val id = database.insert(ContractClass.TextProvider.TABLE_NAME, null, values)
    return if (id.toInt() != -1)
      ContentUris.withAppendedId(Uri.withAppendedPath(ContractClass.BASE_URI, ContractClass.TextProvider.PATH_DIR), id)
    else
      throw IllegalArgumentException("Failed to insert for uri:" + uri.toString())
  }

  override fun delete(
    uri: Uri,
    selection: String?,
    selectionArgs: Array<String>?
  ): Int {
    var selection = selection
    var selectionArgs = selectionArgs
    val database = mHelper!!.writableDatabase
    val code = matcher.match(uri)
    val row: Int
    when (code) {
      CODE_DIR -> row = database.delete(ContractClass.TextProvider.TABLE_NAME, selection, selectionArgs)
      CODE_ITEM -> {
        selection = ContractClass.TextProvider.COLUMN_ID + "=?"
        selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
        row = database.delete(ContractClass.TextProvider.TABLE_NAME, selection, selectionArgs)
      }
      else -> throw IllegalArgumentException("Failed to delete data for uri:" + uri.toString())
    }
    return row
  }

  override fun update(
    uri: Uri,
    values: ContentValues?,
    selection: String?,
    selectionArgs: Array<String>?
  ): Int {
    var selection = selection
    var selectionArgs = selectionArgs
    val database = mHelper!!.writableDatabase
    val code = matcher.match(uri)
    val row: Int
    when (code) {
      CODE_DIR -> row = database.update(ContractClass.TextProvider.TABLE_NAME, values, selection, selectionArgs)
      CODE_ITEM -> {
        selection = ContractClass.TextProvider.COLUMN_ID + "=?"
        selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
        row = database.update(ContractClass.TextProvider.TABLE_NAME, values, selection, selectionArgs)
      }
      else -> throw IllegalArgumentException("Failed to update for uri:" + uri.toString())
    }
    return row
  }

  companion object {

    private const val TAG = "TextsContentProvider"
    private val matcher = UriMatcher(UriMatcher.NO_MATCH)

    private const val CODE_ITEM = 1000
    private const val CODE_DIR = 1001

    init {
      matcher.addURI(ContractClass.AUTHORITY, ContractClass.TextProvider.PATH_ITEM, CODE_ITEM)
      matcher.addURI(ContractClass.AUTHORITY, ContractClass.TextProvider.PATH_DIR, CODE_DIR)
    }
  }
}

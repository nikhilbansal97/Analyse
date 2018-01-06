package com.android.nikhil.analyse.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by NIKHIL on 02-01-2018.
 */

public class TextsContentProvider extends ContentProvider {

    private static final String TAG = "TextsContentProvider";
    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private TextHelper mHelper;
    private Context context;

    private static final int CODE_ITEM = 1000;
    private static final int CODE_DIR = 1001;

    static {
        matcher.addURI(ContractClass.AUTHORITY, ContractClass.TextProvider.PATH_ITEM, CODE_ITEM);
        matcher.addURI(ContractClass.AUTHORITY, ContractClass.TextProvider.PATH_DIR, CODE_DIR);
    }

    @Override
    public boolean onCreate() {
        context = getContext();
        mHelper = new TextHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;
        SQLiteDatabase database = mHelper.getReadableDatabase();

        int code = matcher.match(uri);

        switch (code) {
            case CODE_DIR:
                cursor = database.query(ContractClass.TextProvider.TABLE_NAME,
                        projection, null, null, null, null, null);
                break;
            case CODE_ITEM:
                selection = ContractClass.TextProvider.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ContractClass.TextProvider.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, null);
                break;
            default:
                throw new IllegalArgumentException("Invalid uri : " + uri.toString());
        }
        cursor.setNotificationUri(context.getContentResolver(), uri);
        Log.d(TAG, "query: data queried.");
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int code = matcher.match(uri);
        switch (code) {
            case CODE_DIR:
                return ContractClass.CONTENT_DIR_TYPE;
            case CODE_ITEM:
                return ContractClass.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Invalid Uri : " + uri.toString());
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        Long id = database.insert(ContractClass.TextProvider.TABLE_NAME, null, values);
        if (id != -1)
            return ContentUris.withAppendedId(Uri.withAppendedPath(ContractClass.BASE_URI, ContractClass.TextProvider.PATH_DIR), id);
        else
            throw new IllegalArgumentException("Failed to insert for uri:" + uri.toString());
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        int code = matcher.match(uri);
        int row;
        switch (code) {
            case CODE_DIR:
                row = database.delete(ContractClass.TextProvider.TABLE_NAME, selection, selectionArgs);
                break;
            case CODE_ITEM:
                selection = ContractClass.TextProvider.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                row = database.delete(ContractClass.TextProvider.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Failed to delete data for uri:" + uri.toString());
        }
        return row;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        int code = matcher.match(uri);
        int row;
        switch (code) {
            case CODE_DIR:
                row = database.update(ContractClass.TextProvider.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CODE_ITEM:
                selection = ContractClass.TextProvider.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                row = database.update(ContractClass.TextProvider.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Failed to update for uri:" + uri.toString());
        }
        return row;
    }
}

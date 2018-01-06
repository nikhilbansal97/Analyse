package com.android.nikhil.analyse.widget;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.android.nikhil.analyse.R;
import com.android.nikhil.analyse.database.ContractClass;

/**
 * Created by NIKHIL on 05-01-2018.
 */

public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "WidgetRemoteViewsFactor";
    private Context context;
    private Cursor cursor;
    String[] projections = new String[]{
            ContractClass.TextProvider.COLUMN_ID,
            ContractClass.TextProvider.COLUMN_ENTITY,
            ContractClass.TextProvider.COLUMN_CONCEPT,
            ContractClass.TextProvider.COLUMN_EMOTION,
            ContractClass.TextProvider.COLUMN_MAIN_TEXT
    };

    public WidgetRemoteViewsFactory(Context context) {
        this.context = context;
        Log.d(TAG, "WidgetRemoteViewsFactory: constructor");
    }

    @Override
    public void onCreate() {
        cursor = context.getContentResolver().query(
                Uri.withAppendedPath(ContractClass.BASE_URI, ContractClass.TextProvider.PATH_DIR),
                projections,
                null,
                null,
                null);
        cursor.moveToPosition(0);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onDataSetChanged() {
        Log.d(TAG, "onDataSetChanged: ");
        cursor.close();
        cursor = null;
        cursor = context.getContentResolver().query(Uri.withAppendedPath(ContractClass.BASE_URI, ContractClass.TextProvider.PATH_DIR),
                projections,
                null,
                null,
                null);
        cursor.moveToPosition(0);
    }

    @Override
    public void onDestroy() {
        cursor.close();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public int getCount() {
        Log.d(TAG, "getCount: ");
        if (cursor == null)
            return 0;
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.list_item);
        String mainText = "", entity = "", emotion = "", concept = "";
        if (cursor != null) {
            cursor.moveToPosition(position);
            mainText = cursor.getString(cursor.getColumnIndex(ContractClass.TextProvider.COLUMN_MAIN_TEXT));
            entity = cursor.getString(cursor.getColumnIndex(ContractClass.TextProvider.COLUMN_ENTITY));
            emotion = cursor.getString(cursor.getColumnIndex(ContractClass.TextProvider.COLUMN_EMOTION));
            concept = cursor.getString(cursor.getColumnIndex(ContractClass.TextProvider.COLUMN_CONCEPT));
        }
        view.setTextViewText(R.id.listItemMainText, mainText);
        view.setTextViewText(R.id.listItemAnalysis, emotion + "\n" + concept + "\n" + entity);
        Log.d(TAG, "getViewAt: ");
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        Log.d(TAG, "getLoadingView: ");
        return null;
    }

    @Override
    public int getViewTypeCount() {
        Log.d(TAG, "getViewTypeCount: ");
        return 1;
    }

    @Override
    public long getItemId(int position) {
        Log.d(TAG, "getItemId: ");
        return position;
    }

    @Override
    public boolean hasStableIds() {
        Log.d(TAG, "hasStableIds: ");
        return true;
    }
}

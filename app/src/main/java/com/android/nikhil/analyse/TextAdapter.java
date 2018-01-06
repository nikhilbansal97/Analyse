package com.android.nikhil.analyse;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.nikhil.analyse.database.ContractClass;

/**
 * Created by NIKHIL on 02-01-2018.
 */

public class TextAdapter extends CursorAdapter {

    public TextAdapter(Context context, Cursor c) {
        super(context, c, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String text = cursor.getString(cursor.getColumnIndex(ContractClass.TextProvider.COLUMN_MAIN_TEXT));
        String emotions = cursor.getString(cursor.getColumnIndex(ContractClass.TextProvider.COLUMN_EMOTION));
        String entity = cursor.getString(cursor.getColumnIndex(ContractClass.TextProvider.COLUMN_ENTITY));
        String concept = cursor.getString(cursor.getColumnIndex(ContractClass.TextProvider.COLUMN_CONCEPT));

        TextView mainTextView = view.findViewById(R.id.listItemMainText);
        mainTextView.setText(text);

        TextView analysisTextView = view.findViewById(R.id.listItemAnalysis);
        analysisTextView.setText(emotions + "\n" + entity + "\n" + concept);
    }
}

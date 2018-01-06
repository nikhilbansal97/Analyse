package com.android.nikhil.analyse.activities;

import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.nikhil.analyse.R;
import com.android.nikhil.analyse.TextAdapter;
import com.android.nikhil.analyse.database.ContractClass;

public class HistoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView historyListView;
    private TextAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        historyListView = findViewById(R.id.historyListView);
        adapter = new TextAdapter(getApplicationContext(), null);
        historyListView.setAdapter(adapter);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.delete:
                deleteHistory();
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return true;
    }

    private void deleteHistory() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setPositiveButton(R.string.alert_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getContentResolver().delete(Uri.withAppendedPath(ContractClass.BASE_URI, ContractClass.TextProvider.PATH_DIR),
                                null, null);
                        getContentResolver().notifyChange(
                                Uri.withAppendedPath(ContractClass.BASE_URI, ContractClass.TextProvider.PATH_DIR),
                                null);
                    }
                })
                .setNegativeButton(R.string.alert_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setTitle(R.string.delete)
                .setMessage(R.string.delete_message);

        dialogBuilder.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projections = new String[]{
                ContractClass.TextProvider.COLUMN_ID,
                ContractClass.TextProvider.COLUMN_MAIN_TEXT,
                ContractClass.TextProvider.COLUMN_ENTITY,
                ContractClass.TextProvider.COLUMN_EMOTION,
                ContractClass.TextProvider.COLUMN_CONCEPT
        };
        return new CursorLoader(getApplicationContext(),
                Uri.withAppendedPath(ContractClass.BASE_URI, ContractClass.TextProvider.PATH_DIR),
                projections, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}

package com.android.nikhil.analyse.activities

import android.database.Cursor
import android.net.Uri
import android.support.v4.app.LoaderManager
import android.support.v4.app.NavUtils
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.android.nikhil.analyse.R
import com.android.nikhil.analyse.TextAdapter
import com.android.nikhil.analyse.database.ContractClass
import kotlinx.android.synthetic.main.activity_history.historyListView

class HistoryActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

  private var adapter: TextAdapter? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_history)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    adapter = TextAdapter(applicationContext, null)
    historyListView.adapter = adapter
    supportLoaderManager.initLoader(0, null, this)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.history_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val id = item.itemId
    when (id) {
      R.id.delete -> deleteHistory()
      android.R.id.home -> NavUtils.navigateUpFromSameTask(this)
    }
    return true
  }

  private fun deleteHistory() {
    val dialogBuilder = AlertDialog.Builder(this)
        .setCancelable(true)
        .setPositiveButton(R.string.alert_yes) { dialog, which ->
          contentResolver.delete(Uri.withAppendedPath(ContractClass.BASE_URI, ContractClass.TextProvider.PATH_DIR), null, null)
          contentResolver.notifyChange(
              Uri.withAppendedPath(ContractClass.BASE_URI, ContractClass.TextProvider.PATH_DIR), null
          )
        }
        .setNegativeButton(R.string.alert_no) { dialog, which -> dialog.dismiss() }
        .setTitle(R.string.delete)
        .setMessage(R.string.delete_message)

    dialogBuilder.show()
  }

  override fun onCreateLoader(
    id: Int,
    args: Bundle
  ): Loader<Cursor> {
    val projections = arrayOf(
        ContractClass.TextProvider.COLUMN_ID, ContractClass.TextProvider.COLUMN_MAIN_TEXT, ContractClass.TextProvider.COLUMN_ENTITY,
        ContractClass.TextProvider.COLUMN_EMOTION, ContractClass.TextProvider.COLUMN_CONCEPT
    )
    return CursorLoader(
        applicationContext,
        Uri.withAppendedPath(ContractClass.BASE_URI, ContractClass.TextProvider.PATH_DIR),
        projections, null, null, null
    )
  }

  override fun onLoadFinished(
    loader: Loader<Cursor>,
    data: Cursor
  ) {
    adapter!!.swapCursor(data)
    adapter!!.notifyDataSetChanged()
  }

  override fun onLoaderReset(loader: Loader<Cursor>) {
    adapter!!.swapCursor(null)
  }
}

package com.android.nikhil.analyse.widget

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService

import com.android.nikhil.analyse.R
import com.android.nikhil.analyse.database.ContractClass

/**
 * Created by NIKHIL on 05-01-2018.
 */

class WidgetRemoteViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
  private var cursor: Cursor? = null
  private var projections = arrayOf(
      ContractClass.TextProvider.COLUMN_ID, ContractClass.TextProvider.COLUMN_ENTITY, ContractClass.TextProvider.COLUMN_CONCEPT,
      ContractClass.TextProvider.COLUMN_EMOTION, ContractClass.TextProvider.COLUMN_MAIN_TEXT
  )

  override fun onCreate() {
    cursor = context.contentResolver.query(
        Uri.withAppendedPath(ContractClass.BASE_URI, ContractClass.TextProvider.PATH_DIR),
        projections, null, null, null
    )
    cursor!!.moveToPosition(0)
  }

  override fun onDataSetChanged() {
    cursor!!.close()
    cursor = null
    cursor = context.contentResolver.query(
        Uri.withAppendedPath(ContractClass.BASE_URI, ContractClass.TextProvider.PATH_DIR),
        projections, null, null, null
    )
    cursor!!.moveToPosition(0)
  }

  override fun onDestroy() {
    if (cursor != null)
      cursor?.close()
  }

  override fun getCount(): Int {
    return if (cursor == null) 0 else cursor!!.count
  }

  override fun getViewAt(position: Int): RemoteViews {
    val view = RemoteViews(context.packageName, R.layout.list_item)
    var mainText = ""
    var entity = ""
    var emotion = ""
    var concept = ""
    if (cursor != null) {
      cursor!!.moveToPosition(position)
      mainText = cursor!!.getString(cursor!!.getColumnIndex(ContractClass.TextProvider.COLUMN_MAIN_TEXT))
      entity = cursor!!.getString(cursor!!.getColumnIndex(ContractClass.TextProvider.COLUMN_ENTITY))
      emotion = cursor!!.getString(cursor!!.getColumnIndex(ContractClass.TextProvider.COLUMN_EMOTION))
      concept = cursor!!.getString(cursor!!.getColumnIndex(ContractClass.TextProvider.COLUMN_CONCEPT))
    }
    view.setTextViewText(R.id.listItemMainText, mainText)
    view.setTextViewText(R.id.listItemAnalysis, emotion + "\n" + concept + "\n" + entity)
    return view
  }

  override fun getLoadingView(): RemoteViews? = null

  override fun getViewTypeCount(): Int = 1

  override fun getItemId(position: Int): Long = position.toLong()

  override fun hasStableIds(): Boolean = true

}

package com.android.nikhil.analyse

import android.content.Context
import android.database.Cursor
import android.support.v4.widget.CursorAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.nikhil.analyse.database.ContractClass
import kotlinx.android.synthetic.main.list_item.view.listItemAnalysis
import kotlinx.android.synthetic.main.list_item.view.listItemMainText

/**
 * Created by NIKHIL on 02-01-2018.
 */

class TextAdapter(
  context: Context,
  c: Cursor?
) : CursorAdapter(context, c, true) {

  override fun newView(
    context: Context,
    cursor: Cursor,
    parent: ViewGroup
  ): View {
    return LayoutInflater.from(context)
        .inflate(R.layout.list_item, parent, false)
  }

  override fun bindView(
    view: View,
    context: Context,
    cursor: Cursor
  ) {
    val text = cursor.getString(cursor.getColumnIndex(ContractClass.TextProvider.COLUMN_MAIN_TEXT))
    val emotions = cursor.getString(cursor.getColumnIndex(ContractClass.TextProvider.COLUMN_EMOTION))
    val entity = cursor.getString(cursor.getColumnIndex(ContractClass.TextProvider.COLUMN_ENTITY))
    val concept = cursor.getString(cursor.getColumnIndex(ContractClass.TextProvider.COLUMN_CONCEPT))
    view.listItemMainText.text = text
    view.listItemAnalysis.text = "$emotions/n$entity/n$concept"
  }
}

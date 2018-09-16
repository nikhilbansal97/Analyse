package com.android.nikhil.analyse.database

import android.content.ContentResolver
import android.net.Uri
import android.provider.BaseColumns

/**
 * Created by NIKHIL on 02-01-2018.
 */

object ContractClass {

  const val AUTHORITY = "com.android.nikhil.analyse.database"
  val BASE_URI = Uri.parse("content://$AUTHORITY")
  const val CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY
  const val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY

  class TextProvider : BaseColumns {
    companion object {

      const val TABLE_NAME = "texts"

      const val PATH_DIR = "texts"
      const val PATH_ITEM = "texts/#"

      const val COLUMN_ID = BaseColumns._ID
      const val COLUMN_MAIN_TEXT = "text"
      const val COLUMN_EMOTION = "emotion"
      const val COLUMN_ENTITY = "entity"
      const val COLUMN_CONCEPT = "concept"
    }

  }

}

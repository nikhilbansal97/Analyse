package com.android.nikhil.analyse.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by NIKHIL on 02-01-2018.
 */

public class ContractClass {

    public static final String AUTHORITY = "com.android.nikhil.analyse.database";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY;

    public static class TextProvider implements BaseColumns {

        public static final String TABLE_NAME = "texts";

        public static final String PATH_DIR = "texts";
        public static final String PATH_ITEM = "texts/#";

        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_MAIN_TEXT = "text";
        public static final String COLUMN_EMOTION = "emotion";
        public static final String COLUMN_ENTITY = "entity";
        public static final String COLUMN_CONCEPT = "concept";

    }

}

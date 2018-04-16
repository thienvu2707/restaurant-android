package com.android.thienvu.restaurants.Database;

import android.provider.BaseColumns;

public class DatabaseContact {

    public DatabaseContact() {
    }

    public static final class DatabaseEntry implements BaseColumns {
        public static final String TABLE_NAME = "restaurant";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_USER_ID = "userID";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_IMAGE = "image";

        public static final String CREATE_TABLE_RESTAURANT = "CREATE TABLE " +
                DatabaseEntry.TABLE_NAME + " (" +
                DatabaseEntry._ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
                DatabaseEntry.COLUMN_USER_ID + " TEXT NOT NULL," +
                DatabaseEntry.COLUMN_NAME + " TEXT NOT NULL," +
                DatabaseEntry.COLUMN_LOCATION + " TEXT NOT NULL," +
                DatabaseEntry.COLUMN_PRICE + " TEXT NOT NULL," +
                DatabaseEntry.COLUMN_RATING + " INTEGER NOT NULL DEFAULT 0," +
                DatabaseEntry.COLUMN_IMAGE + " TEXT NOT NULL);";
    }
}
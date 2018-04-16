package com.android.thienvu.restaurants.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public final static String DATABASE_NAME = "restaurant.db";
    public final static int DATABASE_VERSION = 1;
    public final static String LOG_TAG = DatabaseHelper.class.getName();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContact.DatabaseEntry.CREATE_TABLE_RESTAURANT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertItem(DatabaseItem item) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContact.DatabaseEntry.COLUMN_USER_ID, item.getUserID());
        values.put(DatabaseContact.DatabaseEntry.COLUMN_NAME, item.getRestaurantName());
        values.put(DatabaseContact.DatabaseEntry.COLUMN_LOCATION, item.getLocation());
        values.put(DatabaseContact.DatabaseEntry.COLUMN_PRICE, item.getPrice());
        values.put(DatabaseContact.DatabaseEntry.COLUMN_RATING, item.getRating());
        values.put(DatabaseContact.DatabaseEntry.COLUMN_IMAGE, item.getImage());

        long id = database.insert(DatabaseContact.DatabaseEntry.TABLE_NAME, null, values);
    }

    public Cursor readDatabase() {
        SQLiteDatabase database = getReadableDatabase();
        String[] projection = {
                DatabaseContact.DatabaseEntry._ID,
                DatabaseContact.DatabaseEntry.COLUMN_USER_ID,
                DatabaseContact.DatabaseEntry.COLUMN_NAME,
                DatabaseContact.DatabaseEntry.COLUMN_LOCATION,
                DatabaseContact.DatabaseEntry.COLUMN_PRICE,
                DatabaseContact.DatabaseEntry.COLUMN_RATING,
                DatabaseContact.DatabaseEntry.COLUMN_IMAGE
        };

        Cursor cursor = database.query(DatabaseContact.DatabaseEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    public Cursor readItem (long itemId)
    {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                DatabaseContact.DatabaseEntry._ID,
                DatabaseContact.DatabaseEntry.COLUMN_USER_ID,
                DatabaseContact.DatabaseEntry.COLUMN_NAME,
                DatabaseContact.DatabaseEntry.COLUMN_LOCATION,
                DatabaseContact.DatabaseEntry.COLUMN_PRICE,
                DatabaseContact.DatabaseEntry.COLUMN_RATING,
                DatabaseContact.DatabaseEntry.COLUMN_IMAGE
        };

        String selection = DatabaseContact.DatabaseEntry._ID + "=?";
        String[] selectionArgs = new String[] {String.valueOf(itemId)};

        Cursor cursor = db.query(
                DatabaseContact.DatabaseEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        return cursor;
    }

    public void updateItem(long currentItemId, String userID, String name, String location, String price, int rating)
    {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContact.DatabaseEntry.COLUMN_USER_ID, userID);
        values.put(DatabaseContact.DatabaseEntry.COLUMN_NAME, name);
        values.put(DatabaseContact.DatabaseEntry.COLUMN_LOCATION, location);
        values.put(DatabaseContact.DatabaseEntry.COLUMN_PRICE, price);
        values.put(DatabaseContact.DatabaseEntry.COLUMN_RATING, rating);

        String selection = DatabaseContact.DatabaseEntry._ID + "=?";
        String[] selectionArgs = new String[] {String.valueOf(currentItemId)};
        database.update(DatabaseContact.DatabaseEntry.TABLE_NAME, values, selection, selectionArgs);
    }
}

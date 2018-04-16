package com.android.thienvu.restaurants;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.thienvu.restaurants.Database.DatabaseContact;

public class DatabaseCursorAdapter extends CursorAdapter {

    private final MainActivity activity;

    public DatabaseCursorAdapter(MainActivity context, Cursor cursor)
    {
        super(context, cursor, 0);
        this.activity = context;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_detail_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView userTextView = (TextView) view.findViewById(R.id.userID);
        TextView restaurantTextView = (TextView) view.findViewById(R.id.restaurant_name);
        TextView locationTextView = (TextView) view.findViewById(R.id.location);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView ratingTextView = (TextView) view.findViewById(R.id.rating);
        ImageView image = (ImageView) view.findViewById(R.id.image_view);

        String userID = cursor.getString(cursor.getColumnIndex(DatabaseContact.DatabaseEntry.COLUMN_USER_ID));
        String restaurant = cursor.getString(cursor.getColumnIndex(DatabaseContact.DatabaseEntry.COLUMN_NAME));
        String location = cursor.getString(cursor.getColumnIndex(DatabaseContact.DatabaseEntry.COLUMN_LOCATION));
        String price = cursor.getString(cursor.getColumnIndex(DatabaseContact.DatabaseEntry.COLUMN_PRICE));
        final int rating = cursor.getInt(cursor.getColumnIndex(DatabaseContact.DatabaseEntry.COLUMN_RATING));
        image.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(DatabaseContact.DatabaseEntry.COLUMN_IMAGE))));

        userTextView.setText(userID);
        restaurantTextView.setText(restaurant);
        locationTextView.setText(location);
        priceTextView.setText(price);
        ratingTextView.setText(String.valueOf(rating));

        final long id = cursor.getLong(cursor.getColumnIndex(DatabaseContact.DatabaseEntry._ID));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.clickOnViewReview(id);
            }
        });
    }
}

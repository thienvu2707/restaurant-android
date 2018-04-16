package com.android.thienvu.restaurants;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.android.thienvu.restaurants.Database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = MainActivity.class.getName();
    DatabaseHelper dbHelper;
    DatabaseCursorAdapter adapter;
    int lastVisibleReview = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });

        final ListView listView = (ListView) findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        Cursor cursor = dbHelper.readDatabase();

        adapter = new DatabaseCursorAdapter(this, cursor);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) return;
                final int currentFirstVisibleReview = view.getFirstVisiblePosition();
                if (currentFirstVisibleReview > lastVisibleReview) {
                    fab.show();
                } else if (currentFirstVisibleReview < lastVisibleReview) {
                    fab.hide();
                }
                lastVisibleReview = currentFirstVisibleReview;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.swapCursor(dbHelper.readDatabase());
    }

    public void clickOnViewReview(long id) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("itemId", id);
        startActivity(intent);
    }
}
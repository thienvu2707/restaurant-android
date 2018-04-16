package com.android.thienvu.restaurants;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.thienvu.restaurants.Database.DatabaseContact;
import com.android.thienvu.restaurants.Database.DatabaseHelper;
import com.android.thienvu.restaurants.Database.DatabaseItem;

public class DetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailsActivity.class.getName();
    private static final int PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 1;
    private DatabaseHelper dbHelper;
    EditText userIdEdit;
    EditText nameEdit;
    EditText locationEdit;
    EditText priceEdit;
    EditText ratingEdit;
    ImageButton decreaseRating;
    ImageButton increaseRating;
    Button imageBtn;
    ImageView imageView;
    private static final int IMAGE_REQUEST = 0;
    Uri actualUri;
    long currentItemId;
    Boolean infoReviewHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        userIdEdit = (EditText) findViewById(R.id.userID_edit);
        nameEdit = (EditText) findViewById(R.id.restaurant_name_edit);
        locationEdit = (EditText) findViewById(R.id.location_edit);
        priceEdit = (EditText) findViewById(R.id.price_edit);
        ratingEdit = (EditText) findViewById(R.id.rating_edit);
        decreaseRating = (ImageButton) findViewById(R.id.decrease_rating);
        increaseRating = (ImageButton) findViewById(R.id.increase_rating);
        imageBtn = (Button) findViewById(R.id.select_image);
        imageView = (ImageView) findViewById(R.id.image_view);

        dbHelper = new DatabaseHelper(this);
        currentItemId = getIntent().getLongExtra("itemId", 0);

        if (currentItemId == 0) {
            setTitle(getString(R.string.editor_activity_title_new_item));
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_item));
            addValuesToEditItem(currentItemId);
        }
        decreaseRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtractOneRating();
                infoReviewHasChanged = true;
            }
        });

        increaseRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOneRating();
                infoReviewHasChanged = true;
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToOpenImageSelector();
                infoReviewHasChanged = true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!infoReviewHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
        showUnsavedChangeDialog(discardButtonClickListener);
    }

    private void showUnsavedChangeDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void subtractOneRating() {
        String previousValueString = ratingEdit.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            return;
        } else if (previousValueString.equals("0")) {
            return;
        } else {
            previousValue = Integer.parseInt(previousValueString);
            ratingEdit.setText(String.valueOf(previousValue - 1));
        }
    }

    private void addOneRating() {
        String previousValueString = ratingEdit.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        ratingEdit.setText(String.valueOf(previousValue + 1));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentItemId == 0) {
            MenuItem deleteOneItemMenuReview = menu.findItem(R.id.action_delete_review);
            MenuItem deleteAllSaveData = menu.findItem(R.id.action_delete_all_data);
            deleteOneItemMenuReview.setVisible(false);
            deleteAllSaveData.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                // save item in DB
                if (!addReviewToDb()) {
                    // saying to onOptionsItemSelected that user clicked button
                    return true;
                }
                Toast.makeText(this, "Saving to database", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            case android.R.id.home:
                if (!infoReviewHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonOnClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                    }
                };
                showUnsavedChangeDialog(discardButtonOnClickListener);
                return true;
            case R.id.action_delete_review:
                showDeleteConfirmationDialog(currentItemId);
                return true;
            case R.id.action_delete_all_data:
                showDeleteConfirmationDialog(0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean addReviewToDb() {
        boolean isAllOk = true;
        if (!checkIfValueSet(userIdEdit, "UserID")) {
            isAllOk = false;
        }
        if (!checkIfValueSet(nameEdit, "name")) {
            isAllOk = false;
        }
        if (!checkIfValueSet(locationEdit, "location")) {
            isAllOk = false;
        }
        if (!checkIfValueSet(priceEdit, "price")) ;
        {
            isAllOk = false;
        }
        if (!checkIfValueSet(ratingEdit, "rating")) {
            isAllOk = false;
        }
        if (actualUri == null && currentItemId == 0) {
            isAllOk = false;
            imageBtn.setError("Missing image");
        }
        if (!isAllOk) {
            return false;
        }

        if (currentItemId == 0) {
            DatabaseItem item = new DatabaseItem(
                    userIdEdit.getText().toString().trim(),
                    nameEdit.getText().toString().trim(),
                    locationEdit.getText().toString().trim(),
                    priceEdit.getText().toString().trim(),
                    Integer.parseInt(ratingEdit.getText().toString().trim()),
                    actualUri.toString());
            dbHelper.insertItem(item);
        } else {
            String userID = userIdEdit.getText().toString().trim();
            String nameRestaurant = nameEdit.getText().toString().trim();
            String location = locationEdit.getText().toString().trim();
            String price = priceEdit.getText().toString().trim();
            int rating = Integer.parseInt(ratingEdit.getText().toString().trim());
            dbHelper.updateItem(currentItemId, userID, nameRestaurant, location, price, rating);
        }
        return true;
    }

    private boolean checkIfValueSet(EditText text, String description) {
        if (TextUtils.isEmpty(text.getText())) {
            text.setError("Missing " + description);
            return false;
        } else {
            text.setError(null);
            return true;
        }
    }

    private void addValuesToEditItem(long itemId) {
        Cursor cursor = dbHelper.readItem(itemId);
        cursor.moveToFirst();
        userIdEdit.setText(cursor.getString(cursor.getColumnIndex(DatabaseContact.DatabaseEntry.COLUMN_USER_ID)));
        nameEdit.setText(cursor.getString(cursor.getColumnIndex(DatabaseContact.DatabaseEntry.COLUMN_NAME)));
        locationEdit.setText(cursor.getString(cursor.getColumnIndex(DatabaseContact.DatabaseEntry.COLUMN_LOCATION)));
        priceEdit.setText(cursor.getString(cursor.getColumnIndex(DatabaseContact.DatabaseEntry.COLUMN_PRICE)));
        ratingEdit.setText(cursor.getString(cursor.getColumnIndex(DatabaseContact.DatabaseEntry.COLUMN_RATING)));
        imageView.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(DatabaseContact.DatabaseEntry.COLUMN_IMAGE))));

        userIdEdit.setEnabled(true);
        nameEdit.setEnabled(true);
        locationEdit.setEnabled(true);
        priceEdit.setEnabled(true);
        imageBtn.setEnabled(true);
    }

    private int deleteAllRowsFromTable() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.delete(DatabaseContact.DatabaseEntry.TABLE_NAME, null, null);
    }

    private int deleteOneItemFronTable(long itemID) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = DatabaseContact.DatabaseEntry._ID + "=?";
        String[] selectionArgs = {String.valueOf(itemID)};
        int rowsDeleted = database.delete(DatabaseContact.DatabaseEntry.TABLE_NAME, selection, selectionArgs);
        return rowsDeleted;
    }

    private void showDeleteConfirmationDialog(final long itemId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (itemId == 0) {
                    deleteAllRowsFromTable();
                } else {
                    deleteOneItemFronTable(itemId);
                }
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void tryToOpenImageSelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
            return;
        }
        openImageSelector();
    }

    private void openImageSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageSelector();
                    // permission was granted
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK)
        {
            if (data != null)
            {
                actualUri = data.getData();
                imageView.setImageURI(actualUri);
                imageView.invalidate();
            }
        }
    }
}

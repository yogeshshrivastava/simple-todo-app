package com.codepath.simpletodo.editscreen;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.simpletodo.R;
import com.codepath.simpletodo.app.TodoApplication;
import com.codepath.simpletodo.entity.Item;

import java.util.Calendar;

/**
 * Screen editing, saving and deleting the items.
 *
 * @author Yogesh Shrivastava
 */
public class EditActivity  extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = EditActivity.class.getSimpleName();

    /**
     * Keys to pass the item state from another activity.
     */
    public static final String EXTRA_ITEM = TAG + ".EXTRA_ITEM";
    public static final String EXTRA_ITEM_TYPE = TAG + ".EXTRA_ITEM_TYPE";
    public static final String EXTRA_ITEM_TYPE_ADD = TAG + ".EXTRA_ITEM_TYPE_ADD";
    public static final String EXTRA_ITEM_TYPE_EDIT = TAG + ".EXTRA_ITEM_TYPE_EDIT";
    public static final String EXTRA_ITEM_CHANGED = TAG + ".EXTRA_ITEM_CHANGED";
    public static final String EXTRA_ITEM_DELETED = TAG + ".EXTRA_ITEM_DELETED";

    /**
     * Position for Items in spinner for status.
     */
    private static final int TODO = 0;
    private static final int DONE = 1;

    /**
     * Activity Views.
     */
    private EditText itemName;
    private EditText itemNotes;
    private TextView dueDateTextView;
    private TextView dueDateLabel;
    private DatePickerDialog datePickerDialog;
    private Spinner status;
    private Spinner priority;

    /**
     * Item object that contains the item information.
     */
    Item editItem;

    /**
     * Holds state if the item is being added or being edited.
     */
    private boolean isAddScreen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Checks if item is being edited or added.
        if(getIntent() != null && getIntent().hasExtra(EXTRA_ITEM_TYPE)) {
            String type = getIntent().getStringExtra(EXTRA_ITEM_TYPE);
            if(EXTRA_ITEM_TYPE_EDIT.equals(type)) {
                if(getIntent() != null && getIntent().hasExtra(EXTRA_ITEM)) {
                    editItem = getIntent().getParcelableExtra(EXTRA_ITEM);
                }
            } else if (EXTRA_ITEM_TYPE_ADD.equals(type)){
                isAddScreen = true;
            }
        }

        // Initialize based on the type of screen.
        initToolbar();
        initScreen();
    }

    private void saveChanges() {
        String name = itemName.getText().toString().trim();
        if(TextUtils.isEmpty(name)) {
            Toast.makeText(this, getString(R.string.error_name_empty), Toast.LENGTH_LONG).show();
            return;
        }

        if(editItem == null) {
            editItem = new Item();
        }

        editItem.setName(name);
        editItem.setNote(itemNotes.getText().toString().trim());
        if(status.getSelectedItemPosition() == TODO) {
            editItem.setDone(false);
        } else {
            editItem.setDone(true);
        }
        editItem.setDueDate(dueDateTextView.getText().toString());
        editItem.setPriority(priority.getSelectedItemPosition());

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                TodoApplication.getDbHelper().writeItemToList(editItem);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                Intent returnIntent = new Intent();
                returnIntent.putExtra(EXTRA_ITEM_CHANGED,true);
                returnIntent.putExtra(EXTRA_ITEM, editItem);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        }.execute();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(isAddScreen) {
            ((TextView) toolbar.findViewById(R.id.toolbarTitle)).setText(getString(R.string.add_item_title));
        } else {
            ((TextView) toolbar.findViewById(R.id.toolbarTitle)).setText(getString(R.string.edit_item_title));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!isAddScreen) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Don't trigger delete if item is add.
        if(!isAddScreen) {
            switch (item.getItemId()) {
                case R.id.actionDelete:
                    delete();
                    return false;
            }
        }

        // Common for both the screens.
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return false;
    }

    private void delete() {
        if(editItem != null) {
            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    TodoApplication.getDbHelper().removeItemFromList(editItem);
                    return true;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(EXTRA_ITEM_DELETED,true);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
            }.execute();
        }
    }

    private void initScreen() {
        itemName = (EditText) findViewById(R.id.name);
        itemNotes = (EditText) findViewById(R.id.notes);
        dueDateTextView = (TextView) findViewById(R.id.dueDateText);
        dueDateLabel = (TextView) findViewById(R.id.dueDateLabel);
        status = (Spinner) findViewById(R.id.statusSpinner);
        priority = (Spinner) findViewById(R.id.prioritySpinner);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveChanges();
            }
        });
        if(editItem != null) {
            itemName.setText(editItem.getName());
            itemNotes.setText(editItem.getNote());
            if(editItem.isDone()) {
                status.setSelection(DONE);
            } else {
                status.setSelection(TODO);
            }
            priority.setSelection(editItem.getPriority());
            showDueDate(editItem.getDueDate());
        }

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                showDate(year, monthOfYear, dayOfMonth);
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        dueDateTextView.setOnClickListener(this);
    }

    /**
     * Show date in a particular format.
     * @param year
     * @param month
     * @param day
     */
    private void showDate(int year, int month, int day) {
        showDueDate(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year).toString());
    }

    /**
     * Sets up due date depending on the date data.
     * @param dueDate
     */
    private void showDueDate(String dueDate) {
        if(!TextUtils.isEmpty(dueDate)) {
            dueDateTextView.setText(dueDate);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.dueDateText:
                datePickerDialog.show();
                break;
        }

    }
}

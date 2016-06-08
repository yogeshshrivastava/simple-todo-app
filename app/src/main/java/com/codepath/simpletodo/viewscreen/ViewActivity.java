package com.codepath.simpletodo.viewscreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.codepath.simpletodo.R;
import com.codepath.simpletodo.editscreen.EditActivity;
import com.codepath.simpletodo.entity.Item;

/**
 * Handles displaying the data of the provided items.
 *
 * author Yogesh Shrivastava.
 */
public class ViewActivity extends AppCompatActivity {
    private static final String TAG = ViewActivity.class.getSimpleName();

    /**
     * Keys to pass the item state from another activity.
     */
    public static final String EXTRA_ITEM = TAG + ".EXTRA_ITEM";
    public static final String EXTRA_ITEM_TYPE = TAG + ".EXTRA_ITEM_TYPE";
    public static final String EXTRA_ITEM_TYPE_VIEW = TAG + ".EXTRA_ITEM_TYPE_VIEW";

    /**
     * Activity Result code for edit request.
     */
    public static final int VIEW_REQUEST_CODE = 1;

    /**
     * Activity Views.
     */
    private TextView itemName;
    private TextView itemNotes;
    private TextView dueDateTextView;
    private TextView status;
    private TextView priority;

    /**
     * Array that holds the priority strings.
     */
    String [] priorityArray;

    /**
     * Item object that contains the item information.
     */
    Item editItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        if(getIntent() != null && getIntent().hasExtra(EXTRA_ITEM_TYPE)) {
            String type = getIntent().getStringExtra(EXTRA_ITEM_TYPE);
            if(EXTRA_ITEM_TYPE_VIEW.equals(type)) {
                if(getIntent() != null && getIntent().hasExtra(EXTRA_ITEM)) {
                    editItem = getIntent().getParcelableExtra(EXTRA_ITEM);
                }
            }
        }

        initToolbar();
        initScreen();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return false;
    }

    private void openEdit() {
        Intent intent = new Intent(ViewActivity.this, EditActivity.class);
        intent.putExtra(EditActivity.EXTRA_ITEM_TYPE, EditActivity.EXTRA_ITEM_TYPE_EDIT);
        intent.putExtra(EditActivity.EXTRA_ITEM, editItem);
        ActivityCompat.startActivityForResult(ViewActivity.this, intent, VIEW_REQUEST_CODE, null);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView)toolbar.findViewById(R.id.toolbarTitle)).setText(getString(R.string.title_to_do));
    }

    private void initScreen() {
        itemName = (TextView) findViewById(R.id.name);
        itemNotes = (TextView) findViewById(R.id.notes);
        dueDateTextView = (TextView) findViewById(R.id.dueDateText);
        status = (TextView) findViewById(R.id.statusTextView);
        priority = (TextView) findViewById(R.id.priorityTextView);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openEdit();
            }
        });
        populateItems();
    }


    private void populateItems() {
        priorityArray = getResources().getStringArray(R.array.priority_array);
        if(editItem != null) {
            itemName.setText(editItem.getName());
            itemNotes.setText(editItem.getNote());
            if(!TextUtils.isEmpty(editItem.getDueDate())) {
                dueDateTextView.setText(editItem.getDueDate());
            }
            if(editItem.isDone()) {
                status.setText(getString(R.string.status_done));
            } else {
                status.setText(getString(R.string.status_todo));
            }
            priority.setText(priorityArray[editItem.getPriority()]);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == VIEW_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                if(data != null) {
                    // Handle if the content is changed.
                    boolean isChanged = data.getBooleanExtra(EditActivity.EXTRA_ITEM_CHANGED, false);
                    if(isChanged) {
                        editItem = data.getParcelableExtra(EditActivity.EXTRA_ITEM);
                        populateItems();
                        return;
                    }

                    // Handle if the content is deleted.
                    boolean isDeleted = data.getBooleanExtra(EditActivity.EXTRA_ITEM_DELETED, false);
                    if(isDeleted) {
                        finish();
                    }
                }
            }
        }
    }
}

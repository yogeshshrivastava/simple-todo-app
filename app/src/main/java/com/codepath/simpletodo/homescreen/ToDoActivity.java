package com.codepath.simpletodo.homescreen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.codepath.simpletodo.R;
import com.codepath.simpletodo.app.TodoApplication;
import com.codepath.simpletodo.editscreen.EditActivity;
import com.codepath.simpletodo.entity.Item;
import com.codepath.simpletodo.utils.DividerItemDecoration;
import com.codepath.simpletodo.viewscreen.ViewActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Home screen of the app, responsible to show the items in a list to the user.
 *
 * @author  Yogesh Shrivastava
 */
public class ToDoActivity extends AppCompatActivity {

    public static final String TAG = ToDoActivity.class.getSimpleName();

    /**
     * Activity Views
     */
    private View emptyText;
    private RecyclerView todoRecyclerView;
    private List<Item> itemsList;
    private ToDoItemsAdapter adapter;

    private final ToDoItemsAdapter.OnItemClickListener onItemClickListener = new ToDoItemsAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View itemView, int position) {
            Intent intent = new Intent(ToDoActivity.this, ViewActivity.class);
            intent.putExtra(ViewActivity.EXTRA_ITEM_TYPE, ViewActivity.EXTRA_ITEM_TYPE_VIEW);
            intent.putExtra(ViewActivity.EXTRA_ITEM, itemsList.get(position));
            ActivityCompat.startActivity(ToDoActivity.this, intent, null);
        }
    };

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            int currentPos = viewHolder.getAdapterPosition();
            removeFromDB(itemsList.get(currentPos));
            itemsList.remove(currentPos);
            adapter.notifyDataSetChanged();
            // Show no list items screen.
            if(itemsList == null || itemsList.size() == 0) {
                setEmptyScreen();
            }
        }
    };

    private final ToDoItemsAdapter.onCheckListener onItemCheckListener = new ToDoItemsAdapter.onCheckListener() {
        @Override
        public void onItemCheckClicked(boolean isChecked, int position) {
            setItemCheck(isChecked, position);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        init();
        initToolbar();
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addNote();
            }
        });
    }

    private void addNote() {
        Intent intent = new Intent(ToDoActivity.this, EditActivity.class);
        intent.putExtra(EditActivity.EXTRA_ITEM_TYPE, EditActivity.EXTRA_ITEM_TYPE_ADD);
        ActivityCompat.startActivity(ToDoActivity.this, intent, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateList();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((TextView)toolbar.findViewById(R.id.toolbarTitle)).setText(getString(R.string.title_to_do));
    }

    private void init() {
        todoRecyclerView = (RecyclerView) findViewById(R.id.lvItems);
        adapter = new ToDoItemsAdapter(getList(), getResources().getStringArray(R.array.priority_array));
        adapter.setOnItemClickListener(onItemClickListener);
        adapter.setOnCheckListener(onItemCheckListener);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        todoRecyclerView.addItemDecoration(itemDecoration);
        todoRecyclerView.setAdapter(adapter);
        todoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(todoRecyclerView);
        emptyText = findViewById(R.id.empty_view);
        emptyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });
    }

    /**
     * Returns an empty list if null otherwise orignal list is returned
     * @return
     */
    private List getList() {
        if(itemsList == null) {
            itemsList = new ArrayList<>();
        }
        return  itemsList;
    }

    /**
     * Reads the database and updates the DB with the latest information.
     */
    private void populateList() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                List<Item> list = TodoApplication.getDbHelper().getAllItems();
                if(list == null) {
                    Log.d(TAG, "doInBackground: no items in the database.");
                    return false;
                } else {
                    itemsList = list;
                    adapter.setUpdatedList(itemsList);
                }
                // Successfully loaded the list.
                return true;
            }

            @Override
            protected void onPostExecute(Boolean isSuccess) {
                super.onPostExecute(isSuccess);
                if(isCancelled()) {
                    return;
                }
                if(isSuccess) {
                    if(adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    if(itemsList == null || itemsList.size() == 0) {
                        setEmptyScreen();
                    } else {
                        emptyText.setVisibility(View.GONE);
                        todoRecyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    setEmptyScreen();
                }
            }
        }.execute();
    }

    /**
     * Hides the list and display add a note method.
     */
    public void setEmptyScreen() {
        emptyText.setVisibility(View.VISIBLE);
        todoRecyclerView.setVisibility(View.GONE);
    }

    /**
     * Set item check and update the data base.
     *
     * @param isChecked
     * @param position
     */
    public void setItemCheck(boolean isChecked, int position) {
        Item item = itemsList.get(position);
        item.setDone(isChecked);
        updateDB(item);
    }

    /**
     * Update data base in the background thread.
     *
     * @param item todo item with all the informtion.
     */
    private void updateDB(final Item item) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                TodoApplication.getDbHelper().writeItemToList(item);
                return null;
            }
        }.execute();
    }

    private void removeFromDB(final Item item) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                TodoApplication.getDbHelper().removeItemFromList(item);
                return null;
            }
        }.execute();
    }
}

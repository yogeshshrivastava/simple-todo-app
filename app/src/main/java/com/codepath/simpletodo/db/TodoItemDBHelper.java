package com.codepath.simpletodo.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.codepath.simpletodo.entity.Item;

import java.util.ArrayList;
import java.util.List;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Helper class that handles interaction with the database hiding the implementation so that it can be changed later if needed.
 *
 * @author Yogesh Shrivastava
 */
public class TodoItemDBHelper {

    private static TodoItemDBHelper INSTANCE;
    private SQLiteDatabase db;

    public static TodoItemDBHelper setup(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new TodoItemDBHelper();
            // setup database
            TodoItemsDB dbHelper = new TodoItemsDB(context);
            INSTANCE.setDb(dbHelper.getWritableDatabase());
        }
        return INSTANCE;
    }

    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        Cursor itemCursor = cupboard().withDatabase(db).query(Item.class).getCursor();
        try {
            QueryResultIterable<Item> itr = cupboard().withCursor(itemCursor).iterate(Item.class);
            for (Item item : itr) {
                itemList.add(item);
            }
        } finally {
            itemCursor.close();
        }
        return itemList;
    }

    public long writeItemToList(Item item) {
        return cupboard().withDatabase(db).put(item);
    }

    private TodoItemDBHelper() {}

    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

    public void removeItemFromList(Item item) {
        cupboard().withDatabase(db).delete(Item.class, item.getId());
    }
}

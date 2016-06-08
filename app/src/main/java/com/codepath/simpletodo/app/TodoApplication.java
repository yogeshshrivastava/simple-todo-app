package com.codepath.simpletodo.app;

import android.app.Application;

import com.codepath.simpletodo.db.TodoItemDBHelper;

/**
 * Application class that holds the DB helper for all the Activities to access.
 *
 * @author Yogesh Shrivastava.
 */
public class TodoApplication extends Application {
    private static TodoItemDBHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = TodoItemDBHelper.setup(this);
    }

    public static TodoItemDBHelper getDbHelper() {
        return dbHelper;
    }
}

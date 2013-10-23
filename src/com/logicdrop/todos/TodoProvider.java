package com.logicdrop.todos;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.logicdrop.todos.TodoActivity;

public class TodoProvider
{
	private static final String DB_NAME = "tasks";
	private static final String TABLE_NAME = "tasks";
	private static final int DB_VERSION = 1;
	private static final String DB_CREATE_QUERY = "CREATE TABLE " + TABLE_NAME + " (id integer primary key autoincrement, title text not null);";

	// TIP: Use final wherever possible
	private SQLiteDatabase storage;
	private SQLiteOpenHelper helper;

	public TodoProvider(final Context ctx)
	{
		this.helper = new SQLiteOpenHelper(ctx, DB_NAME, null, DB_VERSION)
		{
			@Override
			public void onCreate(final SQLiteDatabase db)
			{
				db.execSQL(DB_CREATE_QUERY);
			}

			@Override
			public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
					final int newVersion)
			{
				db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
				this.onCreate(db);
			}
		};

		this.storage = this.helper.getWritableDatabase();
	}

	// TIP: Avoid synchronization
	public synchronized void addTask(final String title)
	{
		ContentValues data = new ContentValues();
		data.put("title", title);

		this.storage.insert(TABLE_NAME, null, data);
	}

	public synchronized void deleteAll()
	{
		this.storage.delete(TABLE_NAME, null, null);
	}

	public synchronized void deleteTask(final long id)
	{
		this.storage.delete(TABLE_NAME, "id=" + id, null);
	}

	public synchronized void deleteTask(final String title)
	{
		this.storage.delete(TABLE_NAME, "title='" + title + "'", null);
	}

	// TIP: Don't return the entire table of data.
	public synchronized List<String> findAll()
	{
		Log.d(TodoActivity.APP_TAG, "findAll triggered");

		List<String> tasks = new ArrayList<String>();

		Cursor c = this.storage.query(TABLE_NAME, new String[] { "title" }, null, null, null, null, null);

		if (c != null)
		{
			c.moveToFirst();

			while (c.isAfterLast() == false)
			{
				tasks.add(c.getString(0));
				c.moveToNext();
			}

			// TIP: Close resources
			c.close();
		}

		return tasks;
	}
}
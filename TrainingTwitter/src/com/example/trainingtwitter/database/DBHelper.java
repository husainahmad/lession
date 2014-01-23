package com.example.trainingtwitter.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	private static final int VERSION = 2;
	private static final String NAME = "husaintwitter.db";
	
	public DBHelper (Context context) {
		super(context, NAME, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE tweet ( tweet_id INTEGER AUTO INCREMENT PRIMARY KEY, " +
				" tweet_title TEXT," +
				" tweet_from TEXT, " +
				" tweet_from_picture TEXT," +
				" tweet_created_at TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == 1) {
			
		}
	}

}

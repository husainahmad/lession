package com.example.trainingtwitter.database;

import com.example.trainingtwitter.fragments.Tweet;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class TwitterContentProvider extends ContentProvider {

	private static final String AUTHORITY = "com.example.trainingtwitter";
	public static final String INFO_TABLE = "tweet";
	private static final int INFO_LIST = 1;
	private static final int INFO_DETAIL = 2;
	
	private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	DBHelper dbHelper;
	
	static {
		// content://com.example.trainingtwitter/info
		uriMatcher.addURI(AUTHORITY, INFO_TABLE, INFO_LIST);

		// content://com.example.trainingtwitter/info/1
		uriMatcher.addURI(AUTHORITY, INFO_TABLE + "/#", INFO_DETAIL);
	}
	
	public static Uri INFO_URI = Uri.parse("content://" + AUTHORITY + "/"
			+ INFO_TABLE);
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (uriMatcher.match(uri)) {
		case INFO_LIST:
			db.insert(INFO_TABLE, null, values);

			break;

		default:
			break;
		}
		return null;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (uriMatcher.match(uri)) {
		case INFO_LIST:
			db.beginTransaction();
			for (int i = 0; i < values.length; i++) {
				db.insert(INFO_TABLE, null, values[i]);
			}
			db.setTransactionSuccessful();
			db.endTransaction();
			return values.length;
		default:
			break;
		}
		return 0;
	}
	
	
	@Override
	public boolean onCreate() {
		dbHelper = new DBHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
		String param = null;
		String[] args = null;
		switch (uriMatcher.match(uri)) {
		case INFO_LIST:
			sqLiteQueryBuilder.setTables(INFO_TABLE);
			break;
		case INFO_DETAIL:
			param = "tweet_id = ?";
			String id = uri.getLastPathSegment();
			args = new String[]{ id };
			sqLiteQueryBuilder.setTables(INFO_TABLE);
			break;

		default:
			break;
		}
		return sqLiteQueryBuilder.query(db, null, param,
				args, null, null, null);
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		return 0;
	}
	
	public static ContentValues generateTweet(Tweet tweet) {
		ContentValues values = new ContentValues();
		values.put("tweet_title", tweet.getText());
		values.put("tweet_from", tweet.getUser().getScreenName());
		values.put("tweet_from_picture", tweet.getUser().getProfileImageUrl());
		values.put("tweet_created_at", tweet.getDateCreated());
		return values;
	}

}

package jp.co.sae.android.banana.database;

import java.io.File;

import jp.co.sae.android.banana.common.LogView;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

public class BananaContentProvider extends ContentProvider {

	private static final int BANANA_CODE = 1;
	
	/** データベース */
	private SQLiteDatabase database = null;
	
	private UriMatcher mUriMatcher;
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		LogView.w(uri.toString());
		int deleteCount = 0;
		
		switch (mUriMatcher.match(uri)) {
		case BANANA_CODE:
			deleteCount = database.delete(
					DatabasePreferences.BananaTable.TABLE_NAME, selection, selectionArgs);
			break;
		
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return deleteCount;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		long rowId;
		Uri newUri;
		
		switch (mUriMatcher.match(uri)) {
		case BANANA_CODE:
			rowId = database.insertOrThrow(
					DatabasePreferences.BananaTable.TABLE_NAME, null, values);
			newUri = ContentUris.withAppendedId(
					DatabasePreferences.BananaTable.TABLE_URI, rowId);
			break;
			
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(newUri, null);
		LogView.w(newUri.toString());
		
		return newUri;
	}

	@Override
	public boolean onCreate() {
		
		// 何にもマッチしないものを作成
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		
		mUriMatcher.addURI(DatabasePreferences.AUTHORITIES, 
				DatabasePreferences.BananaTable.TABLE_NAME, BANANA_CODE);
		
		DatabasePreferences helper = new DatabasePreferences(getContext());
		
		try {
			database = helper.getWritableDatabase();
		} catch (SQLiteException e) {
			File file = getContext().getDatabasePath(DatabasePreferences.DB_NAME);
			file.delete();
			database = helper.getWritableDatabase();
		}
		
		if (null == database){
			return false;
		}
		
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor cursor = null;
		
		switch (mUriMatcher.match(uri)) {
		case BANANA_CODE:
			cursor = database.query(DatabasePreferences.BananaTable.TABLE_NAME, 
					projection, selection, selectionArgs, null, null, sortOrder);
			break;
		
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = 0;
		
		switch (mUriMatcher.match(uri)) {
		case BANANA_CODE:
			count = database.update(DatabasePreferences.BananaTable.TABLE_NAME, 
					values, selection, selectionArgs);
			break;
		
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}




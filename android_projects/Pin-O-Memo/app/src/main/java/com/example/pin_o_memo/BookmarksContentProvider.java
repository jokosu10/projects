package com.example.pin_o_memo;

import java.sql.SQLException;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class BookmarksContentProvider extends ContentProvider {
public static final String PROVIDER_NAME = "com.example.pin_o_memo.bookmark";
	
	/** A uri to do operations on locations table. A content provider is identified by its uri */
	public static final Uri  CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/bookmark");
	
	/** Constant to identify the requested operation */
	private static final int BOOKMARK = 1;
	private static final int BOOKMARK_ID = 2;
	
	private static final UriMatcher uriMatcher;
	
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, "bookmark", BOOKMARK);
		uriMatcher.addURI(PROVIDER_NAME, "bookmark/*", BOOKMARK_ID);
	}
	
	/** This content provider does the database operations by this object */
	BookmarksDB mBookmarksDB;
	
	/** A callback method which is invoked when the content provider is starting up */
	@Override
	public boolean onCreate(){
		mBookmarksDB = new BookmarksDB(getContext());
		return true;
	}
	
	/** A callback method which is invoked when insert operation is requested on this content provider */
	@Override
	public Uri insert(Uri uri, ContentValues values){
		long rowID = mBookmarksDB.insert(values);
		if(rowID>0){
			Uri new_uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(new_uri, null);
			return new_uri;
		}
		else{
			try{
				throw new SQLException("Failed to insert : " + uri);
			} catch (SQLException e){
				e.printStackTrace();
			}
			return uri;
		}
		
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs){
		return 0;
	}
	
	/** A callback method which is invoked when delete operation is requested on this content provider */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs){
		return 0;
	}
	
	/** A callback method which is invoked by default content uri */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
		
		if(uriMatcher.match(uri) == BOOKMARK){
			return mBookmarksDB.getAllBookmarks();
		}
		else if(uriMatcher.match(uri) == BOOKMARK_ID){
			
			Cursor c = mBookmarksDB.getBookmark(projection, selection, selectionArgs);
			c.setNotificationUri(getContext().getContentResolver(), uri);
			return c;
		}
		
		return null;
		
	}
	
	@Override
	public String getType(Uri uri){
		return null;
	}
}

package com.example.pin_o_memo;

import java.sql.SQLException;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class ProfileContentProvider extends ContentProvider{
	
	public static final String PROVIDER_NAME = "com.example.pin_o_memo.profile";
	
	/** A uri to do operations on locations table. A content provider is identified by its uri */
	public static final Uri  CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/profile");
	
	/** Constant to identify the requested operation */
	private static final int PROFILE = 1;
	
	private static final UriMatcher uriMatcher;
	
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, "profile", PROFILE);
	}
	
	/** This content provider does the database operations by this object */
	ProfileDB mProfileDB;
	
	/** A callback method which is invoked when the content provider is starting up */
	@Override
	public boolean onCreate(){
		mProfileDB = new ProfileDB(getContext());
		return true;
	}
	
	/** A callback method which is invoked when insert operation is requested on this content provider */
	@Override
	public Uri insert(Uri uri, ContentValues values){
		long rowID = mProfileDB.insert(values);
		if(rowID>0){
			uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			
		}
		else{
			try{
				throw new SQLException("Failed to insert : " + uri);
			} catch (SQLException e){
				e.printStackTrace();
			}
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return uri;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs){
		int count;
		count = mProfileDB.update(values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	/** A callback method which is invoked when delete operation is requested on this content provider */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs){
		return 0;
	}
	
	/** A callback method which is invoked by default content uri */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
		return mProfileDB.getName(projection, selection, selectionArgs);
	}
	
	@Override
	public String getType(Uri uri){
		return null;
	}
	
}

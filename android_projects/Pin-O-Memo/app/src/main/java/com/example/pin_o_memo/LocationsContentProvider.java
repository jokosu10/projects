package com.example.pin_o_memo;

import java.sql.SQLException;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/** A custom Content provider to do the database operations */
public class LocationsContentProvider extends ContentProvider{
	
	public static final String PROVIDER_NAME = "com.example.pin_o_memo.locations";
	
	/** A uri to do operations on locations table. A content provider is identified by its uri */
	public static final Uri  CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/locations");
	
	/** Constant to identify the requested operation */
	private static final int LOCATIONS = 1;
	private static final int LOCATION_ID = 2;
	
	private static final UriMatcher uriMatcher;
	
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, "locations", LOCATIONS);
		uriMatcher.addURI(PROVIDER_NAME, "locations/*", LOCATION_ID);
	}
	
	/** This content provider does the database operations by this object */
	LocationsDB mLocationsDB;
	
	/** A callback method which is invoked when the content provider is starting up */
	@Override
	public boolean onCreate(){
		mLocationsDB = new LocationsDB(getContext());
		return true;
	}
	
	/** A callback method which is invoked when insert operation is requested on this content provider */
	@Override
	public Uri insert(Uri uri, ContentValues values){
		long rowID = mLocationsDB.insert(values);
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
		int count;
		count = mLocationsDB.update(values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	/** A callback method which is invoked when delete operation is requested on this content provider */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs){
		int cnt = 0;
		cnt = mLocationsDB.del();
		return cnt;
	}
	
	/** A callback method which is invoked by default content uri */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
		
		if(uriMatcher.match(uri) == LOCATIONS){
			return mLocationsDB.getAllLocations();
		}
		else if(uriMatcher.match(uri) == LOCATION_ID){
			
			Cursor c = mLocationsDB.getDate(projection, selection, selectionArgs);
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
package com.example.pin_o_memo;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocationsDB extends SQLiteOpenHelper{
	
	/** Database name */
	public static final String DBNAME = "locationssqlite";
	
	/** Version number of the database */
	private static int VERSION = 1;
	
	/** Field 1 of the table locations, which is the primary key */
	public static final String FIELD_ROW_ID = "_id";
	
	/** Field 2 of the table locations, stores the latitude */
	public static final String FIELD_LAT = "lat";
	
	/** Field 3 of the table locations, stores the longitude */
	public static final String FIELD_LNG = "lng";
	
	/** Field 4 of the table locations, stores the zoom level of map */
	public static final String FIELD_ZOOM = "zom";
	
	public static final String FIELD_TITLE = "ttl";
	
	public static final String FIELD_MONTH = "mon";
	
	public static final String FIELD_DAY = "day";
	
	public static final String FIELD_YEAR = "year";
	
	public static final String FIELD_HOUR = "hour";
	
	public static final String FIELD_MINUTE = "min";
	
	public static final String FIELD_NOTES = "notes";
	
	public static final String FIELD_LOCATION = "loc";
	
	public static final String FIELD_COMPLETED = "comp";
	
	public static final String FIELD_EMAIL = "email";
	
	/** A constant, stores the table name */
	private static final String DATABASE_TABLE = "locations";
	
	/** An instance variable for SQLiteDatabase */
	private SQLiteDatabase mDB;
	
	/** Constructor */
	public LocationsDB(Context context){
		super(context, DBNAME, null, VERSION);
		this.mDB = getWritableDatabase();
	}
	
	/** This is a callback method, invoked when the method getReadableDatabase() /
	 * getWritableDatabase() is called provided the database does not exists
	 */
	@Override
	public void onCreate(SQLiteDatabase db){
		String sql = 	"create table " + DATABASE_TABLE + " ( " +
						FIELD_ROW_ID + " integer primary key autoincrement , " +
						FIELD_EMAIL + " text , " +
						FIELD_LNG + " text , " +
						FIELD_LAT + " text , " +
						FIELD_LOCATION + " text , " +
						FIELD_NOTES + " text , " +
						FIELD_ZOOM + " text , " +
						FIELD_TITLE + " text , " +
						FIELD_MONTH + " integer , " +
						FIELD_DAY + " integer , " +
						FIELD_YEAR + " integer , " +
						FIELD_HOUR + " integer , " +
						FIELD_MINUTE + " integer , " +
						FIELD_COMPLETED + " text " +
						" ); ";
		db.execSQL(sql);
		
		
	}
	
	/** Inserts a new location to the table locations */
	public long insert(ContentValues contentValues){
		long rowID = mDB.insert(DATABASE_TABLE, null, contentValues);
		return rowID;
	}
	
	public int update(ContentValues contentValues, String where, String[] whereArgs){
		int cnt = mDB.update(DATABASE_TABLE, contentValues, where, whereArgs);
		
		return cnt;
	}
	
	/** Deletes all locations from the table */
	public int del(){
		int cnt = mDB.delete(DATABASE_TABLE, null, null);
		return cnt;
	}
	
	/** Returns all the locations from the table */
	public Cursor getAllLocations(){
		return mDB.query(DATABASE_TABLE, new String[] { FIELD_ROW_ID, FIELD_LAT, FIELD_LNG, FIELD_ZOOM, FIELD_TITLE, FIELD_COMPLETED, FIELD_LOCATION, FIELD_NOTES,LocationsDB.FIELD_MONTH,
    			LocationsDB.FIELD_DAY,
    			LocationsDB.FIELD_YEAR,
    			LocationsDB.FIELD_HOUR,
    			LocationsDB.FIELD_MINUTE } , null, null, null, null, null);
	}
	
	/** Returns all the locations from the table */
	public Cursor getDate(String[] projection, String selection, String[] selectionArgs){
		Cursor c = mDB.query(DATABASE_TABLE, projection, selection, selectionArgs, null, null, null);
		return c;
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){		
	}
	
	public ArrayList<Cursor> getData(String Query){
		//get writable database
		SQLiteDatabase sqlDB = this.getWritableDatabase();
		String[] columns = new String[] { "mesage" };
		//an array list of cursor to save two cursors one has results from the query 
		//other cursor stores error message if any errors are triggered
		ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
		MatrixCursor Cursor2= new MatrixCursor(columns);
		alc.add(null);
		alc.add(null);
		
		
		try{
			String maxQuery = Query ;
			//execute the query results will be save in Cursor c
			Cursor c = sqlDB.rawQuery(maxQuery, null);
			

			//add value to cursor2
			Cursor2.addRow(new Object[] { "Success" });
			
			alc.set(1,Cursor2);
			if (null != c && c.getCount() > 0) {

				
				alc.set(0,c);
				c.moveToFirst();
				
				return alc ;
			}
			return alc;
		} catch(SQLException sqlEx){
			Log.d("printing exception", sqlEx.getMessage());
			//if any exceptions are triggered save the error message to cursor an return the arraylist
			Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
			alc.set(1,Cursor2);
			return alc;
		} catch(Exception ex){

			Log.d("printing exception", ex.getMessage());

			//if any exceptions are triggered save the error message to cursor an return the arraylist
			Cursor2.addRow(new Object[] { ""+ex.getMessage() });
			alc.set(1,Cursor2);
			return alc;
		}

		
	}
	
}
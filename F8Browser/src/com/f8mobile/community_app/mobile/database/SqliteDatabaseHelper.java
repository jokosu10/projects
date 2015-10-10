package com.f8mobile.community_app.mobile.database;

import java.util.ArrayList;

import com.f8mobile.community_app.mobile.model.UserDataModel;
import com.f8mobile.community_app.mobile.model.UserMessages;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqliteDatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "F8_DATABASE";
	private static final String TABLE_NAME_USER = "F8_USER_TABLE";
	private static final String TABLE_NAME_MESSAGES = "F8_USER_MESSAGES";
	
	public SqliteDatabaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		String createTableUsers = "CREATE TABLE " + TABLE_NAME_USER + "("
									+ "id text, "
									+ "fname text, "
									+ "lname text, "
									+ "contact text, "
									+ "address text, "
									+ "country text, "
									+ "gender text, "
									+ "cell_no text, "
									+ "gcm_reg_id text "
									+ ")";
		
		String createTableMessages = "CREATE TABLE " + TABLE_NAME_MESSAGES + "("
									+ "sender_id text, "
									+ "recipient_id text, "
									+ "user text, "
									+ "message text, "
									+ "timestamp text, "
									+ "gcm_reg_id_sender text, "
									+ "gcm_reg_id_receiver text"
									+ ")";
				
		db.execSQL(createTableUsers);
		db.execSQL(createTableMessages);
	
	}

	public void insertToUsersTable(UserDataModel u) {
		
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT * FROM " + TABLE_NAME_USER + " WHERE id = '" + u.getId().trim().replace(" ", "") + "'";
		Cursor cursor = db.rawQuery(query, null);
		if(!cursor.moveToFirst()) { // no existing user
			
			SQLiteDatabase db2 = this.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put("id", u.getId());
			cv.put("fname", u.getFname());
			cv.put("lname", u.getLname());
			cv.put("contact", u.getContact());
			cv.put("address", u.getAddress());
			cv.put("country", u.getCountry());
			cv.put("gender", u.getSex());
			cv.put("cell_no", u.getCellNo());
			cv.put("gcm_reg_id", u.getGcmRegId());
			db2.insert(TABLE_NAME_USER, null, cv);
			db2.close();
		}
		cursor.close();
		db.close();	
	}
	
	public void updateGcmIdOfThisUser(String id, String regId) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("gcm_reg_id", regId);
		db.update(TABLE_NAME_USER, cv, "id = '" + id + "'", null);
		db.close();
	}
	
	public void insertToMessagesTable(UserMessages u) {
		
		SQLiteDatabase db2 = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("sender_id", u.getSenderId());
		cv.put("recipient_id", u.getRecipientId());
		cv.put("user", u.getUser());
		cv.put("message", u.getMessage());
		cv.put("timestamp", u.getTimestamp());
		cv.put("gcm_reg_id_sender", u.getRegIdSender());
		cv.put("gcm_reg_id_receiver", u.getRegIdReceiver());
		db2.insert(TABLE_NAME_MESSAGES, null, cv);
		db2.close();
	}
	
	public UserDataModel returnUserData(String param) {
		
		UserDataModel user = null;
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT * FROM " + TABLE_NAME_USER + " WHERE id = '" + param.trim().replace(" ", "") + "'";
		Log.e("SqliteDatabaseHelper", "returnUserData query: " + query);
		Cursor cursor = db.rawQuery(query, null);
		if(cursor != null && cursor.moveToFirst()) {
			
			user = new UserDataModel();
			user.setId(cursor.getString(0));
			user.setFname(cursor.getString(1));
			user.setLname(cursor.getString(2));
			user.setContact(cursor.getString(3));
			user.setAddress(cursor.getString(4));
			user.setCountry(cursor.getString(5));
			user.setSex(cursor.getString(6));
			user.setCellNo(cursor.getString(7));
			user.setGcmRegId(cursor.getString(8));
			
		} 
		
		cursor.close();
		db.close();
		return user;
	}
	
	public ArrayList<UserMessages> returnMessagesWithThisUser(String sender, String receiver) {
		
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<UserMessages> uMessageList = new ArrayList<UserMessages>();
		String query = "SELECT * FROM " + TABLE_NAME_MESSAGES + " WHERE (sender_id = '" + sender.trim().replace(" ", "") + "' AND recipient_id ='" + receiver.trim().replace(" ", "")+ "') OR "
						+ "(sender_id = '" + receiver.trim().replace(" ", "") + "' AND recipient_id ='" + sender.trim().replace(" ", "")+ "')";
		Log.e("SqliteDatabaseHelper", "" + query);
		Cursor cursor = db.rawQuery(query, null);
		
		Log.e("", "returnMessagesWithThisUser() cursor: " + cursor + ", cursor.moveToFirst: " + cursor.moveToFirst());
		if(cursor != null && cursor.moveToFirst()) {
			while(cursor.moveToNext()) {
				
				UserMessages u = new UserMessages();
				u.setSenderId(cursor.getString(0));
				u.setRecipientId(cursor.getString(1));
				u.setUser(cursor.getString(2));
				u.setMessage(cursor.getString(3));
				u.setTimestamp(cursor.getString(4));
				u.setRegIdSender(cursor.getString(5));
				u.setRegIdReceiver(cursor.getString(6));
				uMessageList.add(u);
			}
		}
		
		cursor.close();
		db.close();
		return uMessageList;
	}
	
	public ArrayList<String> viewRecentChats() {
		
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<String> uMessageList = new ArrayList<String>();
		String query = "SELECT distinct user FROM " + TABLE_NAME_MESSAGES;
		Log.e("SqliteDatabaseHelper", "" + query);
		Cursor cursor = db.rawQuery(query, null);
		
		Log.e("", "viewRecentChats() cursor: " + cursor + ", cursor.moveToFirst: " + cursor.moveToFirst());
		if(cursor != null && cursor.moveToFirst()) {
			while(cursor.moveToNext()) {
				
				String user = cursor.getString(0);
				uMessageList.add(user);
			}
		}
		
		cursor.close();
		db.close();
		return uMessageList;
	}
	
	public ArrayList<String> returnAllUsers() {
		
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<String> userList = new ArrayList<String>();
		String query = "SELECT id, fname, lname FROM " + TABLE_NAME_USER;
		Log.e("SqliteDatabaseHelper", "" + query);
		Cursor cursor = db.rawQuery(query, null);
		
		if(cursor != null && cursor.moveToFirst()) {
			while(cursor.moveToNext()) {
			
				String name = "[ " + cursor.getString(0) + " ] " + cursor.getString(1) + " " +  cursor.getString(2);
				userList.add(name);
			}
		}
		
		cursor.close();
		db.close();
		return userList;
	}
	
	public String getMaxId() {
		String maxId = "16424"; // default maxId from initial_members_list.csv in assets
		String maxIdFromDb = "";
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT max(id) FROM " + TABLE_NAME_USER;
		Cursor cursor = db.rawQuery(query, null);
		if(cursor != null && cursor.moveToFirst()) {
			maxIdFromDb = cursor.getString(0);
		}
		
		Log.e("SqliteDatabaseHelper", "" + query + ", result: " + maxId);
		cursor.close();
		db.close();
		if(Integer.valueOf(maxIdFromDb) > Integer.valueOf(maxId)) {
			maxId = maxIdFromDb;
		}
		return maxId;
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE " + TABLE_NAME_USER);
		db.execSQL("DROP TABLE " + TABLE_NAME_MESSAGES);
		this.onCreate(db);
	}

}

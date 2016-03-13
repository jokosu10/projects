package com.smsgt.mobile.application.supertsuper_v3.util_helpers;

import java.util.ArrayList;
import java.util.List;

import com.smsgt.mobile.application.supertsuper_v3.database.model.AchievementsModel;
import com.smsgt.mobile.application.supertsuper_v3.database.model.CategoriesAndReportModel;
import com.smsgt.mobile.application.supertsuper_v3.database.model.RewardsModel;
import com.smsgt.mobile.application.supertsuper_v3.database.model.TsuperUserProfileModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqliteDatabaseTsuperHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_TSUPER_PROFILE_TABLE = "tsuper_user_profile";
	private static final String DATABASE_TSUPER_REWARDS_TABLE = "tsuper_user_rewards";
	private static final String DATABASE_TSUPER_ACHIEVEMENTS_TABLE = "tsuper_user_achievements";
	private static final String DATABASE_TSUPER_CATEGORIES_AND_REPORTS_TABLE = "tsuper_user_categories_and_reports";
	private static final String DATABASE_TSUPER_TRAFFIC_PROFILE_MAPVIEW = "tsuper_traffic_profile_list";
	private static final String DATABASE_NAME = "super_tsuper_db";
	private Context contxt;
	
	public SqliteDatabaseTsuperHelper(Context context) {
		super(context, DATABASE_NAME, null , 1);
		contxt = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
//		Log.e("", "creating tables: " + DATABASE_TSUPER_PROFILE_TABLE + "," + DATABASE_TSUPER_ACHIEVEMENTS_TABLE + "," + DATABASE_TSUPER_REWARDS_TABLE);
		
/*		String CREATE_TSUPER_PROFILE_TABLE = "CREATE TABLE " +
				DATABASE_TSUPER_PROFILE_TABLE + "("
	             + " TSUPER_USERNAME TEXT PRIMARY KEY NOT NULL ON CONFLICT IGNORE, " 
				 + " TSUPER_PASSWORD TEXT NOT NULL, " 
	             + " TSUPER_FIRSTNAME TEXT NOT NULL,"
	             + " TSUPER_LASTNAME TEXT NOT NULL,"
	             + " TSUPER_CONTACTNUMBER TEXT NULL,"
	             + " TSUPER_CITY TEXT NOT NULL," 
	             + " TSUPER_TOTAL_POINTS TEXT NOT NULL,"
	             + " TSUPER_AVERAGE_MOVING_SPEED TEXT NOT NULL,"
	             + " TSUPER_TRAVEL_BEARING TEXT NOT NULL,"
	             + " TSUPER_TRAVEL_LONGITUDE TEXT NOT NULL,"
	             + " TSUPER_TRAVEL_LATITUDE TEXT NOT NULL,"
	             + " TSUPER_PREV_DISTANCE TEXT NOT NULL,"
	             + " TSUPER_CURR_DISTANCE TEXT NOT NULL,"
	             + " TSUPER_TOTAL_DISTANCE TEXT NOT NULL,"
	             + " TSUPER_MEMBER_SINCE TEXT NOT NULL"
	             + ")";
		
		String CREATE_TSUPER_REWARDS_TABLE = "CREATE TABLE " +
				DATABASE_TSUPER_REWARDS_TABLE + "("
	             + " TSUPER_REWARDS_ID TEXT NOT NULL," 
				 + " TSUPER_REWARDS_NAME TEXT NOT NULL," 
	             + " TSUPER_REWARDS_EQUIVALENT_POINTS TEXT NOT NULL"
	             + ")";
		
		String CREATE_TSUPER_ACHIEVEMENTS_TABLE = "CREATE TABLE " +
				DATABASE_TSUPER_ACHIEVEMENTS_TABLE + "("
	             + " TSUPER_ACHIEVEMENTS_ID TEXT NOT NULL, " 
				 + " TSUPER_ACHIEVEMENTS_NAME TEXT NOT NULL, " 
	             + " TSUPER_ACHIEVEMENTS_DESCRIPTION TEXT NOT NULL"
	             + ")";
		
		String CREATE_TSUPER_CATEGORIES_AND_REPORTS_TABLE = "CREATE TABLE " +
				DATABASE_TSUPER_CATEGORIES_AND_REPORTS_TABLE + "("
	             + " TSUPER_CATEGORIES_AND_REPORTS_TYPE TEXT NOT NULL, " 
				 + " TSUPER_CATEGORIES_AND_REPORTS_NAME TEXT NOT NULL, "
				 + " TSUPER_CATEGORIES_AND_REPORTS_POSTED_BY_USER TEXT NOT NULL, "
				 + " TSUPER_CATEGORIES_AND_REPORTS_DATE_TIME_POSTING TEXT NOT NULL, "
	             + " TSUPER_CATEGORIES_AND_REPORTS_DESCRIPTION TEXT NOT NULL, "
	             + " TSUPER_CATEGORIES_AND_REPORTS_LONGITUDE TEXT NOT NULL, "
	             + " TSUPER_CATEGORIES_AND_REPORTS_LATITUDE TEXT NOT NULL"
	             + ")";
				
	      db.execSQL(CREATE_TSUPER_PROFILE_TABLE);
	      db.execSQL(CREATE_TSUPER_REWARDS_TABLE);
	      db.execSQL(CREATE_TSUPER_ACHIEVEMENTS_TABLE); 
	      db.execSQL(CREATE_TSUPER_CATEGORIES_AND_REPORTS_TABLE); 
	      */

		String CREATE_TSUPER_TRAFFIC_PROFILE__MAPVIEW_LIST = "CREATE TABLE " +
				DATABASE_TSUPER_TRAFFIC_PROFILE_MAPVIEW + "("
	             + " TSUPER_TRAFFIC_PROFILE_KEY TEXT NOT NULL"
	             + ")";
		db.execSQL(CREATE_TSUPER_TRAFFIC_PROFILE__MAPVIEW_LIST); 
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

//		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TSUPER_PROFILE_TABLE);
//		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TSUPER_ACHIEVEMENTS_TABLE);
//		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TSUPER_REWARDS_TABLE);
//		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TSUPER_CATEGORIES_AND_REPORTS_TABLE);
		db.execSQL("DROP TABLE IF EXISTS" + DATABASE_TSUPER_TRAFFIC_PROFILE_MAPVIEW);
	    onCreate(db);
	}
	
	public void addTsuperTrafficProfileToDB(String key) {
		SQLiteDatabase db = null;
		try {
	        db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("TSUPER_TRAFFIC_PROFILE_KEY", key);
			
		    db.insert(DATABASE_TSUPER_TRAFFIC_PROFILE_MAPVIEW, null, values);
		        
		} catch(Exception e) {
				Log.e("","Exception on addTsuperProfile", e);
		} finally { 
				if(db != null) db.close();
		}
	}
	
	public List<String> getAllTsuperTrafficProfileFromDB() {
		
		SQLiteDatabase db = null;
		
		List<String> trafficProfileList = null;
		try {
			db = this.getReadableDatabase();
			
			String query = "Select * FROM " + DATABASE_TSUPER_TRAFFIC_PROFILE_MAPVIEW;
			Cursor cursor = db.rawQuery(query, null);
			trafficProfileList = new ArrayList<String>();
			
			if (cursor != null && cursor.moveToFirst()) {
				do {
					String key = cursor.getString(0);
					trafficProfileList.add(key);	
				} while(cursor.moveToNext());
			} 
			cursor.close();
		} catch(Exception e) {
			Log.e("","Exception on getAllTsuperTrafficProfileFromDB",e);
		} finally {
			if(db != null) db.close();
		}	
		
		return trafficProfileList;
	}
	
	public void deleteTsuperTrafficProfileToDB() {
		SQLiteDatabase db = null;
		try {
	        db = this.getWritableDatabase();
			db.execSQL("DROP TABLE " + DATABASE_TSUPER_TRAFFIC_PROFILE_MAPVIEW);
			this.onCreate(db);
	        
		} catch(Exception e) {
				Log.e("","Exception on addTsuperProfile", e);
		} finally { 
				if(db != null) db.close();
		}
	}
	
	public void addTsuperProfile(TsuperUserProfileModel tsuperUserProfileModel) {
		
		SQLiteDatabase db = null;
		try {
	        db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("TSUPER_USERNAME", tsuperUserProfileModel.getUserName());
			values.put("TSUPER_PASSWORD", tsuperUserProfileModel.getPassword());
	        values.put("TSUPER_FIRSTNAME", tsuperUserProfileModel.getFirstName());
	        values.put("TSUPER_LASTNAME", tsuperUserProfileModel.getLastName());
	        values.put("TSUPER_CONTACTNUMBER", tsuperUserProfileModel.getContactNumber());
	        values.put("TSUPER_CITY", tsuperUserProfileModel.getCity());
	        values.put("TSUPER_TOTAL_POINTS", tsuperUserProfileModel.getTotalPoints());	        
	        values.put("TSUPER_AVERAGE_MOVING_SPEED", tsuperUserProfileModel.getAverageMovingSpeed());
	        values.put("TSUPER_TRAVEL_BEARING", tsuperUserProfileModel.getTravelBearing());
	        values.put("TSUPER_TRAVEL_LONGITUDE", tsuperUserProfileModel.getTravelLongitude());
	        values.put("TSUPER_TRAVEL_LATITUDE", tsuperUserProfileModel.getTravelLatitude());
	        values.put("TSUPER_PREV_DISTANCE", tsuperUserProfileModel.getPrevDistance());
	        values.put("TSUPER_CURR_DISTANCE", tsuperUserProfileModel.getCurrDistance()); 
	        values.put("TSUPER_TOTAL_DISTANCE", tsuperUserProfileModel.getTotalDistance());
	        values.put("TSUPER_MEMBER_SINCE", tsuperUserProfileModel.getMembershipDate());
	        
			Log.e("", "adding TsuperProfile with username:  " + tsuperUserProfileModel.getUserName());
	        db.insert(DATABASE_TSUPER_PROFILE_TABLE, null, values);
	        
		} catch(Exception e) {
			Log.e("","Exception on addTsuperProfile", e);
		} finally { 
			if(db != null) db.close();
		}
		
	}
	
	public TsuperUserProfileModel findTsuperProfile(String tsuperUserName, String tsuperPassword) {
		
		SQLiteDatabase db = null;
		TsuperUserProfileModel tsuperProfile = null;
		try {
			db = this.getReadableDatabase();   
			String query = "SELECT * FROM " + DATABASE_TSUPER_PROFILE_TABLE + " WHERE TSUPER_USERNAME = '" + tsuperUserName.trim() + "' AND TSUPER_PASSWORD ='" + tsuperPassword + "'";
			Cursor cursor =  db.rawQuery(query, null);
			
			if (cursor != null && cursor.moveToFirst()) {
				tsuperProfile = new TsuperUserProfileModel();
				tsuperProfile.setUserName(cursor.getString(0));
				tsuperProfile.setPassword(cursor.getString(1));
				tsuperProfile.setFirstName(cursor.getString(2));
				tsuperProfile.setLastName(cursor.getString(3));
				tsuperProfile.setContactNumber(cursor.getString(4));
				tsuperProfile.setCity(cursor.getString(5));
				tsuperProfile.setTotalPoints(cursor.getString(6));				
				tsuperProfile.setAverageMovingSpeed(cursor.getString(7));
				tsuperProfile.setTravelBearing(cursor.getString(8));
				tsuperProfile.setTravelLongitude(cursor.getString(9));
				tsuperProfile.setTravelLatitude(cursor.getString(10));
				tsuperProfile.setPrevDistance(cursor.getString(11));
				tsuperProfile.setCurrDistance(cursor.getString(12));
				tsuperProfile.setTotalDistance(cursor.getString(13));
				tsuperProfile.setMembershipDate(cursor.getString(14));
				cursor.close();
			} 
		} catch(Exception e) {
			Log.e("","Exception on findTsuperProfile",e);
		} finally {
			if(db != null) db.close();
		}	
		return tsuperProfile;
	}
	
	public boolean deleteTsuperProfile(String tsuperUserName) {
		
		boolean result = false;
		SQLiteDatabase db = null;
		try {
			db = this.getWritableDatabase();
			String query = "Select TSUPER_USERNAME FROM " + DATABASE_TSUPER_PROFILE_TABLE + " WHERE TSUPER_USERNAME = '" + tsuperUserName + "'";
			Cursor cursor = db.rawQuery(query, null);
			TsuperUserProfileModel tsuperProfile = new TsuperUserProfileModel();
			
			if (cursor.moveToFirst()) {
				tsuperProfile.setUserName(cursor.getString(0));
				
				Log.e("", "deleting TsuperProfile from database with username: " + tsuperUserName);
				
				db.delete(DATABASE_TSUPER_PROFILE_TABLE, "TSUPER_USERNAME = ?",
			            new String[] { tsuperUserName });
				cursor.close();
				result = true;
			}
		} catch(Exception e) {
			Log.e("","Exception on deleteTsuperProfile",e);
		} finally {
			if(db != null) db.close();
		}	
		return result;
	}
	
	public void updateTsuperProfile(String tsuperUserName, String totalPoints, String averageMovingSpeed, String travelBearing, String travelLongitude, 
			String travelLatitude, String prevDistance, String currDistance, String totalDistance) {
		
		SQLiteDatabase db = null;
		try {
			db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
	        values.put("TSUPER_TOTAL_POINTS", totalPoints);
			values.put("TSUPER_AVERAGE_MOVING_SPEED", averageMovingSpeed);
			values.put("TSUPER_TRAVEL_BEARING", travelBearing);
			values.put("TSUPER_TRAVEL_LONGITUDE", travelLongitude);
			values.put("TSUPER_TRAVEL_LATITUDE", travelLatitude);
			values.put("TSUPER_PREV_DISTANCE", prevDistance);
			values.put("TSUPER_CURR_DISTANCE", currDistance);
	        values.put("TSUPER_TOTAL_DISTANCE", totalDistance);
	        
	        Log.e("", "updating TsuperProfile with username: " + tsuperUserName);
	        
	        db.update(DATABASE_TSUPER_PROFILE_TABLE, values, "TSUPER_USERNAME = ? ", new String[] { tsuperUserName } );
	        
		} catch(Exception e) {
			Log.e("","Exception on updateTsuperProfile",e);
		} finally {
			if(db != null) db.close();
		}	
	}
	
	public void updateTsuperProfilePoints(String tsuperUserName, String totalPoints) {
		
		SQLiteDatabase db = null;
		try {
			db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
	        values.put("TSUPER_TOTAL_POINTS", totalPoints);
	        
	        Log.e("", "updating TsuperProfile points with username: " + tsuperUserName + " with points: " + totalPoints);
	        
	        db.update(DATABASE_TSUPER_PROFILE_TABLE, values, "TSUPER_USERNAME = ? ", new String[] { tsuperUserName } );
	        
		} catch(Exception e) {
			Log.e("","Exception on updateTsuperProfilePoints",e);
		} finally {
			if(db != null) db.close();
		}	
	}
	
	
	// for rewards part
	
	public void populateRewardsTable() {
		
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("TSUPER_REWARDS_ID", "rewards_1");
			values.put("TSUPER_REWARDS_NAME", "rewards1");
	        values.put("TSUPER_REWARDS_EQUIVALENT_POINTS", "5");
	        
	        ContentValues values2 = new ContentValues();
			values2.put("TSUPER_REWARDS_ID", "rewards_2");
			values2.put("TSUPER_REWARDS_NAME", "rewards2");
	        values2.put("TSUPER_REWARDS_EQUIVALENT_POINTS", "10");
	        
	        ContentValues values3 = new ContentValues();
			values3.put("TSUPER_REWARDS_ID", "rewards_3");
			values3.put("TSUPER_REWARDS_NAME", "rewards3");
	        values3.put("TSUPER_REWARDS_EQUIVALENT_POINTS", "15");
	        
	        ContentValues values4 = new ContentValues();
			values4.put("TSUPER_REWARDS_ID", "rewards_4");
			values4.put("TSUPER_REWARDS_NAME", "rewards4");
	        values4.put("TSUPER_REWARDS_EQUIVALENT_POINTS", "20");
	        
	        db.insert(DATABASE_TSUPER_REWARDS_TABLE, null, values);
	        db.insert(DATABASE_TSUPER_REWARDS_TABLE, null, values2);
	        db.insert(DATABASE_TSUPER_REWARDS_TABLE, null, values3);
	        db.insert(DATABASE_TSUPER_REWARDS_TABLE, null, values4);
		
	}
	
	public void populateAchievementsTable() {
		
			SQLiteDatabase db = this.getWritableDatabase();
			
	        ContentValues values = new ContentValues();
	        values.put("TSUPER_ACHIEVEMENTS_ID", "achievement_icon");
			values.put("TSUPER_ACHIEVEMENTS_NAME", "achievements1");
	        values.put("TSUPER_ACHIEVEMENTS_DESCRIPTION", "achievements1");
	        
	        ContentValues values2 = new ContentValues();
	        values2.put("TSUPER_ACHIEVEMENTS_ID", "achievement_icon");
			values2.put("TSUPER_ACHIEVEMENTS_NAME", "achievements2");
	        values2.put("TSUPER_ACHIEVEMENTS_DESCRIPTION", "achievements2");
	        
	        ContentValues values3 = new ContentValues();
	        values3.put("TSUPER_ACHIEVEMENTS_ID", "achievement_icon");
			values3.put("TSUPER_ACHIEVEMENTS_NAME", "achievements3");
	        values3.put("TSUPER_ACHIEVEMENTS_DESCRIPTION", "achievements3");
	        
	        ContentValues values4 = new ContentValues();
	        values4.put("TSUPER_ACHIEVEMENTS_ID", "achievement_icon");
			values4.put("TSUPER_ACHIEVEMENTS_NAME", "achievements4");
	        values4.put("TSUPER_ACHIEVEMENTS_DESCRIPTION", "achievements4");
	        
	        db.insert(DATABASE_TSUPER_ACHIEVEMENTS_TABLE, null, values);
	        db.insert(DATABASE_TSUPER_ACHIEVEMENTS_TABLE, null, values2);
	        db.insert(DATABASE_TSUPER_ACHIEVEMENTS_TABLE, null, values3);
	        db.insert(DATABASE_TSUPER_ACHIEVEMENTS_TABLE, null, values4);
	}
	
	
	public List<RewardsModel> returnAvailableRewards() {
		
		SQLiteDatabase db = null;
		
		List<RewardsModel> rewardsList = null;
		try {
			db = this.getReadableDatabase();
			
			String query = "Select * FROM " + DATABASE_TSUPER_REWARDS_TABLE;
			Cursor cursor = db.rawQuery(query, null);
			rewardsList = new ArrayList<RewardsModel>();
			
			if (cursor != null && cursor.moveToFirst()) {
				do {
					RewardsModel r = new RewardsModel();
					r.setRewardsImageId(contxt.getResources().getIdentifier(cursor.getString(0), "drawable", contxt.getPackageName()));
					r.setRewardImageName(cursor.getString(1));
					r.setRewardsItemEquivalentPoints(cursor.getString(2));
					rewardsList.add(r);	
				} while(cursor.moveToNext());
			} 
			cursor.close();
		} catch(Exception e) {
			Log.e("","Exception on returnAvailableRewards",e);
		} finally {
			if(db != null) db.close();
		}	
		
		return rewardsList;
	}
	
	// for achievements part
	public List<AchievementsModel> returnAvailableAchievements() {
		
		SQLiteDatabase db = null;
		
		List<AchievementsModel> acList = null;
		try {
			db = this.getReadableDatabase();
			
			String query = "Select * FROM " + DATABASE_TSUPER_ACHIEVEMENTS_TABLE;
			Cursor cursor = db.rawQuery(query, null);
			acList = new ArrayList<AchievementsModel>();
			
			if (cursor != null && cursor.moveToFirst()) {
				do {
					AchievementsModel ac = new AchievementsModel();
					//ac.setAchievementId(contxt.getResources().getIdentifier(cursor.getString(0), "drawable", contxt.getPackageName()));
					ac.setAchievementName(cursor.getString(1));
					ac.setAchievementDescription(cursor.getString(2));
					acList.add(ac);
				} while(cursor.moveToNext());
			} 
			cursor.close();
		} catch(Exception e) {
			Log.e("","Exception on returnAvailableAchievements",e);
		} finally {
			if(db != null) db.close();
		}	
		return acList;
	}	
	
	public void addCategoriesAndReports(CategoriesAndReportModel categoriesAndReportModel) {
		SQLiteDatabase db = null;
		try {
	        db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("TSUPER_CATEGORIES_AND_REPORTS_TYPE", String.valueOf(categoriesAndReportModel.getDrawableId()));
			values.put("TSUPER_CATEGORIES_AND_REPORTS_NAME", categoriesAndReportModel.getName());
			values.put("TSUPER_CATEGORIES_AND_REPORTS_POSTED_BY_USER", categoriesAndReportModel.getPostedBy());
			values.put("TSUPER_CATEGORIES_AND_REPORTS_DATE_TIME_POSTING", categoriesAndReportModel.getPostedDate());
			values.put("TSUPER_CATEGORIES_AND_REPORTS_DESCRIPTION", categoriesAndReportModel.getDescription());			
			values.put("TSUPER_CATEGORIES_AND_REPORTS_LONGITUDE", categoriesAndReportModel.getLon());
			values.put("TSUPER_CATEGORIES_AND_REPORTS_LATITUDE", categoriesAndReportModel.getLat());
			
			Log.e("", "adding CategoriesAndReportModel by user:  " + categoriesAndReportModel.getPostedBy());
	        db.insert(DATABASE_TSUPER_CATEGORIES_AND_REPORTS_TABLE, null, values);
	        
		} catch(Exception e) {
			Log.e("","Exception on addCategoriesAndReports", e);
		} finally { 
			if(db != null) db.close();
		}
	}
	
	public List<CategoriesAndReportModel> showCategoriesAndReport(int categoryAndReportType) {
		
		SQLiteDatabase db = null;
		List<CategoriesAndReportModel> categoriesAndReportModelsList = null;
		try {
			db = this.getReadableDatabase();
			
			String query = "Select * FROM " + DATABASE_TSUPER_CATEGORIES_AND_REPORTS_TABLE + " WHERE TSUPER_CATEGORIES_AND_REPORTS_TYPE = '" + String.valueOf(categoryAndReportType) + "'";
			Cursor cursor = db.rawQuery(query, null);
			categoriesAndReportModelsList = new ArrayList<CategoriesAndReportModel>();
			
			if (cursor != null && cursor.moveToFirst()) {
				do {
					
					CategoriesAndReportModel car = new CategoriesAndReportModel();
					car.setDrawableId(Integer.parseInt(cursor.getString(0)));
					car.setName(cursor.getString(1));
					car.setPostedBy(cursor.getString(2));
					car.setPostedDate(cursor.getString(3));
					car.setDescription(cursor.getString(4));
					car.setLon(cursor.getString(5));
					car.setLat(cursor.getString(6));
					categoriesAndReportModelsList.add(car);
							
				} while(cursor.moveToNext());
			} 
			cursor.close();
		} catch(Exception e) {
			Log.e("","Exception on showCategoriesAndReport",e);
		} finally {
			if(db != null) db.close();
		}	
		
		return categoriesAndReportModelsList;
	}
}

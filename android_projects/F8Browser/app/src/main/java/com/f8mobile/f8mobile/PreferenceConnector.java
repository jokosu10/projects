package com.f8mobile.f8mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceConnector{
	public static final int MODE = Context.MODE_PRIVATE;
	public static final String PREF_NAME = "PEOPLE_PREFERENCES";
	public static final String GCM_REG_ID = "GCM_REG_ID"; 
	public static final String CLIENT_ID = "CLIENTID";
	public static final String NAME = "NAME";
	public static final String SERIAL = "SERIAL";
	public static final String GENDER = "GENDER";
	public static final String AGE = "AGE";
	public static final String CIVIL_STATUS = "CIVIL_STATUS";
	public static final String ADDRESS = "ADDRESS";
	public static final String LOCATION = "LOCATION";
	public static final String LOCATION_LONG = "LOCATION_LONG";
	public static final String LOCATION_LAT = "LOCATION_LAT";
	public static final String PROVINCE = "PROVINCE";
	public static final String TOWN = "TOWN";
	public static final String EARNINGS = "EARNINGS";	
	public static final String IDLESTATUS = "IDLESTATUS";
	public static final String USERNAME = "USERNAME";
	public static final String PASSWORD = "PASSWORD";
	public static final String DEF_USER = "DEF_USER";
	public static final String DEF_PASS = "DEF_PASS";
	public static final String NEW_USER = "NEW_USER";
	public static final String NEW_PASS = "NEW_PASS";
	public static final String FIRST_TIME_SYNC = "FIRST_TIME_SYNC";
	public static final String SYNC_FINISHED = "SYNC_FINISHED";
	public static final String SYNC_RUNNING = "SYNC_RUNNING";
	public static final String ALARM_FOR_SYNC_IS_SET = "ALARM_FOR_SYNC_IS_SET";

	public static void writeBoolean(Context context, String key, boolean value) {
		getEditor(context).putBoolean(key, value).commit();
	}

	public static boolean readBoolean(Context context, String key, boolean defValue) {
		return getPreferences(context).getBoolean(key, defValue);
	}

	public static void writeInteger(Context context, String key, int value) {
		getEditor(context).putInt(key, value).commit();

	}

	public static int readInteger(Context context, String key, int defValue) {
		return getPreferences(context).getInt(key, defValue);
	}

	public static void writeString(Context context, String key, String value) {
		getEditor(context).putString(key, value).commit();

	}
	
	public static String readString(Context context, String key, String defValue) {
		return getPreferences(context).getString(key, defValue);
	}
	
	public static void writeFloat(Context context, String key, float value) {
		getEditor(context).putFloat(key, value).commit();
	}

	public static float readFloat(Context context, String key, float defValue) {
		return getPreferences(context).getFloat(key, defValue);
	}
	
	public static void writeLong(Context context, String key, long value) {
		getEditor(context).putLong(key, value).commit();
	}

	public static long readLong(Context context, String key, long defValue) {
		return getPreferences(context).getLong(key, defValue);
	}

	public static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences(PREF_NAME, MODE);
	}

	public static Editor getEditor(Context context) {
		return getPreferences(context).edit();
	}

}

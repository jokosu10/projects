package com.example.pin_o_memo;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class AddReminder extends Activity {

	private ShareActionProvider mShareActionProvider;
	
	TextView tv_location;
	TextView tv_latitude;
	TextView tv_longitude;
	TextView tv_duedate;
	TextView tv_duetime;
	
	EditText et_rem;
	EditText et_note;
	
	CheckBox cb_complete;
	CheckBox cb_bookmark;
	
	int mm_final;
	int dd_final;
	int yy_final;
	int h_final;
	int m_final;
	int lat;
	int lng;
	
	String final_loc;

	String location_lat;
	String location_lng;
	String notes="";
	String loc="";
	String cmplt="";
	
	private String id = "2";
	
	static final int DATE_DIALOG_ID = 1;
	static final int TIME_DIALOG_ID = 2;
	
	PendingIntent pendingIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_reminder);
		
		tv_location = (TextView) findViewById(R.id.tv_location);
		tv_duedate = (TextView) findViewById(R.id.tv_duedate);
		tv_duetime = (TextView) findViewById(R.id.tv_duetime);
		
		et_rem = (EditText) findViewById(R.id.et_rem);
		et_note = (EditText) findViewById(R.id.et_note);
		
		Button btn_back = (Button) findViewById(R.id.btn_back);
		Button btn_add = (Button) findViewById(R.id.btn_add);
		
		cb_complete = (CheckBox) findViewById(R.id.cb_complete);
		//cb_bookmark = (CheckBox) findViewById(R.id.cb_bookmark);
		
		Intent i = getIntent();
		Bundle bundle = i.getParcelableExtra("bundle");
		final LatLng location = bundle.getParcelable("last_location");
		final String markerTitle = bundle.getString("markerTitle");
		
		location_lat = String.valueOf(location.latitude);
		location_lng = String.valueOf(location.longitude);
		
		et_rem.setText(markerTitle);
		
		setTitle(markerTitle);
		
		Uri uri = Uri.parse(LocationsContentProvider.CONTENT_URI + "/" + id);
		Cursor cursor = getContentResolver().query(uri, 
				new String[] {
					LocationsDB.FIELD_COMPLETED,
					LocationsDB.FIELD_LOCATION, 
					LocationsDB.FIELD_NOTES,
					LocationsDB.FIELD_MONTH, 
					LocationsDB.FIELD_DAY, 
					LocationsDB.FIELD_YEAR,
					LocationsDB.FIELD_HOUR,
					LocationsDB.FIELD_MINUTE},
				 "lat=? AND lng=?", new String[] {location_lat, location_lng}, null);
		
		if( cursor != null && cursor.moveToFirst() ){
			mm_final = cursor.getInt(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_MONTH));
			mm_final = mm_final-1;
			dd_final = cursor.getInt(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_DAY));
			yy_final = cursor.getInt(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_YEAR));
			h_final = cursor.getInt(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_HOUR));
			m_final = cursor.getInt(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_MINUTE));
			notes = cursor.getString(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_NOTES));
			loc = cursor.getString(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_LOCATION));
			cmplt = cursor.getString(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_COMPLETED));
			cursor.close();
		}
		else{
			final Calendar c = Calendar.getInstance();
    		yy_final = c.get(Calendar.YEAR);
    		mm_final = c.get(Calendar.MONTH);
    		dd_final = c.get(Calendar.DAY_OF_MONTH);
    		h_final = c.get(Calendar.HOUR_OF_DAY);
    		m_final = c.get(Calendar.MINUTE);
		}
		
		
		
		
		String month = new DateFormatSymbols().getMonths()[mm_final];
		
		tv_duedate.setText(new StringBuilder()
		.append(month).append(" ")
		.append(dd_final).append(", ")
		.append(yy_final));
		
		tv_duetime.setText(new StringBuilder()
		.append(h_final).append(":")
		.append(m_final));
		
		et_note.setText(notes);
		
		if (loc == null || loc == "")
			new ReverseGeocodingTask(getBaseContext()).execute(location);
		else
			tv_location.setText(loc);
		
		if(cmplt == null || cmplt == "" || cmplt == " ")
			cb_complete.setChecked(false);
		else
			cb_complete.setChecked(true);
		
		cb_complete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(cb_complete.isChecked()){
					createDialog();
				}
				
			}
		});
		
		/*cb_bookmark.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				if(cb_bookmark.isChecked()){
					ContentValues cv = new ContentValues();
					cv.put(BookmarksDB.FIELD_LOC, tv_location.getText().toString());
					getContentResolver().insert(BookmarksContentProvider.CONTENT_URI, cv);
				}
				
			}
		});*/
		
		btn_add.setOnClickListener(new OnClickListener(){			
			@Override
			public void onClick(View v){
				
				ContentValues cv = new ContentValues();
				cv.put(LocationsDB.FIELD_TITLE, et_rem.getText().toString());
				cv.put(LocationsDB.FIELD_MONTH, mm_final+1);
				cv.put(LocationsDB.FIELD_DAY, dd_final);
				cv.put(LocationsDB.FIELD_YEAR, yy_final);
				cv.put(LocationsDB.FIELD_HOUR, h_final);
				cv.put(LocationsDB.FIELD_MINUTE, m_final);
				cv.put(LocationsDB.FIELD_NOTES, et_note.getText().toString());
				cv.put(LocationsDB.FIELD_LOCATION, tv_location.getText().toString());
				
				if(cb_complete.isChecked())
					cv.put(LocationsDB.FIELD_COMPLETED, "(Completed)");
				else
					cv.put(LocationsDB.FIELD_COMPLETED, "");
				
				getContentResolver().update(LocationsContentProvider.CONTENT_URI, cv, "lat=? AND lng=?", new String[] {location_lat, location_lng});
				Toast.makeText(getBaseContext(), "Changes have been made", Toast.LENGTH_SHORT).show();
				mShareActionProvider.setShareIntent(getDefaultShareIntent());
				
				 Calendar calendar = Calendar.getInstance();
			     
			     calendar.set(Calendar.MONTH, mm_final);
			     calendar.set(Calendar.YEAR, yy_final);
			     calendar.set(Calendar.DAY_OF_MONTH, dd_final);
			 
			     calendar.set(Calendar.HOUR_OF_DAY, h_final);
			     calendar.set(Calendar.MINUTE, m_final);
			     
			     Intent myIntent = new Intent(AddReminder.this, MyReceiver.class);
			     myIntent.putExtra("lat", location_lat);
			     myIntent.putExtra("lng", location_lng);
			     pendingIntent = PendingIntent.getBroadcast(AddReminder.this, 0, myIntent,0);
			     
			      AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
			      alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
			}
			
		});
		
		btn_back.setOnClickListener(new OnClickListener(){
			
			@Override
        	public void onClick(View v){
				Intent resultIntent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putParcelable("last_location", location);
				resultIntent.putExtra("bundle", bundle);
				resultIntent.putExtra("newReminder", et_rem.getText().toString());
				
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			}
			
		});
		
		tv_duedate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showDialog(DATE_DIALOG_ID);
			}
		});
		
		tv_duetime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showDialog(TIME_DIALOG_ID);
				
			}
		});
				
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			
			// Use the current date as default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(this, datePickerListener, year, month, day);
			
		case TIME_DIALOG_ID:
			final Calendar cc = Calendar.getInstance();
			int hour  = cc.get(Calendar.HOUR_OF_DAY);
			int minute = cc.get(Calendar.MINUTE);
			return new TimePickerDialog(this, timePickerListener, hour, minute, DateFormat.is24HourFormat(this));
		}
		return null;
	}
	
	private void createDialog(){
		final AlertDialog.Builder alert = new AlertDialog.Builder(this)
		.setTitle("Share")
		.setMessage("Do you want to share the completion of this task?")
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				String message = "";
				message += "I have completed a task: " + et_rem.getText();
				message += " at " + tv_location.getText();
				message  += ". Notes: " + et_note.getText();
				
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, message);
				sendIntent.setType("text/plain");
				startActivity(Intent.createChooser(sendIntent, "Share it to.."));
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
	alert.show();
	}
	
	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
		
		public void onTimeSet(TimePicker arg0, int hour, int minute) {
			
			h_final = hour;
			m_final = minute;
			
			tv_duetime.setText(new StringBuilder()
					.append(h_final).append(":")
					.append(m_final));
			
		}
	};
	
	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int yy, int mm, int dd) {
			
			mm_final = mm;
			dd_final = dd;
			yy_final = yy;
			
			String month = new DateFormatSymbols().getMonths()[mm];
			
			tv_duedate.setText(new StringBuilder()
			.append(month).append(" ")
			.append(dd).append(", ")
			.append(yy));
		}
	};
	
	private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String>{
    	Context mContext;
    	
    	public ReverseGeocodingTask(Context context){
    		super();
    		mContext = context;
    	}
    	
    	// Finding address using reverse geocoding
    	@Override
    	protected String doInBackground(LatLng... params){
    		Geocoder geocoder = new Geocoder(mContext);
    		double latitude = params[0].latitude;
    		double longitude = params[0].longitude;
    		
    		List<Address> addresses = null;
    		String addressText="";
    		
    		try{
    			addresses = geocoder.getFromLocation(latitude, longitude, 1);
    		} catch (IOException e){
    			e.printStackTrace();
    		}
    		
    		if(addresses != null && addresses.size() > 0){
    			Address address = addresses.get(0);
    			
    			addressText = String.format("%s, %s, %s",
    					address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
    					address.getLocality(),
    					address.getCountryName());
    		}
    		
    		return addressText;
    	}
    	
    	@Override
    	protected void onPostExecute(String addressText){
    		tv_location.setText(addressText);
    	}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_reminder, menu);
		
		MenuItem item = menu.findItem(R.id.menu_item_share);
		mShareActionProvider = (ShareActionProvider) item.getActionProvider();
		return true;
	}

	private Intent getDefaultShareIntent(){
		Intent intent = new Intent(Intent.ACTION_SEND);
		
		String message = "";
		message += "Reminder: " + et_rem.getText();
		message += ". Location: " + tv_location.getText();
		message  += ". Notes: " + et_note.getText();
		
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, message);
		return intent;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

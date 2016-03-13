package com.example.pin_o_memo;

import java.text.DateFormatSymbols;
import java.util.Calendar;




import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.TextView;

public class EditReminder extends Activity{
	
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
	String ttl;
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
		
		Intent i = getIntent();
		int rowID = i.getIntExtra("rowID", 0);
		
		Uri uri = Uri.parse(LocationsContentProvider.CONTENT_URI + "/" + id);
		Cursor cursor = getContentResolver().query(uri, 
				new String[] {
					LocationsDB.FIELD_TITLE,
					LocationsDB.FIELD_LAT,
					LocationsDB.FIELD_LNG,
					LocationsDB.FIELD_COMPLETED,
					LocationsDB.FIELD_LOCATION, 
					LocationsDB.FIELD_NOTES,
					LocationsDB.FIELD_MONTH, 
					LocationsDB.FIELD_DAY, 
					LocationsDB.FIELD_YEAR,
					LocationsDB.FIELD_HOUR,
					LocationsDB.FIELD_MINUTE},
				 "_id=?", new String[] {String.valueOf(rowID)}, null);
		
		if( cursor != null && cursor.moveToFirst() ){
			mm_final = cursor.getInt(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_MONTH));
			mm_final = mm_final-1;
			dd_final = cursor.getInt(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_DAY));
			yy_final = cursor.getInt(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_YEAR));
			h_final = cursor.getInt(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_HOUR));
			m_final = cursor.getInt(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_MINUTE));
			notes = cursor.getString(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_NOTES));
			loc = cursor.getString(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_LOCATION));
			ttl = cursor.getString(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_TITLE));
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
		
		et_rem.setText(ttl);
		
		setTitle(ttl);
		
String month = new DateFormatSymbols().getMonths()[mm_final];
		
		tv_duedate.setText(new StringBuilder()
		.append(month).append(" ")
		.append(dd_final).append(", ")
		.append(yy_final));
		
		tv_duetime.setText(new StringBuilder()
		.append(h_final).append(":")
		.append(m_final));
		
		et_note.setText(notes);
		
		
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

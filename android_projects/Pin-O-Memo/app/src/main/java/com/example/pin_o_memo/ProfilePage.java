package com.example.pin_o_memo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ProfilePage extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

	private SimpleCursorAdapter dataAdapter;
	
	
	ImageView iv_profile;
	TextView tv_name;
	Bitmap bmp;
	String existing;
	String name;
	ImageButton bt_edit;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_page);
		
		displayReminderView();
		//displayBookmarkView();
		
		tv_name = (TextView) findViewById(R.id.tv_name);
		iv_profile = (ImageView) findViewById(R.id.iv_profile);
		bt_edit = (ImageButton) findViewById(R.id.bt_edit);
		
		Uri uri = ProfileContentProvider.CONTENT_URI;
		Cursor cursor = getContentResolver().query(uri, 
				new String[] {ProfileDB.FIELD_NAME},
				 "_id=?", new String[] {"1"}, null);
		
		if( cursor != null && cursor.moveToFirst() ){
			name = 	cursor.getString(cursor.getColumnIndexOrThrow(ProfileDB.FIELD_NAME));
			cursor.close();
		}
		
		tv_name.setText(name);
		
		bt_edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				createDialog();
			}
		});
		
	}
	
	private void createDialog(){
		final View view = LayoutInflater.from(this).inflate(R.layout.my_edit_text, null);
		final EditText input = (EditText) view.findViewById(R.id.myid);
		final AlertDialog.Builder alert = new AlertDialog.Builder(this)
			.setTitle("Edit Name")
			.setMessage("Name:")
			.setView(view)
			.setPositiveButton("Save", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String value = input.getText().toString();
					tv_name.setText(value);
					ContentValues cv = new ContentValues();
					cv.put(ProfileDB.FIELD_NAME, value);
					getContentResolver().update(ProfileContentProvider.CONTENT_URI, cv, "_id=?", new String[] {"1"});
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
		alert.show();
	}
	
	private void displayReminderView(){
		String[] columns = new String[]{
			LocationsDB.FIELD_TITLE,
			LocationsDB.FIELD_COMPLETED,
			LocationsDB.FIELD_LOCATION,
			LocationsDB.FIELD_NOTES,
			LocationsDB.FIELD_MONTH,
			LocationsDB.FIELD_DAY,
			LocationsDB.FIELD_YEAR,
			LocationsDB.FIELD_HOUR,
			LocationsDB.FIELD_MINUTE
		};
		
		int[] to = new int[]{
			R.id.name,
			R.id.tv_complete,
			R.id.location,
			R.id.notes,
			R.id.month,
			R.id.day,
			R.id.year,
			R.id.hour,
			R.id.min
		};
		
		dataAdapter = new SimpleCursorAdapter(this, R.layout.reminder_info, null, columns, to, 0);
		
		ListView listView = (ListView) findViewById(R.id.reminderList);
		dataAdapter.notifyDataSetChanged();
		listView.setAdapter(dataAdapter);
		getLoaderManager().initLoader(0, null, this);
		
		/*listView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> listView, View view, int position, long id){
				
				Cursor cursor = (Cursor) listView.getItemAtPosition(position);
				
				int rowID = cursor.getInt(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_ROW_ID));
				String title = cursor.getString(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_TITLE));
				Toast.makeText(getApplicationContext(), title, Toast.LENGTH_SHORT).show();
				
				Intent i = new Intent(getApplicationContext(), EditReminder.class);
				i.putExtra("rowID", rowID);
				//startActivity(i);
				
			}
			
		});*/
	}
	
	public Loader<Cursor> onCreateLoader(int id, Bundle args){
    	
        	Uri uri = LocationsContentProvider.CONTENT_URI;
        	String[] projection = {
        			LocationsDB.FIELD_ROW_ID,
        			LocationsDB.FIELD_TITLE, 
        			LocationsDB.FIELD_COMPLETED, 
        			LocationsDB.FIELD_LOCATION,
        			LocationsDB.FIELD_MONTH,
        			LocationsDB.FIELD_DAY,
        			LocationsDB.FIELD_YEAR,
        			LocationsDB.FIELD_HOUR,
        			LocationsDB.FIELD_MINUTE,
        			LocationsDB.FIELD_NOTES};
        	return new CursorLoader(this, uri, projection, null, null, null);
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data){	
		dataAdapter.swapCursor(data);
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader){
		dataAdapter.swapCursor(null);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		getLoaderManager().restartLoader(0, null, this);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile_page, menu);
		return true;
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

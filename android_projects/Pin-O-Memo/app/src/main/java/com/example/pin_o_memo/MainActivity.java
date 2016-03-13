package com.example.pin_o_memo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.ContentsResult;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class MainActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, OnMyLocationChangeListener/*, LoaderCallbacks<Cursor>*/{
	
	// Get a handle to the Map Fragment
	GoogleMap map;
	MarkerOptions markerOptions;
	LatLng latLng;
	AutoCompleteTextView atvPlaces;
	PlacesTask placesTask;
	ParserTask parserTask;
	TextView tvLocation;
	Marker marker;
	LocationManager locationManager;
	PendingIntent pendingIntent;
	static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
	String final_loc;
	private static final int RC_SIGN_IN = 0;
	private GoogleApiClient mGoogleApiClient;
	private boolean mIntentInProgress;
	
	private String id = "2";
	 String email;
	private static final String TAG = "MainActivity";
    private GoogleApiClient api;
    private boolean mResolvingError = false;
    private DriveFile mfile;
    private static final int DIALOG_ERROR_CODE =100; 
    private static final String DATABASE_NAME = "locationssqlite.db";
    private static final String GOOGLE_DRIVE_FILE_NAME = "sqlite_db_backup";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        api = new GoogleApiClient.Builder(this)
        	.addApi(Drive.API)
        	.addScope(Drive.SCOPE_FILE)
        	.addConnectionCallbacks(this)
        	.addOnConnectionFailedListener(this)
        	.build();
        
        mGoogleApiClient = new GoogleApiClient.Builder(this)
        	.addConnectionCallbacks(this)
        	.addOnConnectionFailedListener(this)
        	.addApi(Plus.API)
        	.addScope(Plus.SCOPE_PLUS_LOGIN)
        	.build();
        	
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        if(bundle != null){
        }
        
        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        
        // Showing status
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
        	
        	int requestCode = 10;
        	Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
        	dialog.show();
        }
        else{ // Google Play Services are available

        	map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        	
        	ImageButton btn_find = (ImageButton) findViewById(R.id.btn_find);
        	ImageButton btn_prof = (ImageButton) findViewById(R.id.btn_profile);
        	//Button btn_db = (Button) findViewById(R.id.btn_db);
        	tvLocation = (TextView) findViewById(R.id.tv_location);
        	atvPlaces = (AutoCompleteTextView) findViewById(R.id.et_location);
            
        	atvPlaces.setThreshold(1);
        	
        	// Enabling MyLocation Layer of Google Map
            map.setMyLocationEnabled(true);
            
            // Getting LocationManager object from System Service LOCATION_SERVICE
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            
            //getLoaderManager().initLoader(0, null, this);
            
            // Setting event handler for location change
            map.setOnMyLocationChangeListener(this);

            btn_prof.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(getApplicationContext(), ProfilePage.class);
					startActivity(intent);
					
				}
			});
            
            /*btn_db.setOnClickListener(new OnClickListener() {
            	
            	public void onClick(View v) {
            		Intent dbmanager = new Intent(getApplicationContext(),AndroidDatabaseManager.class);
                    startActivity(dbmanager);
                }
            });*/
            
            map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            	
            	@Override
                public void onInfoWindowClick(Marker arg0) {
                    Intent i = new Intent(getApplicationContext(), AddReminder.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("markerTitle", arg0.getTitle());
                    bundle.putParcelable("last_location", arg0.getPosition());
                    i.putExtra("bundle", bundle);
                    arg0.remove();
                    startActivityForResult(i, 1);

                }
            });
            
            atvPlaces.addTextChangedListener(new TextWatcher() {
            	 
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    placesTask = new PlacesTask();
                    placesTask.execute(s.toString());
                }
     
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
                    // TODO Auto-generated method stub
                }
     
                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub
                }
            });	
            
            btn_find.setOnClickListener(new OnClickListener(){
            	
            	@Override
            	public void onClick(View v){
            	
            		// Getting reference to EditText to get user input location
            		EditText etLocation = (EditText) findViewById(R.id.et_location);
            		
            		// Getting user input location
            		String location = etLocation.getText().toString();
            		
            		if(location!=null && !location.equals("")){
            			new GeocoderTask().execute(location);
            		}
            		
            	}
            	
            });
            
            // Setting a click event handler for the map
            map.setOnMapClickListener(new OnMapClickListener(){
            	
            	@Override
            	public void onMapClick(LatLng arg0){
            		
            		// Getting the latitude and longitude of the touched location
            		latLng = arg0;
            		
            		drawMarker(latLng);
            		
            		drawCircle(latLng);
            		
            		// This intent will call the activity ProximityActivity
            		Intent proximityIntent = new Intent(MainActivity.this, LocationReceiver.class);
            		proximityIntent.putExtra("lat", latLng.latitude);
            		proximityIntent.putExtra("lng", latLng.longitude);
            		pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, proximityIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
            		
            		// Setting proximity alert
            		// The pending intent will be invoked when the device enters or exits the 
            		// region 20 meters away from the marked point
            		// The -1 indicates that, the monitor will not be expired
            		locationManager.addProximityAlert(latLng.latitude, latLng.longitude, 50, -1, pendingIntent);
            		
            		final Calendar c = Calendar.getInstance();
            		int yy_final = c.get(Calendar.YEAR);
            		int mm_final = c.get(Calendar.MONTH);
            		int dd_final = c.get(Calendar.DAY_OF_MONTH);
            		int hh_final = c.get(Calendar.HOUR_OF_DAY);
            		int m_final = c.get(Calendar.MINUTE);
            		String month = new DateFormatSymbols().getMonths()[mm_final];
            		
            		// Creating an instance of ContentValues
            		ContentValues contentValues = new ContentValues();
            		
            		// Setting latitude in ContentValues
            		contentValues.put(LocationsDB.FIELD_LAT, latLng.latitude);
            		contentValues.put(LocationsDB.FIELD_LNG, latLng.longitude);
            		contentValues.put(LocationsDB.FIELD_ZOOM, map.getCameraPosition().zoom);
            		contentValues.put(LocationsDB.FIELD_TITLE, " ");
            		contentValues.put(LocationsDB.FIELD_MONTH, mm_final+1);
            		contentValues.put(LocationsDB.FIELD_DAY, dd_final);
            		contentValues.put(LocationsDB.FIELD_YEAR, yy_final);
            		contentValues.put(LocationsDB.FIELD_HOUR, hh_final);
            		contentValues.put(LocationsDB.FIELD_MINUTE, m_final);
            		contentValues.put(LocationsDB.FIELD_EMAIL, email);
            		
            		// Creating an instance of LocationInsertTask
            		LocationInsertTask insertTask = new LocationInsertTask();
            		
            		// Storing the latitude, longitude and zoom level to SQLite database
            		insertTask.execute(contentValues);
            		
            		Drive.DriveApi.newContents(api).setResultCallback(contentsCallback);
            		
            		Toast.makeText(getBaseContext(), "Marker is added to the Map", Toast.LENGTH_SHORT).show();
            	}
            });
            
            map.setOnMapLongClickListener(new OnMapLongClickListener() {
				
				@Override
				public void onMapLongClick(LatLng arg0) {
					
					Intent proximityIntent = new Intent("com.example.pin_o_memo.activity.proximity");
					
					pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, proximityIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
					
					// Removing the proximity alert
					locationManager.removeProximityAlert(pendingIntent);
					
					AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

					Intent myIntent = new Intent(MainActivity.this, MyReceiver.class);
					pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent,0);
					
					alarmManager.cancel(pendingIntent);
					
					// Removing all markers from the Google Map
					map.clear();
					
					// Creating an instance of LocationDeleteTask
					LocationDeleteTask deleteTask = new LocationDeleteTask();
					
					// Deleting all the rows from SQLite Database
					deleteTask.execute();
					
					Toast.makeText(getBaseContext(), "All markers are removed", Toast.LENGTH_LONG).show();
					
				}
			});
            
        }
    }
    
    private void drawCircle(LatLng latLng){
    	
    	// Instantiating CircleOptions to draw a circle around the marker
    	CircleOptions circleOptions = new CircleOptions();
    	
    	// Specifying the center of the circle
    	circleOptions.center(latLng);
    	
    	// Radius
    	circleOptions.radius(50);
    	
    	// Border color
    	circleOptions.strokeColor(Color.BLACK);
    	
    	// Border width
    	circleOptions.strokeWidth(2);
    	
    	// Fill color of the circle
    	circleOptions.fillColor(0x30ff0000);
    	
    	// Adding circle to the Google Map
    	map.addCircle(circleOptions);
    }
    
    private void drawMarker(LatLng latLng){
    	// Creating an instance of MarkerOptions
        markerOptions = new MarkerOptions();
 
        // Setting latitude and longitude for the marker
        markerOptions.position(latLng);
        markerOptions.title("Add Reminder");
 
        // Adding marker on the Google Map
        marker = map.addMarker(markerOptions);
        
        
        // Adding marker on the touched location with address
		// new ReverseGeocodingTask(getBaseContext()).execute(latLng);
    }
    
    private void drawMarker_load(LatLng latLng, String ttl){
    	// Creating an instance of MarkerOptions
        markerOptions = new MarkerOptions();
 
        // Setting latitude and longitude for the marker
        markerOptions.position(latLng);
        markerOptions.title(ttl);
 
        // Adding marker on the Google Map
        marker = map.addMarker(markerOptions);
        
        
        // Adding marker on the touched location with address
		new ReverseGeocodingTask(getBaseContext()).execute(latLng);
    }
    
    private class LocationInsertTask extends AsyncTask<ContentValues, Void, Void>{
    	
    	@Override
    	protected Void doInBackground(ContentValues... contentValues){
    		/** Setting up values to insert the clicked location into SQLite database */
    		getContentResolver().insert(LocationsContentProvider.CONTENT_URI, contentValues[0]);
    		return null;
    	}
    }
    
    private class LocationDeleteTask extends AsyncTask<Void, Void, Void>{
    	@Override
    	protected Void doInBackground(Void... params){
    		/** Deleting all the locations stored in SQLite Database */
    		getContentResolver().delete(LocationsContentProvider.CONTENT_URI, null, null);
    		return null;
    	}
    }
    
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
 
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
 
            // Connecting to url
            urlConnection.connect();
 
            // Reading data from url
            iStream = urlConnection.getInputStream();
 
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
 
            StringBuffer sb = new StringBuffer();
 
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
 
            data = sb.toString();
 
            br.close();
 
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }	
    
 // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String>{
 
        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";
 
            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=AIzaSyCtV6HKePkiqHbZLkYKW7QX5I3zF353n1Q";
 
            String input="";
 
            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
 
            // place type to be searched
            String types = "types=geocode";
 
            // Sensor enabled
            String sensor = "sensor=false";
 
            // Building the parameters to the web service
            String parameters = input+"&"+types+"&"+sensor+"&"+key;
 
            // Output format
            String output = "json";
 
            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;
 
            try{
                // Fetching the data from we service
                data = downloadUrl(url);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
 
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
 
            // Creating ParserTask
            parserTask = new ParserTask();
 
            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
        }
    }
    
    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{
 
        JSONObject jObject;
 
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {
 
            List<HashMap<String, String>> places = null;
 
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();
 
            try{
                jObject = new JSONObject(jsonData[0]);
 
                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);
 
            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return places;
        }
 
        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
 
            String[] from = new String[] { "description"};
            int[] to = new int[] { android.R.id.text1 };
 
            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);
 
            // Setting the adapter
            atvPlaces.setAdapter(adapter);
        }
    }
    
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>>{
    	
    	@Override
    	protected List<Address> doInBackground(String... locationName){
    		
    		// Creating an instance of Geocoder class
    		Geocoder geocoder = new Geocoder(getBaseContext());
    		List<Address> addresses = null;
    		
    		try{
    			// Getting a maximum of 3 Address that matches the input text
    			addresses = geocoder.getFromLocationName(locationName[0], 3);
    		} catch (IOException e){
    			e.printStackTrace();
    		}
    		return addresses;
    	}
    	
    	@Override
    	protected void onPostExecute(List<Address> addresses){
    		
    		if(addresses==null || addresses.size()==0){
    			Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
    		}
    		
    		// Clears all the existing markers on the map
    		// map.clear();
    		
    		// Adding markers on google map for each matching address
    		// for(int i=0; i<addresses.size(); i++){
    			
    			Address address = (Address) addresses.get(0);
    			
    			// Creating an instance of geopoint, to display in google map
    			latLng = new LatLng(address.getLatitude(), address.getLongitude());
    			
    			/* String addressText = String.format("%s, %s",
    					address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
    					address.getCountryName());
    			
    			markerOptions = new MarkerOptions();
    			markerOptions.position(latLng);
    			markerOptions.title(addressText);
    			
    			map.addMarker(markerOptions);*/
    			
    			// Locate the first location
    			// if(i==0)
        		float zoom = 19;
        		// Setting the zoom level in the map on last position is clicked
        		map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
    			map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    		// }
    	}
    }
    
    private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String>{
        Context mContext;
 
        public ReverseGeocodingTask(Context context){
            super();
            mContext = context;
        }
 
        // Finding address using reverse geocoding
        @Override
        protected String doInBackground(LatLng... params) {
            Geocoder geocoder = new Geocoder(mContext);
            double latitude = params[0].latitude;
            double longitude = params[0].longitude;
 
            List<Address> addresses = null;
            String addressText="";
 
            try {
                addresses = geocoder.getFromLocation(latitude, longitude,1);
            } catch (IOException e) {
                e.printStackTrace();
            }
 
            if(addresses != null && addresses.size() > 0 ){
                Address address = addresses.get(0);
 
                addressText = String.format("%s, %s, %s",
                address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                address.getLocality(),
                address.getCountryName());
            }
            return addressText;
        }
 
        @Override
        protected void onPostExecute(String addressText) {
        	
        	final_loc = addressText;
 
        }
    }
    
    @Override
    public void onMyLocationChange(Location location){
    	
    	// Getting the latitude of the current location
    	double latitude = location.getLatitude();
    	
    	// Getting the longitude of the current location
    	double longitude = location.getLongitude();
    	
    	// Creating a LatLng object for the current 
    	//LatLng latLng = new LatLng(latitude, longitude);
    	
    	// Showing the current location in Google Map
    	//map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    	
    	// Zoom in the Google Map
    	//map.animateCamera(CameraUpdateFactory.zoomTo(15));
    	
    	tvLocation.setText("Latitude:" + latitude + ", Longitude:" + longitude);
    	
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	switch(requestCode){
    		case(1) : {
    			if(resultCode == Activity.RESULT_OK){
    				String newReminder = data.getStringExtra("newReminder");
    				
    				Bundle bundle = data.getParcelableExtra("bundle");
    				LatLng location = bundle.getParcelable("last_location");
    				
    				
    				markerOptions = new MarkerOptions();
    				markerOptions.position(location);
    				markerOptions.title(newReminder);
    				map.addMarker(markerOptions).showInfoWindow();
    			}
    			break;
    		}
    		
    		case(RC_SIGN_IN) : {
    			mIntentInProgress = false;
    			
    			if(!mGoogleApiClient.isConnecting())
    				mGoogleApiClient.connect();
    		}
    		
    		case(DIALOG_ERROR_CODE) : {
    			mResolvingError = false;
    			
    			if(resultCode == RESULT_OK) { // Error was resolved, now connect to the client if not done so.
                    if(!api.isConnecting() && !api.isConnected()) {
                        api.connect();
                    }
                }
    		}
    		
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    /*@Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1){
    	
    	// Uri to the content provider LocationsContentProvider
    	Uri uri = LocationsContentProvider.CONTENT_URI;
    	
    	// Fetches all the rows from locations table
    	return new CursorLoader(this, uri, null, null, null, null);
    }
    
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1){
    	
    	int locationCount = 0;
    	double lat = 0;
    	double lng = 0;
    	float zoom = 0;
    	String ttl;
    	
    	// Number of locations available in the SQLite database table
    	locationCount = arg1.getCount();
    	
    	// Move the current record pointer to the first row of the table
    	arg1.moveToFirst();
    	
    	for(int i=0; i<locationCount; i++){
    		
    		// Get the latitude
    		lat = arg1.getDouble(arg1.getColumnIndex(LocationsDB.FIELD_LAT));
    		
    		// Get the longitude
    		lng = arg1.getDouble(arg1.getColumnIndex(LocationsDB.FIELD_LNG));
    		
    		// Get the zoom level
    		zoom = arg1.getFloat(arg1.getColumnIndex(LocationsDB.FIELD_ZOOM));
    		
    		ttl = arg1.getString(arg1.getColumnIndex(LocationsDB.FIELD_TITLE));
    		
    		// Creating an instance of LatLng to plot the location in Google Maps
    		LatLng location = new LatLng(lat, lng);
    		
    		drawMarker_load(location,ttl);
    		
    		drawCircle(location);
    		
    		arg1.moveToNext();
    	}
    	
    	if(locationCount>0){
    		// Moving CameraPosition to last clicked position
    		map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lng)));
    		
    		// Setting the zoom level in the map on last position is clicked
    		map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
    	}
    }
    
    @Override
    public void onLoaderReset(Loader<Cursor> arg0){
    	// TODO Auto-generated method stub
    }*/

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
    
    @Override
    protected void onStart(){
    	super.onStart();
    	api.connect();
    	mGoogleApiClient.connect();
    }
    
    @Override
    protected void onStop(){
    	super.onStop();
    	api.disconnect();
    	mGoogleApiClient.disconnect();
    }
    
    @Override
    public void onConnectionFailed(ConnectionResult result){
    	if(!mIntentInProgress && result.hasResolution()){
    		try{
    			mIntentInProgress = true;
    			startIntentSenderForResult(result.getResolution().getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
    		} catch (SendIntentException e){
    			mIntentInProgress = false;
    			mGoogleApiClient.connect();
    		}
    	}
    	
    	if(mResolvingError)
    		return;
    	else if(result.hasResolution()){
    		mResolvingError = true;
    		try{
    			result.startResolutionForResult(this, DIALOG_ERROR_CODE);
    		} catch (SendIntentException e){
    			e.printStackTrace();
    		}
    	}
    	else {
    		ErrorDialogFragment fragment = new ErrorDialogFragment();
    		Bundle args = new Bundle();
    		args.putInt("error", result.getErrorCode());
    		fragment.setArguments(args);
    		fragment.show(getFragmentManager(), "errordialog");
    	}
    }
    
    @Override
    public void onConnected(Bundle connectionHint){
    	
    	getProfileInformation();
    	
    	
    	
    }
    
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                Toast.makeText(this, email, Toast.LENGTH_LONG).show();
                Uri uri = Uri.parse(LocationsContentProvider.CONTENT_URI + "/" + id);
        		Cursor cursor = getContentResolver().query(uri, 
        				new String[] {
        					LocationsDB.FIELD_LAT,
        					LocationsDB.FIELD_LNG,
        					LocationsDB.FIELD_TITLE,
        					LocationsDB.FIELD_ZOOM},
        				 "email=?", new String[] {email}, null);
        		
        		int locationCount = 0;
            	double lat = 0;
            	double lng = 0;
            	float zoom = 0;
            	String ttl;
            	
            	// Number of locations available in the SQLite database table
            	locationCount = cursor.getCount();
            	
            	// Move the current record pointer to the first row of the table
            	cursor.moveToFirst();
            	
            	for(int i=0; i<locationCount; i++){
            		
            		// Get the latitude
            		lat = cursor.getDouble(cursor.getColumnIndex(LocationsDB.FIELD_LAT));
            		
            		// Get the longitude
            		lng = cursor.getDouble(cursor.getColumnIndex(LocationsDB.FIELD_LNG));
            		
            		// Get the zoom level
            		zoom = cursor.getFloat(cursor.getColumnIndex(LocationsDB.FIELD_ZOOM));
            		
            		ttl = cursor.getString(cursor.getColumnIndex(LocationsDB.FIELD_TITLE));
            		
            		// Creating an instance of LatLng to plot the location in Google Maps
            		LatLng location = new LatLng(lat, lng);
            		
            		drawMarker_load(location,ttl);
            		
            		drawCircle(location);
            		
            		cursor.moveToNext();
            	}
            	
            	if(locationCount>0){
            		// Moving CameraPosition to last clicked position
            		map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lng)));
            		
            		// Setting the zoom level in the map on last position is clicked
            		map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
            	}
            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    final private ResultCallback<ContentsResult> contentsCallback = new ResultCallback<ContentsResult>() {
		
		@Override
		public void onResult(ContentsResult result) {
			if(!result.getStatus().isSuccess()){
				Log.v(TAG, "Error while trying to create new file contents");
				return;
			}
			
			String mimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType("db");
			MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
				.setTitle(GOOGLE_DRIVE_FILE_NAME)
				.setMimeType(mimeType)
				.setStarred(true)
				.build();
			Drive.DriveApi.getRootFolder(api)
				.createFile(api, changeSet, result.getContents())
				.setResultCallback(fileCallback);
		}
	};
    
	final private ResultCallback<DriveFileResult> fileCallback = new ResultCallback<DriveFileResult>() {

		@Override
		public void onResult(DriveFileResult result) {
			if(!result.getStatus().isSuccess()){
				Log.v(TAG, "Error while trying to create the file");
				return;
			}
			
			mfile = result.getDriveFile();
			mfile.openContents(api, DriveFile.MODE_WRITE_ONLY, null).setResultCallback(contentsOpenedCallback);
		}
	};
	
	final private ResultCallback<ContentsResult> contentsOpenedCallback = new ResultCallback<ContentsResult>() {

		@Override
		public void onResult(ContentsResult result) {
			
			if (!result.getStatus().isSuccess()) {
                Log.v(TAG, "Error opening file");
                return;
            }

            try {
                FileInputStream is = new FileInputStream(getDbPath());
                BufferedInputStream in = new BufferedInputStream(is);
                byte[] buffer = new byte[8 * 1024];
                Contents content = result.getContents();
                BufferedOutputStream out = new BufferedOutputStream(content.getOutputStream());
                int n = 0;
                while( ( n = in.read(buffer) ) > 0 ) {
                    out.write(buffer, 0, n);
                }

                in.close();
                mfile.commitAndCloseContents(api, content).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status result) {
                        // Handle the response status
                    }
                });
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
			
		}
	};
	
	private File getDbPath() {
        return this.getDatabasePath(DATABASE_NAME);
    }
	
	public void onDialogDismissed() {
        mResolvingError = false;
    }
	
	public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {}

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int errorCode = this.getArguments().getInt("error");
            return GooglePlayServicesUtil.getErrorDialog(errorCode, this.getActivity(), DIALOG_ERROR_CODE);
        }

        public void onDismiss(DialogInterface dialog) {
            ((MainActivity) getActivity()).onDialogDismissed();
        }
    }
	
    @Override
    public void onConnectionSuspended(int cause){
    	mGoogleApiClient.connect();
    }
    
}

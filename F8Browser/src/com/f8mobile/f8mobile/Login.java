package com.f8mobile.f8mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.f8mobile.community_app.mobile.asynctask.GcmAsyncTask;
import com.f8mobile.community_app.mobile.database.SqliteDatabaseHelper;
import com.f8mobile.community_app.mobile.model.UserDataModel;
import com.f8mobile.community_app.mobile.sync.SyncServiceReceiver;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class Login extends Activity implements LocationListener, OnClickListener{
	
	double longitude;
	double latitude;
	double lon;
	double lat;
	Geocoder geocoder;
	int gpsTag;
	
	static LocationManager locationManager;
	boolean alertOn;
	private Context context;
	private ProgressDialog pd;
	Button btnLogin;  
	EditText userName;
	EditText passWord;
	String user;    
	String pass;
	
	/**************GEO FENCING ******************/
	String country;
	String province;
	String town;
	String town1;
	String town2;
	
	String xml;
	XMLParser parser;
	String response_result;
	String response_client_id;
	String response_txtid;
	String response_fname;
	String response_email;
	String response_address;
	String response_bday;
	String response_sex;
	String response_age;
	String response_civil;
	String response_earnings;
	String reponse_trans_id;
	String response_gcm_reg_id;
	
	//for E-Loading
	String default_user;
	String default_pass;
	String new_user;
	String new_pass;
	
	final String KEY_RESPONSE_HEADER = "F8MobileXMLRequest"; // parent node
	final String KEY_RESPONSE_RESULT = "Result";  
	final String KEY_RESPONSE_CLIENT_ID = "client_id";
	final String KEY_RESPONSE_TXTID = "txnid";
	final String KEY_RESPONSE_NAME = "first_name";
	final String KEY_RESPONSE_EMAIL = "email";
	final String KEY_RESPONSE_ADDRESS = "address";
	final String KEY_RESPONSE_BDAY = "birthdate";
	final String KEY_RESPONSE_SEX = "sex";
	final String KEY_RESPONSE_AGE = "age";
	final String KEY_RESPONSE_CIVIL_STATUS = "civil_status";
	final String KEY_RESPONSE_EARNINGS = "earnings";
	final String KEY_RESPONSE_DUSER = "username";
	final String KEY_RESPONSE_DPASS = "password";
	final String KEY_RESPONSE_NUSER = "new_username";
	final String KEY_RESPONSE_NPASS = "new_password";
	final String KEY_RESPONSE_GCMREGID = "gcmRegId";
	
	final String KEY_RESPONSE_VERSION = "version";
	//final String KEY_RESPONSE_MESSAGE = "ResponseMsg";
	
    String provider;
	private boolean firstTimeSync = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      setContentView(R.layout.login);
     
      /********** get Gps location service LocationManager object ***********/
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		/*
		  Parameters :
		     First(provider)    :  the name of the provider with which to register 
		     Second(minTime)    :  the minimum time interval for notifications, in milliseconds. This field is only used as a hint to conserve power, and actual time between location updates may be greater or lesser than this value. 
		     Third(minDistance) :  the minimum distance interval for notifications, in meters 
		     Fourth(listener)   :  a {#link LocationListener} whose onLocationChanged(Location) method will be called for each location update 
    
		*/
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
				0, 
				0, this);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				3000,   // 3 sec
				10, this);
		
		
		/********* After registration onLocationChanged method called periodically after each 3 sec ***********/
		  
      //turnGPSOn(); 
      context = this;
      
      userName = (EditText)findViewById(R.id.user);
      passWord = (EditText)findViewById(R.id.pass);
      
      btnLogin = (Button)findViewById(R.id.btnlogin);
      
      btnLogin.setOnClickListener(this); 
      
      Login.this.alertConnection();
      
   // Getting LocationManager object
      locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

      // Creating an empty criteria object
      Criteria criteria = new Criteria();

      // Getting the name of the provider that meets the criteria
      /*provider = locationManager.getBestProvider(criteria, false);
      
      provider = LocationManager.GPS_PROVIDER;

      if(provider!=null && !provider.equals("")){

          // Get the location from the given provider
          Location location = locationManager.getLastKnownLocation(provider);

          locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 1, this);

          if(location!=null)
              onLocationChanged(location);
          else
              Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();

      }else{
          Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
      }
      
      Toast.makeText(getBaseContext(), "LONG: " + longitude + "\nLAT: " + latitude, Toast.LENGTH_LONG).show();*/
      
        
      //Toast.makeText(getBaseContext(), getUserCountry(this), Toast.LENGTH_LONG).show();
      
      firstTimeSync = PreferenceConnector.readBoolean(Login.this, PreferenceConnector.FIRST_TIME_SYNC, true);
      Log.e("Login","firstTimeSync: " + firstTimeSync);
      
      if(firstTimeSync) {
    	  
    	  firstTimeSync = false;
    	  // read file from assets
    	  // for every record, put in database
    	  // should be run in separate thread
    	  Thread thread = new Thread() {
    		    @Override
    		    public void run() {
    		    	  PreferenceConnector.writeBoolean(Login.this, PreferenceConnector.SYNC_RUNNING, true);
    		    	  PreferenceConnector.writeBoolean(Login.this, PreferenceConnector.SYNC_FINISHED, false);
    		    	  SqliteDatabaseHelper sqliteDatabaseHelper = new SqliteDatabaseHelper(Login.this);
    		    	  BufferedReader reader = null;
    		    	  try {
    		    	      reader = new BufferedReader(
    		    	          new InputStreamReader(getAssets().open("initial_members_list.csv")) , 1024);

    		    	      // do reading, usually loop until end of file reading  
    		    	      String mLine = "";
    		    	      while((mLine = reader.readLine()) != null) {
    		    	    	  
    		    	    	  if(!TextUtils.isEmpty(mLine)) {
    		    	    		  String[] fields = mLine.split("\",\"", -1);
        		    	    	  UserDataModel userDataModel = new UserDataModel();
        		    	    	  userDataModel.setId(fields[0].replaceAll("\"", ""));
        		    	    	  userDataModel.setFname(fields[1].replaceAll("\"", ""));
        		    	    	  userDataModel.setLname(fields[2].replaceAll("\"", ""));
        		    	    	  userDataModel.setCellNo(fields[3].replaceAll("\"", ""));
        		    	    	  userDataModel.setGcmRegId(fields[4].replaceAll("\"", ""));
        						  sqliteDatabaseHelper.insertToUsersTable(userDataModel);
    		    	    	  } 
    		    	      }
    		    	  } catch (IOException e) {
    		    		  Log.e("Login","", e);
    		    	  } finally {
    		    	      if (reader != null) {
    		    	           try {
    		    	               reader.close();
    		    	           } catch (IOException e) {
    		    	              Log.e("Login","", e);
    		    	           }
    		    	      }
        		    	  PreferenceConnector.writeBoolean(Login.this, PreferenceConnector.SYNC_RUNNING, false);
        		    	  PreferenceConnector.writeBoolean(Login.this, PreferenceConnector.SYNC_FINISHED, true);
        		    	  PreferenceConnector.writeBoolean(Login.this, PreferenceConnector.FIRST_TIME_SYNC, firstTimeSync);
    		    	  }
    		    }
    		};
    		thread.start();
    	  
      }  else {
    	  boolean recurringAlarmHasSet = PreferenceConnector.readBoolean(Login.this, PreferenceConnector.ALARM_FOR_SYNC_IS_SET, false);
    	  Log.e("Login", "recurringAlarmHasSet" +  recurringAlarmHasSet);
    	  if(recurringAlarmHasSet == false) {
    		  setRecurringAlarm(this); // for scheduled update on local members database
    		  
    	  }
    	 
      }
      
	}
	
	
	private long value(String string) {
	    string = string.trim();
	    if( string.contains( "." )){  	
	        final int index = string.lastIndexOf( "." );
	        return value( string.substring( 0, index ))* 100 + value( string.substring( index + 1 )); 
	    }
	    else {
	        return Long.valueOf( string ); 
	    }
	} 
	
	public void alertConnection(){
		
		
		//LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
	     // boolean statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	      
	      String status = NetworkUtil.getConnectivityStatusString(context);
	      
	      //if (!statusOfGPS || status.equals("3")){
	      if (status.equals("3")){
	    	  alertOn = true;
	    	  //Toast.makeText(getBaseContext(), "Internet Connections or GPS is OFF!!", Toast.LENGTH_LONG).show();
	    	  
	    	  AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
	          builder1.setMessage("Please Turn on Internet Connection.\n");
	          builder1.setCancelable(false);
	          builder1.setPositiveButton("Try Again",
	                  new DialogInterface.OnClickListener() {
	              public void onClick(DialogInterface dialog, int id) {
	            	  Login.this.alertConnection();
	            	  //finish();
	            	  
	                  dialog.cancel();    
	              }
	          });
	          /*builder1.setNegativeButton("No",
	                  new DialogInterface.OnClickListener() {
	              public void onClick(DialogInterface dialog, int id) {
	                  dialog.cancel();
	              }
	          });*/

	          AlertDialog alert11 = builder1.create();
	          alert11.show();
	      }
	      else{
	    	  
	    	  alertOn = false;  
	    	  _getLocation();  
	    	  System.out.println("getting location: Location must print");
	    	  initTask task = new initTask();
		      task.execute();
	    	  
	      }
	}
	
	@Override 
    protected void onDestroy() {
    	if (pd!=null) {
			pd.dismiss();
			btnLogin.setEnabled(true);
		}
    	super.onDestroy();
        //android.os.Process.killProcess(android.os.Process.myPid());
    }
	
	@Override
	public void onClick(View v) {
		v.setEnabled(false);
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			
			@Override
			protected void onPreExecute() {
				pd = new ProgressDialog(context);
				pd.setTitle("Authenticating...");
				pd.setMessage("Please wait.");
				pd.setCancelable(false);
				pd.setIndeterminate(true);
				pd.show();  
			} 
			
			@Override
			protected Void doInBackground(Void... arg0) {
				try {
					//Do something...
					
					Log.d("Button", "Clicked");
		        	user = userName.getText().toString();
		        	pass = passWord.getText().toString();		   
		        	System.out.print("lon " + lon + "\nlat: " + lat);
		        	
		        	if(hasActiveInternetConnection(Login.this)){
		        		System.out.println("connection: " + hasActiveInternetConnection(Login.this));
		        	
		        		try{
		        			// ******* API FOR USERS
		        			String encode_user = URLEncoder.encode(user);
		        			String encode_pass = URLEncoder.encode(pass);
		        			String Url_search = "http://f8mobile.net/web_api/index.php/webservice_api/get_data?username="+encode_user+"&password="+encode_pass+"&longitude="+String.valueOf(lon)+"&latitude="+String.valueOf(lat);
						   	//String Url_String = URLEncoder.encode(Url_search, "utf-8");
				        	final String URL = (Url_search); 
						   	System.out.println("URL: " + Url_search);
						
						
							parser = new XMLParser();
							xml = parser.getXmlFromUrl(URL); // getting XML
							Document doc = parser.getDomElement(xml); // getting DOM element
							
							NodeList nl = doc.getElementsByTagName(KEY_RESPONSE_HEADER);
							Element e = (Element) nl.item(0);
							
							// ******** GET THE RESULT OF API
							response_result = parser.getValue(e, KEY_RESPONSE_RESULT);
							System.out.println(response_result);
							if (response_result.equals("0000"))
							{
								response_client_id = parser.getValue(e, KEY_RESPONSE_CLIENT_ID);
								response_txtid = parser.getValue(e, KEY_RESPONSE_TXTID);
								response_fname = parser.getValue(e, KEY_RESPONSE_NAME);
								response_email = parser.getValue(e, KEY_RESPONSE_EMAIL);
								response_address = parser.getValue(e, KEY_RESPONSE_ADDRESS);
								response_bday = parser.getValue(e, KEY_RESPONSE_BDAY);
								response_sex = parser.getValue(e, KEY_RESPONSE_SEX);
								response_age = parser.getValue(e, KEY_RESPONSE_AGE);
								response_civil = parser.getValue(e, KEY_RESPONSE_CIVIL_STATUS);
								response_earnings = parser.getValue(e, KEY_RESPONSE_EARNINGS);
								response_gcm_reg_id = parser.getValue(e, KEY_RESPONSE_GCMREGID);
								default_user = parser.getValue(e, KEY_RESPONSE_DUSER);
								
								if (response_earnings.equals("")){
									response_earnings = "0";
								}
							} 
							
			        	
			        	// ******* RETURN USER INFO
		        		}catch (Exception e){
		        			
		        			response_result = "2000";
		        		}  
			        	
						//Thread.sleep(5000);  
		        	}else{
		        		response_result = "2000";
		        		System.out.println("connection: " + hasActiveInternetConnection(Login.this));
		        		//Toast.makeText(getApplicationContext(), "Connection Timeout!", Toast.LENGTH_LONG).show();
		        	}
				/*} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					
					e.printStackTrace();*/
				} catch (Exception e){
					response_result = "2000";
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				if (pd!=null) {  
					pd.dismiss(); 
					
					//Toast.makeText(getApplicationContext(), response_result, Toast.LENGTH_LONG).show();
					if (response_result.equals("0000")){
						//System.out.println("-"+response_address + "-");
						if(response_sex.equals(" ") || response_civil.equals(" ") || response_address.equals(" "))
						{
							//Toast.makeText(getApplicationContext(), "Please complete the registration to continue...", Toast.LENGTH_LONG).show();	
							AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
			  	          	builder1.setMessage("Please complete the registration to continue...\n");
			  	          	builder1.setCancelable(false);
			  	          	builder1.setPositiveButton("Register",  
			  	          			new DialogInterface.OnClickListener() {
			  	          				public void onClick(DialogInterface dialog, int id) {
			  	          					//Login.this.alertConnection();
			  	          					System.out.println("http://www.f8mobile.net/subscriptions/personal/personal_form/" + response_txtid);
			  	          					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.f8mobile.net/subscriptions/personal/personal_form/" + response_txtid));
			  	          					startActivity(browserIntent);
			  	          			
			  	          					dialog.cancel();  
			  	          		}
			  	          	});
			  	          	builder1.setNegativeButton("Cancel", 
			  	          			new DialogInterface.OnClickListener() {
			  	          				public void onClick(DialogInterface dialog, int id) {
			  	          					//Login.this.alertConnection();
			  	          					//Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.f8mobile.net"));
			  	          					//startActivity(browserIntent);
			  	          			
			  	          					dialog.cancel();  
			  	          		}
			  	          	});
			  	          	AlertDialog alert11 = builder1.create();
			  	          	alert11.show();
						}
						else{
							// ******* SAVE USER INFO IN PREFERENCES
							
							// compare user's gcm from shared preference to api's gcm reg id response
							String deviceGcm = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.GCM_REG_ID , "");
							if(!TextUtils.isEmpty(deviceGcm)) {
								if(!deviceGcm.equalsIgnoreCase(response_gcm_reg_id)) { // not the same, re-register to gcm server
									Log.e("Login", "gcm regid on this device is not the same as user's existing gcm regid. Re-registering to GCM Server");
									new GcmAsyncTask(getApplicationContext(), GoogleCloudMessaging.getInstance(getApplicationContext()), response_client_id).execute();
								}
							} else {
								Log.e("Login", "No existing gcm regid on this device. Registering to GCM Server");
								new GcmAsyncTask(getApplicationContext(), GoogleCloudMessaging.getInstance(getApplicationContext()), response_client_id).execute();
								
							}
							// end comparison of gcm
							
				        	writeUserDetailsToPrefs(response_client_id, response_fname, user, response_sex, response_age, response_civil, response_address, country, town, province, String.valueOf(lat), String.valueOf(lon), response_earnings);
				        	Log.d("INFO", response_client_id + " - " + response_fname + " - " + user + " - " + response_sex + " - " + response_age + " - " + response_civil + " - " + country + " - " + town + " - " + province + " - " + String.valueOf(lat) + " - " + String.valueOf(lon) + " - " + response_earnings);
				        	// ******* GO TO MAIN PAGE
				        	Intent intent = new Intent(Login.this, MainActivity.class);
			              	Login.this.startActivity(intent);
			                finish();
			                Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();
						}
			        	
					}
					else if (response_result.equals("1000")){
						if (user.equals("") && !pass.isEmpty()){
		        			Toast.makeText(getApplicationContext(), "Please Enter Username", Toast.LENGTH_LONG).show();
		        		}
		        		else if (pass.equals("") && !user.isEmpty()){
		        			Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_LONG).show();
		        		}
		        		else if (user.equals("") && pass.equals("")){
		        			Toast.makeText(getApplicationContext(), "Please Enter Username and Password", Toast.LENGTH_LONG).show();
		        		}
		        		else{
		        			Toast.makeText(getApplicationContext(), "Invalid Username or Password", Toast.LENGTH_LONG).show();
		        		}
					}
					else if(response_result.equals("2000")){
						Toast.makeText(getApplicationContext(), "Connetion Timeout", Toast.LENGTH_LONG).show();
					}
					else if(response_result.equals("3000")){
						Toast.makeText(getApplicationContext(), "User already logged in.", Toast.LENGTH_LONG).show();
					}
					btnLogin.setEnabled(true);
				}
			}
			
		};
		task.execute((Void[])null);
	}
	
	public void writeUserDetailsToPrefs(String client_id, String name, String serial, String gender, String age, String status, String address, String location, String town, String province, String lat, String lon, String earnings){
		
		
		PreferenceConnector.writeString(this, PreferenceConnector.DEF_USER,
				default_user);
		
		PreferenceConnector.writeString(this, PreferenceConnector.USERNAME,
				user);
		
		PreferenceConnector.writeString(this, PreferenceConnector.PASSWORD,
				pass);
		
		// ******* WRITE CLIENT ID
		PreferenceConnector.writeString(this, PreferenceConnector.CLIENT_ID,
				client_id);
		
		// ******* WRITE NAME
		PreferenceConnector.writeString(this, PreferenceConnector.NAME,
				name);
		
		// ******* WRITE SERIAL
		PreferenceConnector.writeString(this, PreferenceConnector.SERIAL,
		serial);
				
		// ******* WRITE GENDER
		PreferenceConnector.writeString(this, PreferenceConnector.GENDER,
				gender);
		
		// ******* WRITE AGE
		PreferenceConnector.writeString(this, PreferenceConnector.AGE,
				age);
				
		// ******* WRITE CIVIL STATUS
		PreferenceConnector.writeString(this, PreferenceConnector.CIVIL_STATUS,
				status);
		
		// ******* WRITE ADDRESS
		PreferenceConnector.writeString(this, PreferenceConnector.ADDRESS,
				address);
		
		// ******* GET LOCATION FIRST ****** //
		/*String profile_province = PreferenceConnector.readString(Login.this,
				PreferenceConnector.PROVINCE, null);*/
		
		// ******* WRITE LOCATION -- APPLIED ON GPS LISTENER --  WRITE LOCATION EVERY TIME USER CHANGE LOCATION FOR LONG LAT
		if (location != null)
		{
			location = location.replace(" ", "");
			PreferenceConnector.writeString(this, PreferenceConnector.LOCATION,
					location);
		}
		
		
		// ******* WRITE LOCATION -- APPLIED ON GPS LISTENER --  WRITE LOCATION EVERY TIME USER CHANGE LOCATION FOR LONG LAT
		if (town != null || country != null)
		{
			if (town != null)
			{
				town = town.replace(" ", "");
			}
			PreferenceConnector.writeString(this, PreferenceConnector.TOWN,
					town);
		}
		
				
		// ******* WRITE LOCATION -- APPLIED ON GPS LISTENER --  WRITE LOCATION EVERY TIME USER CHANGE LOCATION FOR LONG LAT
		if (province != null)
		{
			province = province.replace(" ", "");
			PreferenceConnector.writeString(this, PreferenceConnector.PROVINCE,
					province);
		}
		
		
		// ******* WRITE LOCATION -- APPLIED ON GPS LISTENER --  WRITE LOCATION EVERY TIME USER CHANGE LOCATION FOR LONG LAT
		//System.out.println(lat);
		if (Double.parseDouble(lat) > 0)
		{
			PreferenceConnector.writeString(this, PreferenceConnector.LOCATION_LAT,
					lat);
		}
			
		// ******* WRITE LOCATION -- APPLIED ON GPS LISTENER --  WRITE LOCATION EVERY TIME USER CHANGE LOCATION FOR LONG LAT
		if (Double.parseDouble(lon) > 0)
		{
			PreferenceConnector.writeString(this, PreferenceConnector.LOCATION_LONG,
					lon);
		}
		
		// ******* WRITE EARNINGS
		PreferenceConnector.writeString(this, PreferenceConnector.EARNINGS,
				earnings);
	}
	private static long back_pressed;

	@Override
	public void onBackPressed()
	{
	        if (back_pressed + 2000 > System.currentTimeMillis()) 
	        	super.onBackPressed();
	        else 
	        	Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
	        
	        back_pressed = System.currentTimeMillis();
	        
	}	
	
	@Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	  // Inflate the menu; this adds items 
	                //to the action bar if it is present.
	  getMenuInflater().inflate(R.menu.main, menu);
	  return true;
	 }
	
	/************* Called after each 3 sec **********/
	@Override
	public void onLocationChanged(Location location) {
		   
		String longitude = String.valueOf(location.getLongitude());
		String latitude = String.valueOf(location.getLatitude());
		
		//PreferenceConnector.writeString(this, PreferenceConnector.LOCATION_LONG,
		//		longitude);
		
		//PreferenceConnector.writeString(this, PreferenceConnector.LOCATION_LAT,
		//		latitude);
		if (location.getLatitude() > 0 & location.getLongitude() > 0){
			lon = location.getLongitude();
			lat = location.getLatitude();
		}
		String str = "LOCATION CHANGE: \nLatitude: "+location.getLatitude()+" \nLongitude: "+location.getLongitude();
		
		if (location.getLatitude() > 0)
		{
			PreferenceConnector.writeString(getBaseContext(), PreferenceConnector.LOCATION_LAT,
					String.valueOf(location.getLatitude()));
			System.out.println("LAT: " + location.getLatitude());
		}
			
		// ******* WRITE LOCATION -- APPLIED ON GPS LISTENER --  WRITE LOCATION EVERY TIME USER CHANGE LOCATION FOR LONG LAT
		if (location.getLongitude() > 0)
		{
			PreferenceConnector.writeString(getBaseContext(), PreferenceConnector.LOCATION_LONG,
					String.valueOf(location.getLongitude()));
			System.out.println("LON: " + location.getLongitude());
		}
		
		//Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderDisabled(String provider) {
		
		/******** Called when User off Gps *********/
		
		//Toast.makeText(getBaseContext(), "We advise to turn on your GPS..", Toast.LENGTH_LONG).show();
		
		//gpsTag = 1;
		//turnGPS();
	}
	
	public void turnGPS(){ 
		ActivityManager am = (ActivityManager) this .getSystemService(ACTIVITY_SERVICE);
		  List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		  ComponentName componentInfo = taskInfo.get(0).topActivity;
		  Log.d("TAG", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName()+"   Package Name :  "+componentInfo.getPackageName());
		if (gpsTag == 1 & taskInfo.get(0).topActivity.getClassName().contains("Login")){
			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
	        builder1.setMessage("Please Turn on your GPS!\n");
	        builder1.setCancelable(false);
	        builder1.setPositiveButton("Try Again",
	                new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	          	  Login.this.alertConnection();
	          	  //finish();
	          	  	turnGPS();
	                dialog.cancel();    
	            }
	        });
	        /*builder1.setNegativeButton("No",
	                new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	            }
	        });*/

	        AlertDialog alert11 = builder1.create();
	        alert11.show();
		}
		else if (gpsTag == 1 & !taskInfo.get(0).topActivity.getClassName().contains("Login")){
			Toast.makeText(getBaseContext(), "We advise to turn on your GPS..", Toast.LENGTH_LONG).show();
		}
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
		/******** Called when User on Gps  *********/
		//gpsTag = 0;
		//Toast.makeText(getBaseContext(), "Gps turned on ", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean hasActiveInternetConnection(Context context) {
	    if (isNetworkAvailable()) {
	        try {
	            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
	            urlc.setRequestProperty("User-Agent", "Test");
	            urlc.setRequestProperty("Connection", "close");
	            urlc.setConnectTimeout(1500); 
	            urlc.connect();
	            return (urlc.getResponseCode() == 200);
	        } catch (IOException e) {
	            Log.e("Active Connection", "Error checking internet connection", e);
	        }
	    } else {
	        Log.d("Active Connection", "No network available!");
	    }
	    return false;
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	public class Pair
	{
		String ver_header;
		String ver_result;
		long ver_curVer;
		long ver_appVer;
	}
	
    // ------- start of Async Task -------- //
    private class initTask extends AsyncTask <String, Void, Pair> 
    {
    	
    	/** progress dialog to show user that the backup is processing. */
        //private ProgressDialog dialog;
        
        @Override
		protected Pair doInBackground(String... params) 
        {
        	
        	String version_result = "";
			String version = "";
			long curVer = 0;
    		long appVer = 0;  
        	try{
        		String url_version = "http://f8mobile.net/web_api/index.php/webservice_api/version";
    		   	final String URL = (url_version);
    		   	//System.out.println("http://f8mobile.net/account/index.php/webservice_api/get_data?gender="+profile_gender+"&category="+cat+"&age="+profile_age+"&location=Philippines&day="+day);
    		
    			parser = new XMLParser();
    			xml = parser.getXmlFromUrl(URL); // getting XML
    			Document doc = parser.getDomElement(xml); // getting DOM element
    			
    			NodeList nl = doc.getElementsByTagName(KEY_RESPONSE_HEADER);
    			Element e = (Element) nl.item(0);
    			
    			version_result = parser.getValue(e, KEY_RESPONSE_RESULT);
    			version = parser.getValue(e, KEY_RESPONSE_VERSION);
    			if (version_result.equals("0000"))
    			{
    				String curPackage;
            		curPackage = getPackageName(); 
            		curVer = value(Login.this.getPackageManager().getPackageInfo(curPackage, 0).versionName);
            		appVer = value(version);
    			}
    			else{
    				
    			}
        	}
        	catch(Exception e){
        		
        	}
        	Pair p = new Pair();
	        p.ver_result = version_result;
	        p.ver_curVer = curVer;
	        p.ver_appVer = appVer;
	        //p.response_count = response_count;
		    return p;
        }
        
        @Override
        protected void onPostExecute(Pair p) 
        {
        	//Toast.makeText(getBaseContext(), "RESULT: " + p.ver_result + "\nAVAILABLE VERSION: " + p.ver_appVer + "\nCURRENT VERSION: " + p.ver_curVer, Toast.LENGTH_LONG).show();
        	if (p.ver_curVer < p.ver_appVer){
        		//Toast.makeText(getBaseContext(), "UPDATE GOES HERE", Toast.LENGTH_LONG).show();
        		//Toast.makeText(getBaseContext(), "RESULT: " + p.ver_result + "\nAVAILABLE VERSION: " + p.ver_appVer + "\nCURRENT VERSION: " + p.ver_curVer, Toast.LENGTH_LONG).show();
        	
        		AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
  	          	builder1.setMessage("Update is required!\n");
  	          	builder1.setCancelable(false);
  	          	builder1.setPositiveButton("Go to PlayStore",
  	          			new DialogInterface.OnClickListener() {
  	          		public void onClick(DialogInterface dialog, int id) {
  	          			//Login.this.alertConnection();
  	          			
  	          			try {
  	          				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.f8mobile.f8mobile"));
  	          				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
  	          				startActivity(i);
  	          				finish();
  	          			} catch (android.content.ActivityNotFoundException anfe) {
  	          				//...
  	          			}
  	          			dialog.cancel();  
  	          		}
  	          	});
  	          	AlertDialog alert11 = builder1.create();
  	          	alert11.show();
        	}
        }
        
        @Override
        protected void onPreExecute() 
        {  
           
        }

    } 
    
    // GET INFO FROM TELEPHONY PROVIDER
    /*public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso(); 
            tm.
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            }
            else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        }
        catch (Exception e) { }
        return null;
    }*/
    
    
    private void _getLocation() {
        // Get the location manager
    	System.out.println("GPS Tracking!");
        LocationManager locationManager = (LocationManager) 
                getSystemService(LOCATION_SERVICE);
        
        geocoder = new Geocoder(this);
        
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        LocationListener loc_listener = new LocationListener() {

            public void onLocationChanged(Location l) {
            	
            }

            public void onProviderEnabled(String p) {}

            public void onProviderDisabled(String p) {}

            public void onStatusChanged(String p, int status, Bundle extras) {}
        };
        locationManager
                .requestLocationUpdates(bestProvider, 0, 0, loc_listener);
        location = locationManager.getLastKnownLocation(bestProvider);
        try {
            lat = location.getLatitude();
            lon = location.getLongitude();
        } catch (NullPointerException e) {
            lat = -1.0;
            lon = -1.0;
        }  
        
        //Toast.makeText(getBaseContext(), "LONG: " + lon + " - " + location.getLongitude() + "\nLAT: " + lat + " - " + location.getLatitude(), Toast.LENGTH_LONG).show();
         //<10>    
        try {    
        	List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
        	//List<Address> addresses = geocoder.getFromLocation(14.6022643,121.0393868, 10); 
        	//Toast.makeText(getBaseContext(), "LONG: " + lon + " - " + location.getLongitude() + "\nLAT: " + lat + " - " + location.getLatitude(), Toast.LENGTH_LONG).show();
        	//System.out.println("LONG: " + lon + " - " + location.getLongitude() + "\nLAT: " + lat + " - " + location.getLatitude());
        	System.out.println("\n" + addresses.size());
        	int x = 0;
            for (Address address : addresses) {
              x = x + 1;
              //System.out.println("\n" + address.getAddressLine(0));
              
              if (addresses.size()-x==1){  
            	  System.out.println("region:" + x + " - " + address.getAddressLine(0));
            	  province = address.getAddressLine(0);
            	  if (province.equals("Metro Manila")){
            		  town = town1;
            		    
            	  }
            	  else{
            		  town = town2;
            		  province = town1;
            		  
            	  }
            	  System.out.println("province:" + x + " - " + province);
            	  System.out.println("town:" + x + " - " + town);
              }
              
              if (addresses.size()-x==0){
            	  System.out.println("country:" + x + " - " + address.getAddressLine(0));
            	  country = address.getAddressLine(0);    
              }
              
             
              
              if (addresses.size()-x==2){
            	  town1 = address.getAddressLine(0);
            	  //System.out.println("province:" + x + " - " + town1);      
              }
              
              if (addresses.size()-x==3){
            	  town2 = address.getAddressLine(0);
            	  //System.out.println("city:" + x + " - " + address.getAddressLine(0));
            	      
              }
              
              
            }
        }
        catch(Exception e){  
        	
        }
    }
    
	private void setRecurringAlarm(Login loginActivity) {

         Intent downloader = new Intent(loginActivity, SyncServiceReceiver.class);
         downloader.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         PendingIntent pendingIntent = PendingIntent.getBroadcast(loginActivity, 0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
         AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
         alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 1000, 604800000, pendingIntent); // trigger next week
        // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 1000, 600000, pendingIntent); // trigger 10 minutes (for testing only)
        
	}  
}


package com.example.pin_o_memo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


public class MyLocationService extends Service{
	       public static final String BROADCAST_ACTION = "Hello World";
	       private static final int TWO_MINUTES = 1000 * 60 * 2;
	       public LocationManager locationManager;
	       public MyLocationListener listener;
	       public Location previousBestLocation = null;

	       Intent intent;
	       int counter = 0;
	       
	       String notificationTitle;
	   		String notificationContent;
	   	String tickerMessage;
	   	
	   	private String id = "2";
	   	String title;
	   	int row_id;
	   	

	    @Override
	    public void onCreate() 
	    {
	        super.onCreate();
	        intent = new Intent(BROADCAST_ACTION);
	        
	    }

	    @Override
	    public void onStart(Intent intent, int startId) 
	    {      
	        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	        listener = new MyLocationListener();       
	        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
	        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
	        
	        boolean proximity_entering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);
	        
	        int lat = intent.getIntExtra("lat", 0);
	        int lng = intent.getIntExtra("lng", 0);
	        
	        Uri uri = Uri.parse(LocationsContentProvider.CONTENT_URI + "/" + id);
			Cursor cursor = getContentResolver().query(uri, 
					new String[] {LocationsDB.FIELD_TITLE},
					 "lat=? AND lng=?", new String[] {String.valueOf(lat), String.valueOf(lng)}, null);
			
			if( cursor != null && cursor.moveToFirst() ){
				title = cursor.getString(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_TITLE));
				cursor.close();
			}
	 
	        String strLocation = Double.toString(lat)+","+Double.toString(lng);
	 
	        if(proximity_entering){
	            Toast.makeText(getBaseContext(),"Entering a reminder"  ,Toast.LENGTH_LONG).show();
	            notificationTitle = "Pin-o-Memo - Entry";
	            notificationContent = title;
	            tickerMessage = title;
	        }else{
	            Toast.makeText(getBaseContext(),"Exiting a reminder"  ,Toast.LENGTH_LONG).show();
	            notificationTitle = "Pin-o-Memo - Exit";
	            notificationContent = title;
	            tickerMessage = title;
	        }
	 
	        Intent notificationIntent = new Intent(getApplicationContext(),NotificationView.class);
	 
	        /** Adding content to the notificationIntent, which will be displayed on
	        * viewing the notification
	        */
	        notificationIntent.putExtra("content", notificationContent );
	 
	        /** This is needed to make this intent different from its previous intents */
	        notificationIntent.setData(Uri.parse("tel:/"+ (int)System.currentTimeMillis()));
	 
	        /** Creating different tasks for each notification. See the flag Intent.FLAG_ACTIVITY_NEW_TASK */
	        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
	 
	        /** Getting the System service NotificationManager */
	        NotificationManager nManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
	 
	        /** Configuring notification builder to create a notification */
	        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
	                .setWhen(System.currentTimeMillis())
	                .setContentText(notificationContent)
	                .setContentTitle(notificationTitle)
	                .setSmallIcon(R.drawable.ic_launcher)
	                .setAutoCancel(true)
	                .setTicker(tickerMessage)
	                .setContentIntent(pendingIntent)
	                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
	 
	        /** Creating a notification from the notification builder */
	        Notification notification = notificationBuilder.build();
	 
	        /** Sending the notification to system.
	        * The first argument ensures that each notification is having a unique id
	        * If two notifications share same notification id, then the last notification replaces the first notification
	        * */
	        nManager.notify((int)System.currentTimeMillis(), notification);
	    }

	    @Override
	    public IBinder onBind(Intent intent) 
	    {
	        return null;
	    }

	    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	        if (currentBestLocation == null) {
	            // A new location is always better than no location
	            return true;
	        }

	        // Check whether the new location fix is newer or older
	        long timeDelta = location.getTime() - currentBestLocation.getTime();
	        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	        boolean isNewer = timeDelta > 0;

	        // If it's been more than two minutes since the current location, use the new location
	        // because the user has likely moved
	        if (isSignificantlyNewer) {
	            return true;
	        // If the new location is more than two minutes older, it must be worse
	        } else if (isSignificantlyOlder) {
	            return false;
	        }

	        // Check whether the new location fix is more or less accurate
	        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	        boolean isLessAccurate = accuracyDelta > 0;
	        boolean isMoreAccurate = accuracyDelta < 0;
	        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	        // Check if the old and new location are from the same provider
	        boolean isFromSameProvider = isSameProvider(location.getProvider(),
	                currentBestLocation.getProvider());

	        // Determine location quality using a combination of timeliness and accuracy
	        if (isMoreAccurate) {
	            return true;
	        } else if (isNewer && !isLessAccurate) {
	            return true;
	        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	            return true;
	        }
	        return false;
	    }



	/** Checks whether two providers are the same */
	    private boolean isSameProvider(String provider1, String provider2) {
	        if (provider1 == null) {
	          return provider2 == null;
	        }
	        return provider1.equals(provider2);
	    }



	@Override
	    public void onDestroy() {       
	       // handler.removeCallbacks(sendUpdatesToUI);     
	        super.onDestroy();
	        Log.v("STOP_SERVICE", "DONE");
	        locationManager.removeUpdates(listener);        
	    }   

	    public static Thread performOnBackgroundThread(final Runnable runnable) {
	        final Thread t = new Thread() {
	            @Override
	            public void run() {
	                try {
	                    runnable.run();
	                } finally {

	                }
	            }
	        };
	        t.start();
	        return t;
	    }




	public class MyLocationListener implements LocationListener
	    {

	        public void onLocationChanged(final Location loc)
	        {
	            Log.i("**************************************", "Location changed");
	            if(isBetterLocation(loc, previousBestLocation)) {
	                loc.getLatitude();
	                loc.getLongitude();             
	                intent.putExtra("Latitude", loc.getLatitude());
	                intent.putExtra("Longitude", loc.getLongitude());     
	                intent.putExtra("Provider", loc.getProvider());                 
	                sendBroadcast(intent);          

	            }                               
	        }

	        public void onProviderDisabled(String provider)
	        {
	            Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
	        }


	        public void onProviderEnabled(String provider)
	        {
	            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
	        }


	        public void onStatusChanged(String provider, int status, Bundle extras)
	        {

	        }

	    }
	}

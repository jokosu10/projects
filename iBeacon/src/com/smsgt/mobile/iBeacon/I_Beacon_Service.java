package com.smsgt.mobile.iBeacon;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class I_Beacon_Service extends Service implements BeaconConsumer {

	private static final String TAG = "I_Beacon_Service";
	private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
	private NotificationCompat.Builder notification;
	private PendingIntent pIntent;
	private NotificationManager manager;
	private Intent resultIntent;
	private TaskStackBuilder stackBuilder;
	private Handler handler, handler2;
	private String jsonResponse;
	
   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      // Let it continue running until it is stopped.
      Log.e(TAG, "Service Started");
      
      beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
	  beaconManager.bind(this); 
      
      handler = new Handler() {
    	 public void handleMessage(Message msg) {
                switch (msg.what) {
                case 1: Beacon beacon = (Beacon) msg.obj;
                		List<Identifier> bList = beacon.getIdentifiers();
                		
                		final String proxId = bList.get(0).toString(),
                				maj = bList.get(1).toString(),
                				min = bList.get(2).toString();
                			
                		handler2 = new Handler() {
                			public void handleMessage(Message m) {
                				switch(m.what) {
                					case 1: jsonResponse = (String) m.obj; 
                					   //Creating Notification Builder
                				       notification = new NotificationCompat.Builder(I_Beacon_Service.this);
                				       notification.setDefaults(Notification.DEFAULT_ALL);
                				       //Title for Notification
                				       notification.setContentTitle("iBeacon Alert");
                				       //Message in the Notification
                				       notification.setContentText("A new iBeacon content has been found!");
                				       //Alert shown when Notification is received
                				       notification.setTicker("New Alert!");
                				       //Icon to be set on Notification
                				       notification.setSmallIcon(R.drawable.ibeacon_icon);
                				       //Creating new Stack Builder
                				       stackBuilder = TaskStackBuilder.create(I_Beacon_Service.this);
                				       stackBuilder.addParentStack(I_BeaconMainActivity.class);
                				       //Intent which is opened when notification is clicked
                				       resultIntent = new Intent(I_Beacon_Service.this, I_BeaconMainActivity.class);
                				       resultIntent.putExtra("result", jsonResponse);
                				       resultIntent.putExtra("proxId", proxId);
                				       resultIntent.putExtra("major", maj);
                				       resultIntent.putExtra("minor",min);
                				       resultIntent.putExtra("notification_id",0);
                				       
                				       
                				       stackBuilder.addNextIntent(resultIntent);
                				       pIntent =  stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
                		               notification.setContentIntent(pIntent);
                		        	   manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                		               manager.notify(0, notification.build());
                					
                					break;
                				}
                			}
                		};
                		
                		new CallKontaktIOApi(proxId, maj, min, handler2).execute();
                		break;
                }
    	    }      
      };
      
      return START_STICKY;
   }
   
   @Override
   public void onDestroy() {
      super.onDestroy();
      beaconManager.unbind(this);
      Log.e(TAG,"Service Destroyed");
   }
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onBeaconServiceConnect() {
		
	      beaconManager.setRangeNotifier(new RangeNotifier() {
	            @Override 
	            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
	                if (beacons.size() > 0) {
	                    for(Beacon b: beacons) {
	                    	Message.obtain(handler, 1, b).sendToTarget();
	                    }
	                } 
	             }
	            });

	            try {
	                beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
			        beaconManager.setBackgroundBetweenScanPeriod(30000L); // next scan will be 30 seconds after 1st background scan
			        beaconManager.setBackgroundScanPeriod(1100L); // will scan in background for 1.1 seconds
			        beaconManager.setForegroundBetweenScanPeriod(15000L); // next scan will be 15 seconds after 1st foreground scan
			        beaconManager.setForegroundScanPeriod(1100L); // will scan in foreground for 1.1 seconds
			            
			        } catch (RemoteException e) {  
			        	Log.e(TAG, e.getLocalizedMessage());
			        }
        

		 beaconManager.setMonitorNotifier(new MonitorNotifier() {
		    @Override
		    public void didEnterRegion(Region region) {
		      Log.e(TAG, "I just saw a beacon for the first time!"); 
			 }
			
			@Override
			public void didExitRegion(Region region) {
			  Log.e(TAG, "I no longer see a beacon");
			}
			
			@Override
			public void didDetermineStateForRegion(int state, Region region) {
			    Log.e(TAG, "I have just switched from seeing/not seeing beacons: "+ state);      
			}
			});
		
		try {
		    beaconManager.startMonitoringBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
		 } catch (RemoteException e) { 
			Log.e("", e.getLocalizedMessage());
		 }
    }	
	
	class CallKontaktIOApi extends AsyncTask<Void, Void, String> {

		// https://api.kontakt.io/beacon?proximity=f7826da6-4fa2-4e98-8024-bc5b71e0893e&major=100&minor=1
		private String kontakIOUrl = "https://api.kontakt.io/beacon?proximity=";
		
		private String proximityId;
		private String major;
		private String minor;
		private Handler kontakIOHandler;
		
		public CallKontaktIOApi(String proxId, String maj, String min, Handler hand) {
			this.proximityId = proxId;
			this.major = maj;
			this.minor = min;
			this.kontakIOHandler = hand;
		}
		
		@Override
		protected String doInBackground(Void... params) {

			kontakIOUrl = kontakIOUrl + this.proximityId + "&major=" + this.major + "&minor=" + this.minor + "";
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			String responseText = "";
			
			HttpGet httpGet = new HttpGet(kontakIOUrl);
			httpGet.addHeader("Api-Key","XmgnRYkaCSFybCovFzlazfepHFsPlkOQ");
			httpGet.addHeader("Accept", "application/vnd.com.kontakt+json; version=2");
			
			try {
				HttpResponse response = httpClient.execute(httpGet, localContext);
				HttpEntity entity = response.getEntity();
				responseText = getASCIIContentFromEntity(entity);
				
				//Log.e(TAG, "" + responseText);
				Message.obtain(kontakIOHandler, 1, responseText).sendToTarget();
				
			} catch (Exception e) {
				Log.e("", "", e);
			}
			
			return "";
		}
		
		public String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {

			InputStream in = entity.getContent();
			StringBuffer out = new StringBuffer();
			int n = 1;
			while (n>0) {
			byte[] b = new byte[4096];
			n =  in.read(b);
			if (n>0) out.append(new String(b, 0, n));
			}
			return out.toString();
		}
		
	}	
	
	
}

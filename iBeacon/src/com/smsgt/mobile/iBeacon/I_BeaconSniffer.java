package com.smsgt.mobile.iBeacon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class I_BeaconSniffer extends Activity {
	
	protected static final String TAG = "I_BeaconSniffer";
/*	private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
	private NotificationCompat.Builder notification;
	private PendingIntent pIntent;
	private NotificationManager manager;
	private Intent resultIntent;
	private TaskStackBuilder stackBuilder;
	private Handler handler, handler2;
	private String jsonResponse;*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		startService(new Intent(getBaseContext(), I_Beacon_Service.class));
		finish();
		//beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
	    //beaconManager.bind(this); 
	    
/*	    handler = new Handler() {
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
	                				       notification = new NotificationCompat.Builder(I_BeaconSniffer.this);
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
	                				       stackBuilder = TaskStackBuilder.create(I_BeaconSniffer.this);
	                				       stackBuilder.addParentStack(I_BeaconMainActivity.class);
	                				       //Intent which is opened when notification is clicked
	                				       resultIntent = new Intent(I_BeaconSniffer.this, I_BeaconMainActivity.class);
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
	    };*/
	    
	}	
	
	@Override 
    protected void onDestroy() {
        super.onDestroy();
       // beaconManager.unbind(this);
    }

/*	@Override
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
		
	}*/	
}

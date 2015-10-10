package com.f8mobile.community_app.mobile.asynctask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.f8mobile.community_app.mobile.database.SqliteDatabaseHelper;
import com.f8mobile.f8mobile.PreferenceConnector;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

public class GcmAsyncTask extends AsyncTask<Void, Void, String>{
	
	private static final String GCM_SENDER_ID = "17286696166"; 
	private GoogleCloudMessaging gcm;
	private String clientId;
	private Context contxt;
	
	public GcmAsyncTask(Context con, GoogleCloudMessaging g, String cId) {
		this.gcm = g;
		this.clientId = cId;
		this.contxt = con;
	}

	@Override
	protected String doInBackground(Void... params) {
		
		new Runnable() {

			@Override
			public void run() {
				String r="";
		        int BACKOFF_MILLI_SECONDS = 100;
		        int MAX_ATTEMPTS = 10;
		        
				long backoff = BACKOFF_MILLI_SECONDS + (new Random()).nextInt(1000);
		        
				for (int i = 1; i <= MAX_ATTEMPTS; i++) {
		            Log.e("GcmAsyncTask", "Attempt #" + i + " to register");
		            try{    
		                
		                //Should get registrationid from gcm server
		                //but this portion is never running and control tranfers to
		                //catch portion with error "service not available "
		                r = gcm.register(GCM_SENDER_ID);
		                if(!TextUtils.isEmpty(r)) {
		                	
		                	 Log.e("GcmAsyncTask","GCM_REG_ID: " + r);
		                	 PreferenceConnector.writeString(contxt, PreferenceConnector.GCM_REG_ID, r);
		                	 String[] param = new String[2];
		                     param[0] = "method=11";
		                     param[1] = "POST";
		                     
		                     List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		        			 nameValuePairs.add(new BasicNameValuePair("client_id", clientId));
		        			 nameValuePairs.add(new BasicNameValuePair("gcm_registration_id", r));
		        			 
		                     new CommunityAppAsyncTaskHttpCall(nameValuePairs,null,0).execute(param);
		                     
		                     SqliteDatabaseHelper sqliteDatabaseHelper = new SqliteDatabaseHelper(contxt);
		                     sqliteDatabaseHelper.updateGcmIdOfThisUser(clientId, r);
		                     break;
		                }
		                
		            } catch(IOException e){
		                //here getting the error message service not available
		                Log.e("GcmAsyncTask", "Failed to register on attempt " + i + ":" + e);
		                if (i == MAX_ATTEMPTS) {
		                    break;
		                }
		                try {
		                    Log.e("GcmAsyncTask", "Sleeping for " + backoff + " ms before retry");
		                    Thread.sleep(backoff);
		                } catch (InterruptedException e1) {
		                    // Activity finished before we complete - exit.
		                    Log.e("GcmAsyncTask", "Thread interrupted: abort remaining retries!");
		                    Thread.currentThread().interrupt();
		                }
		                // increase backoff exponentially
		                backoff *= 2;
		            }	
				} 
			}
			
		}.run();  		        
	 return "";
  }
}

package com.smsgt.mobile.application.supertsuper_v3.asynctask_classes;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;

//import com.smsgt.mobile.application.supertsuper_v3.custom_interfaces.impl.TrafficProfileMockDataSource;
import com.smsgt.mobile.application.supertsuper_v3.database.model.TsuperTrafficProfileModel;
import com.smsgt.mobile.application.supertsuper_v3.util_helpers.ParseHttpContent;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GetAllTrafficProfilesFromStrongloop extends AsyncTask<Void, Void, ArrayList<TsuperTrafficProfileModel>> {
		 
		private static Handler mHandler;
		private String URL = "http://107.155.108.121:3000/api/filteredHighway";
		private JSONArray jArray;
		private String caller;
		
		public GetAllTrafficProfilesFromStrongloop(Handler h, String caller) {
			mHandler = h;
			this.caller = caller;
		}

	 	@Override
		protected void onPostExecute(ArrayList<TsuperTrafficProfileModel> listTrafficProfileModel) {
	 		
	 		int tag = 2;
	 		if(caller.equalsIgnoreCase("DriveActivity")) {
	 			tag = 4;
	 		}
	 		Message.obtain(mHandler, tag, listTrafficProfileModel).sendToTarget();	
		}
		
		@Override
		protected ArrayList<TsuperTrafficProfileModel> doInBackground(Void... params) {
			
			// change to strongloop connection

			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			String responseText = "";
			ArrayList<TsuperTrafficProfileModel> trafficProfileList = new ArrayList<TsuperTrafficProfileModel>();
			
			Log.e("", "CALLING ASYNCTASKHTTPCALL CLASS: url=>" + URL);
			HttpGet httpGet = new HttpGet(URL);
			
			try {
				HttpResponse response = httpClient.execute(httpGet, localContext);
				HttpEntity entity = response.getEntity();
				responseText = ParseHttpContent.getASCIIContentFromEntity(entity);
				
				jArray = new JSONArray(responseText);
				if(jArray != null) {
					Log.e("", "filter_highway count total: " + jArray.length());
					for(int i = 0; i < jArray.length(); i++) {
						JSONObject j = jArray.getJSONObject(i);
						//Log.e("", j.getString("name") + j.getJSONArray("geopoint"));
						TsuperTrafficProfileModel t = new TsuperTrafficProfileModel();
						t.setTrafficProfilePolygonTag(j.getString("name"));
						t.setLat(j.getJSONObject("geopoint").getString("latitude"));
						t.setLon(j.getJSONObject("geopoint").getString("longitude"));
						t.setTrafficProfileHistorical(j.getString("historical_data")); // to be fetched on historical
						trafficProfileList.add(t);
					}
				}
				
				Log.e("", "list size: " + trafficProfileList.size());
				
			} catch (Exception e) {
				Log.e("", "", e);
			}
			
			return trafficProfileList;
			/*ArrayList<TsuperTrafficProfileModel> dummyTrafficProfiles = new TrafficProfileMockDataSource().getTrafficProfiles();
			return dummyTrafficProfiles; */
		}
 }

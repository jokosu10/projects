package com.smsgt.mobile.application.supertsuper_v3.asynctask_classes;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.smsgt.mobile.application.supertsuper_v3.database.model.TsuperUserProfileModel;
import com.smsgt.mobile.application.supertsuper_v3.strongloop.StrongloopClient;
import com.smsgt.mobile.application.supertsuper_v3.strongloop.model.StrongloopRegistrationModel;
import com.smsgt.mobile.application.supertsuper_v3.strongloop.repository.StrongloopRegistrationRepository;
import com.smsgt.mobile.application.supertsuper_v3.util_helpers.ParseHttpContent;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.VoidCallback;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class SendTsuperUserProfileToStrongloop extends AsyncTask<String, Void, String> {

	private TsuperUserProfileModel tsuperUserProfile;
	private Context context;
	private String URL = "http://107.155.108.121:3000/api";
	
	public SendTsuperUserProfileToStrongloop(TsuperUserProfileModel t, Context c) {
		this.tsuperUserProfile = t;
		this.context = c;
	}
	
	@Override
	protected String doInBackground(final String... params) {
		
		StrongloopClient client =  new StrongloopClient(this.context, URL);
		RestAdapter adapter;
		StrongloopRegistrationRepository registrationRepository;
		
		// POST -- insert
		if(params[1].equalsIgnoreCase("POST")) {
			
			adapter = client.getLoopBackAdapter(params[0], params[1]);
			registrationRepository = adapter.createRepository(StrongloopRegistrationRepository.class);
			
			final StrongloopRegistrationModel registrationModel = registrationRepository.createObject(ImmutableMap.of("name","UserRegistrations"));
			
			registrationModel.setUserId(tsuperUserProfile.getUserId());
			registrationModel.setUserName(tsuperUserProfile.getUserName());
			registrationModel.setPassword(tsuperUserProfile.getPassword());
			registrationModel.setFirstName(tsuperUserProfile.getFirstName());
			registrationModel.setLastName(tsuperUserProfile.getLastName());
			registrationModel.setContactNumber(tsuperUserProfile.getContactNumber());
			registrationModel.setCity(tsuperUserProfile.getCity());
			registrationModel.setTotalPoints(tsuperUserProfile.getTotalPoints());
			registrationModel.setAverageMovingSpeed(tsuperUserProfile.getAverageMovingSpeed());
			registrationModel.setTravelBearing(tsuperUserProfile.getTravelBearing());
			registrationModel.setTravelLatitude(tsuperUserProfile.getTravelLatitude());
			registrationModel.setTravelLongitude(tsuperUserProfile.getTravelLongitude());
			registrationModel.setPrevDistance(tsuperUserProfile.getPrevDistance());
			registrationModel.setCurrDistance(tsuperUserProfile.getCurrDistance());
			registrationModel.setTotalDistance(tsuperUserProfile.getTotalDistance());
			registrationModel.setMembershipDate(tsuperUserProfile.getMembershipDate());
			
			registrationModel.save(new VoidCallback() {
				
				@Override
				public void onSuccess() {
					Log.e("", "Succesfully saved Registration model.");
				}

				@Override
				public void onError(Throwable t) {
					Log.e("", "Cannot save Registration model.", t);
				}
			});
			
		} // PUT -- update
		else if (params[1].equalsIgnoreCase("PUT")) {
			updateTsuperProfile(tsuperUserProfile, params[0]);
			
		} 
		return "";
	}	
	
	protected void updateTsuperProfile(TsuperUserProfileModel tsuperUserProfileModel, String url) {
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
				
		String jsonUpdate = "";
		HttpPut httpPut = new HttpPut(URL + "/" + url);
		httpPut.addHeader("Accept", "application/json");
		httpPut.addHeader("Content-Type", "application/json");

		Gson gson = new Gson();
		jsonUpdate = gson.toJson(tsuperUserProfileModel);
		String responseText = "";
		try {

			Log.e("", "tsuperJson: " + jsonUpdate);
			StringEntity entity = new StringEntity(jsonUpdate, "UTF-8");
			entity.setContentType("application/json");
	        httpPut.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPut, localContext);
			HttpEntity resEntity = response.getEntity();
			responseText = ParseHttpContent.getASCIIContentFromEntity(resEntity);
		} catch (Exception e) {
			Log.e("", "", e);
		}
		
		Log.e("", "response: " + responseText);
	}
}

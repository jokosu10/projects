package com.smsgt.mobile.application.supertsuper_v3.asynctask_classes;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.common.collect.ImmutableMap;
import com.smsgt.mobile.application.supertsuper_v3.database.model.TsuperUserProfileModel;
import com.smsgt.mobile.application.supertsuper_v3.strongloop.StrongloopClient;
import com.smsgt.mobile.application.supertsuper_v3.strongloop.model.StrongloopTravelInfoModel;
import com.smsgt.mobile.application.supertsuper_v3.strongloop.repository.StrongloopTravelInfoRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.VoidCallback;

public class SendTravelDataOnStrongloop extends AsyncTask<String, Void, String>{
	
	private TsuperUserProfileModel tsuperUserProfile;
	private Context context;
	private static String URL = "http://107.155.108.121:3000/api";
	
	public SendTravelDataOnStrongloop(TsuperUserProfileModel t, Context c) {
		this.tsuperUserProfile = t;
		this.context = c;
	}
	 
		@Override
		protected String doInBackground(String... params) {
			
			StrongloopClient client =  new StrongloopClient(this.context, URL);
			
			RestAdapter adapter = client.getLoopBackAdapter(params[0], params[1]);
			StrongloopTravelInfoRepository repository = adapter.createRepository(StrongloopTravelInfoRepository.class);
			StrongloopTravelInfoModel model = repository.createObject(ImmutableMap.of("name","TravelInfos"));
			
			model.setUserId(tsuperUserProfile.getUserName());
			model.setBearing(tsuperUserProfile.getTravelBearing());
			model.setLatitude(params[2]); // latitude
			model.setLongitude(params[3]); // longitude
			model.setSpeed(tsuperUserProfile.getAverageMovingSpeed());
			model.setTotalDistance(tsuperUserProfile.getTotalDistance());
			model.setType(params[4]);
			model.setName(params[5]);
			model.setDescription(params[6]);
			model.setTimestamp(params[7]);
			
			model.save(new VoidCallback() {
				
				@Override
				public void onSuccess() {
					Log.e("", "Succesfully saved TravelInfo model.");
				}

				@Override
				public void onError(Throwable t) {
					Log.e("", "Cannot save TravelInfo model.", t);
				}
			});
			
			return "";
		}				
}

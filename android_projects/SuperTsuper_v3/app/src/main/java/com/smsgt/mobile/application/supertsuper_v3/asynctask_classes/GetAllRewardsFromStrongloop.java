package com.smsgt.mobile.application.supertsuper_v3.asynctask_classes;

import java.util.List;

import com.smsgt.mobile.application.supertsuper_v3.strongloop.StrongloopClient;
import com.smsgt.mobile.application.supertsuper_v3.strongloop.model.StrongloopRewardsModel;
import com.smsgt.mobile.application.supertsuper_v3.strongloop.repository.StrongloopRewardsRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ListCallback;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GetAllRewardsFromStrongloop extends AsyncTask<String, Void, String>{

	private Context context;
	private static Handler rewardsHandler;
	private static String URL = "http://107.155.108.121:3000/api";
	
	public GetAllRewardsFromStrongloop(Context c, Handler h) {
		this.context = c;
		rewardsHandler =  h;
	}
	
	@Override
	protected String doInBackground(String... params) {
		
		StrongloopClient strongloopClient = new StrongloopClient(this.context, URL);
		// params[0] = rewards, params[1] = GET
		RestAdapter adapter = strongloopClient.getLoopBackAdapter(params[0], params[1]);
		StrongloopRewardsRepository strongloopRewardsRepository = adapter.createRepository(StrongloopRewardsRepository.class);
		strongloopRewardsRepository.findAll(new ListCallback<StrongloopRewardsModel>() {
			
			@Override
			public void onSuccess(List<StrongloopRewardsModel> arg0) {
				Log.e("", "GetAllRewards success size:  " + arg0.size());
				Message.obtain(rewardsHandler, 1, arg0).sendToTarget();
				
			}
			
			@Override
			public void onError(Throwable arg0) {
				Log.e("", "GetAllRewards error: " + arg0);
			}
		});
				
		return "";
	}

}

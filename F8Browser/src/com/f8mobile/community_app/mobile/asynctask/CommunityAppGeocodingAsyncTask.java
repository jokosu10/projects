package com.f8mobile.community_app.mobile.asynctask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import com.google.android.gms.maps.model.LatLng;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

public class CommunityAppGeocodingAsyncTask extends AsyncTask<String,Void,LatLng> {
	
	private ProgressDialog progressDialog;
	private Context context;
	
	public CommunityAppGeocodingAsyncTask(Context c, ProgressDialog p) {
		this.progressDialog = p;
		this.context = c;
	}

	@Override
	protected void onPreExecute() {
		
		if(progressDialog != null) {
			progressDialog.setTitle("");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	 		progressDialog.setCanceledOnTouchOutside(false);
	 		progressDialog.setMessage("Searching.....");
	 		progressDialog.show();
		}
	}
	
	@Override
	protected void onPostExecute(LatLng result) {
		
		if(progressDialog != null) {
			if(progressDialog.isShowing()) {
				
				progressDialog.dismiss();
				// show progress dialog for another 3 seconds
				/*Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
				    public void run() {
				    	progressDialog.dismiss();
				    }
				 }, 3000);  */
	 			
	 		}
		}
	}
	
	@Override
	protected LatLng doInBackground(String... params) {
		
		LatLng l = null;
		Geocoder googleGeoCoder = new Geocoder(context, Locale.getDefault());
		try {
			List<Address> loc = googleGeoCoder.getFromLocationName(params[0], 1);
			if(loc.size() > 0) {
				l = new LatLng(loc.get(0).getLatitude(), loc.get(0).getLongitude());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return l;
	}

}

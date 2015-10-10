package com.f8mobile.communitymapadmin.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CommunityAppGeocodingAsyncTask extends AsyncTask<String, Void, LatLng> {

    private Context context;
    private ProgressDialog progressDialog;

    public CommunityAppGeocodingAsyncTask(Context context1, ProgressDialog progressdialog)
    {
        progressDialog = progressdialog;
        context = context1;
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

package com.iloilo.iloilotaxidriverapp.asynctask;

import android.os.AsyncTask;
import android.content.Context;
import android.app.ProgressDialog;

import java.io.IOException;

import android.location.Geocoder;

import java.util.Locale;
import java.util.List;

import android.location.Address;

public class IloiloGeocodingAsyncTask extends AsyncTask<Double, Void, String> {
	
    private Context context;
    private ProgressDialog progressDialog;
    
    public IloiloGeocodingAsyncTask(Context c, ProgressDialog p) {
        progressDialog = p;
        context = c;
    }
    
    @Override
    protected void onPreExecute() {
        if(progressDialog != null) {
            progressDialog.setTitle("");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Getting address..");
            progressDialog.show();
        }
    }
    
    @Override
 	protected void onPostExecute(String result) {
 		
 		if(progressDialog != null) {
 			if(progressDialog.isShowing()) {
 				progressDialog.dismiss();   
 	 		}
 		}
 	}
    
    protected String doInBackground(Double... params) {
    	
    	StringBuilder addressBuilder = new StringBuilder();
    	String finalAddress = "";
		Geocoder googleGeoCoder = new Geocoder(context, Locale.getDefault());
		try {
			List<Address> loc = googleGeoCoder.getFromLocation(params[0], params[1], 1);
			if(loc.size() > 0) {
				for(int i = 0; i < loc.get(0).getMaxAddressLineIndex(); i++) {
					addressBuilder.append(loc.get(0).getAddressLine(i)).append(" , ");
				}
				finalAddress = addressBuilder.toString().substring(0, addressBuilder.toString().length() - 2);
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return finalAddress;
    }
}

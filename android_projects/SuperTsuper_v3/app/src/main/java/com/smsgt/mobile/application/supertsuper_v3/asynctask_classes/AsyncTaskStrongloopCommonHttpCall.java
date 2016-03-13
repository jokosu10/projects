package com.smsgt.mobile.application.supertsuper_v3.asynctask_classes;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.smsgt.mobile.application.supertsuper_v3.util_helpers.ParseHttpContent;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AsyncTaskStrongloopCommonHttpCall extends AsyncTask<String,Void,String> {

	private ProgressDialog progressDialog;
	private String URL = "http://107.155.108.121:3000/api/";
	private static Handler asyncHandler;
	
	public AsyncTaskStrongloopCommonHttpCall(ProgressDialog p) { 
		this.progressDialog = p;
	}
	
	public AsyncTaskStrongloopCommonHttpCall() {}
	
	public AsyncTaskStrongloopCommonHttpCall(Handler h) {
		asyncHandler = h;
	}
	
	@Override
	protected void onPreExecute() {
		
		if(progressDialog != null) {
			progressDialog.setTitle("");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	 		progressDialog.setCanceledOnTouchOutside(false);
	 		progressDialog.setMessage("Please wait...");
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
	
	@Override
	protected String doInBackground(String... params) {
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		String responseText = "";
		
		URL = URL + params[0];
		
		Log.e("", "CALLING ASYNCTASKHTTPCALL CLASS: url=>" + URL);
		
		if(params[1].equalsIgnoreCase("GET")) {
			HttpGet httpGet = new HttpGet(URL);
			
			try {
				HttpResponse response = httpClient.execute(httpGet, localContext);
				HttpEntity entity = response.getEntity();
				responseText = ParseHttpContent.getASCIIContentFromEntity(entity);
			} catch (Exception e) {
				Log.e("", "", e);
			}

			Log.e("", "result: " + responseText);
			
			if(asyncHandler != null) {
				Message.obtain(asyncHandler, 1, responseText).sendToTarget();
			}
		} 
		
		return responseText;
	}
}

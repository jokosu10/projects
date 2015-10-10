package com.f8mobile.communitymapadmin.asynctask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;


public class CommunityAppAsyncTaskHttpCall extends AsyncTask<String,Void,String>{

	private ProgressDialog progressDialog;
	private String URL = "http://f8mobile.net/web_api_dev/community/community_map_api.php?"; // should point to community app server
	private List<NameValuePair> nameValuePairs;
	private Handler mHandler;
	private int handlerCode;
	
	public CommunityAppAsyncTaskHttpCall(List<NameValuePair> nVP) {
		this.nameValuePairs = nVP;
	}
	
	public CommunityAppAsyncTaskHttpCall(Handler h, int code) {
		this.mHandler = h;
		this.handlerCode = code;
	}
	
	public CommunityAppAsyncTaskHttpCall(ProgressDialog p, Handler h, int code) {
		this.progressDialog = p;
		this.mHandler = h;
		this.handlerCode = code;
	}
	
	public CommunityAppAsyncTaskHttpCall(ProgressDialog pDialog, List<NameValuePair> nValuePair, Handler h, int code) {
		this.progressDialog = pDialog;
		this.nameValuePairs = nValuePair;
		this.mHandler = h;
		this.handlerCode = code;
	}

	public CommunityAppAsyncTaskHttpCall(List<NameValuePair> nValuePair, Handler h, int code) {
		this.nameValuePairs = nValuePair;
		this.mHandler = h;
		this.handlerCode = code;
	}	
	
	@Override
	protected void onPreExecute() {
		
		if(progressDialog != null) {
			progressDialog.setTitle("");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	 		progressDialog.setCanceledOnTouchOutside(false);
	 		
	 		switch(handlerCode) {
		 		case 1: progressDialog.setMessage("Please wait approximately 2 minutes while loading chat messages..."); break;
		 		case 2: progressDialog.setMessage("Please wait approximately 2 minutes while loading..."); break;
		 		case 3: progressDialog.setMessage("Please wait approximately 2 minutes while searching..."); break;
		 		case 4: progressDialog.setMessage("Please wait approximately 2 minutes while searching..."); break;
		 		case 5: progressDialog.setMessage("Saving data..."); break;
		 		default: progressDialog.setMessage("Please wait while loading...."); break;
	 		}
	 		
	 		progressDialog.show();
		}
	}
	
	@Override
	protected void onPostExecute(String result) {
		
		if(progressDialog != null) {
			
			if(progressDialog.isShowing()) {
				
				progressDialog.dismiss();
				if(handlerCode != 0) { // for message sending
					Message.obtain(mHandler, handlerCode, result).sendToTarget();
				}
	 		}
		}
	}
	
	@Override
	protected String doInBackground(String... params) {
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		String responseText = "";
		
		if(params[1].equalsIgnoreCase("GET")) {
			URL = URL + params[0];
			HttpGet httpGet = new HttpGet(URL);
			
			try {
				// Execute HTTP GET Request
				HttpResponse response = httpClient.execute(httpGet, localContext);
				HttpEntity entity = response.getEntity();
				responseText = CommunityAppAsyncTaskHttpCall.getASCIIContentFromEntity(entity);
			} catch (Exception e) {
				Log.e("", "", e);
			}
			Log.e("", "result: " + responseText);
			
		} else if (params[1].equalsIgnoreCase("POST")) {
			
			URL = URL + params[0];
			HttpPost httpPost = new HttpPost(URL);
			
		    try {
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				 // Execute HTTP POST Request
			    HttpResponse response = httpClient.execute(httpPost, localContext);
			    HttpEntity entity = response.getEntity();
				responseText = CommunityAppAsyncTaskHttpCall.getASCIIContentFromEntity(entity);
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    Log.e("", "result: " + responseText);
		}
		
		return responseText;
	}

	public static String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {

		InputStream in = entity.getContent();
		StringBuffer out = new StringBuffer();
		int n = 1;
		while (n > 0) {
			byte[] b = new byte[4096];
			n =  in.read(b);
			if (n>0) out.append(new String(b, 0, n));
		}
		return out.toString();
	}	
}

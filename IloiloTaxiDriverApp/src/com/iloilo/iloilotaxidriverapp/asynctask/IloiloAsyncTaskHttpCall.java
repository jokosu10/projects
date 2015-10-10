package com.iloilo.iloilotaxidriverapp.asynctask;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import java.util.List;

import org.apache.http.NameValuePair;

import android.app.ProgressDialog;

import org.apache.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;

import android.util.Log;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.entity.UrlEncodedFormEntity;

public class IloiloAsyncTaskHttpCall extends AsyncTask<String,Void,String> {
	
    private List<NameValuePair> nameValuePairs;
    private ProgressDialog progressDialog;
    private String URL = "http://tidalsolutions.co/iloilo_apps_api.php?";
    private Handler handler;
    
    public IloiloAsyncTaskHttpCall(ProgressDialog p) {
        this.progressDialog = p;
    }
    
    public IloiloAsyncTaskHttpCall(List<NameValuePair> l) {
        this.nameValuePairs = l;
    }
    
    public IloiloAsyncTaskHttpCall(ProgressDialog p, List<NameValuePair> l) {
        this.progressDialog = p;
        this.nameValuePairs = l;
    }
    
    public IloiloAsyncTaskHttpCall(ProgressDialog p, List<NameValuePair> l, Handler h) {
        this.progressDialog = p;
        this.nameValuePairs = l;
        this.handler = h;
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
        DefaultHttpClient httpClient = new DefaultHttpClient();
        BasicHttpContext localContext = new BasicHttpContext();
        
        String responseText = "";
        HttpResponse response;
        HttpEntity entity;
        
        if(params[1].equalsIgnoreCase("GET")) {
            URL += params[0];
            HttpGet httpGet = new HttpGet(URL);
            try {
                response = httpClient.execute(httpGet, localContext);
                entity = response.getEntity();
                responseText = getASCIIContentFromEntity(entity);
            } catch(Exception e) {
                Log.e("", "", e);
            }
            
        }  else if(params[1].equalsIgnoreCase("POST")) {
            URL += params[0];
            HttpPost httpPost = new HttpPost(URL);
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                response = httpClient.execute(httpPost, localContext);
                entity = response.getEntity();
                responseText = getASCIIContentFromEntity(entity);
            } catch(UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch(ClientProtocolException e) {
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            }
            Log.e("", responseText);
        }
        
        if(handler != null) {
        	Message.obtain(handler, 1, responseText).sendToTarget();
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

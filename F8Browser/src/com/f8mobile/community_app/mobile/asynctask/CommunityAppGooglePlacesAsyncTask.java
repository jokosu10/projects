package com.f8mobile.community_app.mobile.asynctask;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class CommunityAppGooglePlacesAsyncTask extends AsyncTask<String, Void, ArrayList<String>> {

	private ArrayList<String> resultList;
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	private static final String API_KEY = "AIzaSyDGWvRMNRn78giC7d4_BDt8gmhhityq4Bg";

	@Override
	protected ArrayList<String> doInBackground(String... params) {
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		String responseText = "";
		
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON + "?sensor=false&key=" + API_KEY + "&input=" + URLEncoder.encode(params[0], "utf8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} 
		
		HttpResponse response;
		
		try {
			response = httpClient.execute(httpGet, localContext);
			HttpEntity entity = response.getEntity();
			responseText = CommunityAppAsyncTaskHttpCall.getASCIIContentFromEntity(entity);
			
			JSONObject jObject = new JSONObject(responseText);
			JSONArray jArray = jObject.getJSONArray("predictions");
			
			resultList = new ArrayList<>(jArray.length());
			for (int i = 0; i < jArray.length(); i++) {
				resultList.add(jArray.getJSONObject(i).getString("description"));
			}
			
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
		
		return resultList;
	}

}

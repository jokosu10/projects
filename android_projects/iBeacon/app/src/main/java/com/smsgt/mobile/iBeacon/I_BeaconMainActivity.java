package com.smsgt.mobile.iBeacon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;

import android.os.Bundle;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class I_BeaconMainActivity extends Activity {
	
	protected static final String TAG = "I_BeaconMainActivity";
    private TextView beaconTextViewUUID, beaconTextViewMajor, beaconTextViewMinor;
    private String jsonResponse = "";
    private ImageView imageView;
    private WebView webView;
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ibeacon_main);	
		
		beaconTextViewUUID = (TextView) findViewById(R.id.textViewUUIDValue);
		beaconTextViewMajor = (TextView) findViewById(R.id.textViewUUIDMajorValue);
		beaconTextViewMinor = (TextView) findViewById(R.id.textViewUUIDMinorValue);
		
		imageView = (ImageView) findViewById(R.id.imageView1);
		webView = (WebView) findViewById(R.id.webView1);
		
		jsonResponse = getIntent().getExtras().getString("result");
		String uuid = getIntent().getExtras().getString("proxId");
		String major = getIntent().getExtras().getString("major");
		String minor = getIntent().getExtras().getString("minor");
	    
		int notification_id = getIntent().getIntExtra("notification_id", 0);
		if(notification_id != -1) {
			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(notification_id);
		}
		
		beaconTextViewUUID.setText(uuid);
		beaconTextViewMajor.setText(major);
		beaconTextViewMinor.setText(minor);
			
		try {
			JSONObject jObj = new JSONObject(jsonResponse);
			if(jObj != null) {
				JSONArray jArray = jObj.getJSONArray("browserActions");
				if(jArray.length() > 0) { // beacon has browser content
					
					String actionType = jArray.getJSONObject(0).getString("actionType");
					if(actionType.equalsIgnoreCase("BROWSER")) {
						imageView.setVisibility(View.INVISIBLE);
						webView.setVisibility(View.VISIBLE);
						String url = jArray.getJSONObject(0).getString("url");
						webView.loadUrl(url);
					}
					
				} else {
					jArray = jObj.getJSONArray("contentActions");
					if(jArray.length() > 0) { // beacon has image/file content
						
						String actionType = jArray.getJSONObject(0).getString("actionType");
						if(actionType.equalsIgnoreCase("CONTENT")) {
							String contentCategory = jArray.getJSONObject(0).getString("contentCategory");
							String content = jArray.getJSONObject(0).getString("content");
							
							Log.e(TAG, "contentType: " + contentCategory + "\ncontent: " + content);
							
							if(contentCategory.equalsIgnoreCase("IMAGE")) {
								imageView.setVisibility(View.VISIBLE);
								webView.setVisibility(View.INVISIBLE);
								AQuery androidAQuery=new AQuery(getApplicationContext());
								androidAQuery.id(imageView).image(content, true, true, 300, R.drawable.ic_launcher,null, AQuery.FADE_IN);        
							} else {
								Toast.makeText(getApplicationContext(), "FILE CONTENT detected", Toast.LENGTH_SHORT).show();
							}
						} 
					}
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}	
	}
	
	@Override 
    protected void onDestroy() {
        super.onDestroy();
    }
	
}


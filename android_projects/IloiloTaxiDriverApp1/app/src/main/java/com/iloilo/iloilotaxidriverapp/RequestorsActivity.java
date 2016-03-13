package com.iloilo.iloilotaxidriverapp;

import android.app.Activity;

import java.util.ArrayList;

import org.json.JSONObject;

import android.widget.ListView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.view.Window;
import android.view.WindowManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import com.iloilo.iloilotaxidriverapp.asynctask.IloiloAsyncTaskHttpCall;
import com.iloilo.iloilotaxidriverapp.asynctask.IloiloGeocodingAsyncTask;
import com.iloilo.iloilotaxidriverapp.custom.HireRequestAdapter;
import com.iloilo.iloilotaxidriverapp.model.HireRequestModel;

import android.util.Log;

public class RequestorsActivity extends Activity {
    
	private String driverId, contactNumber;
    private HireRequestAdapter hireRequestAdapter;
    private ArrayList<HireRequestModel> hireRequestList;
    private JSONObject j;
    private ListView listview;
    private ProgressDialog progressDialog;
    private static Handler mHandler;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
       requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.taxi_hirers_layout);
        progressDialog = new ProgressDialog(this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
       // gcmRegistrationId = sharedPreferences.getString("IloiloTaxi_GCM_RegistrationId", "");
        driverId = sharedPreferences.getString("IloiloTaxi_UserId", "");
        contactNumber = sharedPreferences.getString("IloiloTaxi_ContactNumber", "");
        Log.e("","driverId: " + driverId);
        listview = (ListView)findViewById(R.id.listViewRequestors);
        
        mHandler = new Handler() {
        	public void handleMessage(Message msg) {
        		switch(msg.what) {
        		case 1 : 
        			
     				try {
     					String jsonString = (String) msg.obj;
     	                if(!jsonString.equalsIgnoreCase("null")) {
     	                    JSONArray jArray = new JSONArray(jsonString);
     	                    
     	                    hireRequestList = new ArrayList<HireRequestModel>();
     	                    
     	                    for(int i = 0; i < jArray.length(); i++) {
     	                    	
                                 j = jArray.getJSONObject(i);
                                 Log.e("jsonobject: ", j.toString());
                                 Double[] p = new Double[2];
                                 p[0] = Double.valueOf(j.getString("latitude"));
                                 p[1] = Double.valueOf(j.getString("longitude"));
                                 String addressString = new IloiloGeocodingAsyncTask(getApplicationContext(), progressDialog).execute(p).get();
                                 HireRequestModel h = new HireRequestModel();
                                 h.setRequestId(j.getString("id"));
                                 h.setAddress(addressString);
                                 h.setGcmRegId(j.getString("gcm_registration_id"));
                                 hireRequestList.add(h);
     	                       
     	                    }
     	                    
     	                    hireRequestAdapter = new HireRequestAdapter(hireRequestList, RequestorsActivity.this);
     	                    listview.setAdapter(hireRequestAdapter);
         
     	                }
     				} catch (InterruptedException e1) {
     					e1.printStackTrace();
     				} catch (ExecutionException e1) {
     					e1.printStackTrace();
     				} catch (JSONException e) {
     					e.printStackTrace();
     				}; break;
        		}
        	}
        };
        
        String gcmValue = getIntent().getStringExtra("message");
        gcmValue = "New Hire Request Notification";
        if((gcmValue != null) && (!gcmValue.trim().isEmpty())) {
            if(gcmValue.equalsIgnoreCase("New Hire Request Notification")) {
                String[] params = new String[2];
                params[0] = "method=7";
                params[1] = "POST";
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("driver_id", driverId));
                nameValuePairs.add(new BasicNameValuePair("contact_number", contactNumber));
                new IloiloAsyncTaskHttpCall(progressDialog, nameValuePairs, mHandler).execute(params);  
            } 
        }
    }
    
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}

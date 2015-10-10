package com.iloilo.iloilotaxidriverapp;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import com.google.android.gms.maps.GoogleMap;
import android.location.Location;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.iloilo.iloilotaxidriverapp.asynctask.IloiloAsyncTaskHttpCall;

public class MainActivity extends Activity {
	
    private GoogleMap map;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                 WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_taxi_main);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String userId = sharedPreferences.getString("IloiloTaxi_UserId", "1234567890");
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setCompassEnabled(false);
        GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
           
            public void onMyLocationChange(Location location) {
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 14.0f));
                String[] params = new String[2];
                params[0] = "method=3";
                params[1] = "POST";
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("user_id", userId));
                nameValuePairs.add(new BasicNameValuePair("latitude", Double.toString(loc.latitude)));
                nameValuePairs.add(new BasicNameValuePair("longitude", Double.toString(loc.longitude)));
                new IloiloAsyncTaskHttpCall(nameValuePairs).execute(params);
            }
        };
        map.setOnMyLocationChangeListener(myLocationChangeListener);
   
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        map.setMyLocationEnabled(false);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        map.setMyLocationEnabled(true);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

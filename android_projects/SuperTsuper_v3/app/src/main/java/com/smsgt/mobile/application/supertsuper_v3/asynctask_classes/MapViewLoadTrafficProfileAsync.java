package com.smsgt.mobile.application.supertsuper_v3.asynctask_classes;

import java.util.List;
import com.mapquest.android.maps.DefaultItemizedOverlay;
import com.mapquest.android.maps.MapView;
import com.smsgt.mobile.application.supertsuper_v3.util_helpers.SqliteDatabaseTsuperHelper;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class MapViewLoadTrafficProfileAsync extends AsyncTask <Void,Void,String> {

	private MapView mapView;
	private List<DefaultItemizedOverlay> currentOverlayList;
	private List<String> previousOverlayList;
	private Context contxt;
	
	public MapViewLoadTrafficProfileAsync(MapView m, List<DefaultItemizedOverlay> o, Context c) {
		this.mapView = m;
		this.currentOverlayList = o;
	    this.contxt = c;
	}
	
	@Override
	protected String doInBackground(Void... params) {
		
		SqliteDatabaseTsuperHelper sqliteDatabaseTsuperHelper = new SqliteDatabaseTsuperHelper(contxt);
		this.previousOverlayList = sqliteDatabaseTsuperHelper.getAllTsuperTrafficProfileFromDB();
		
		if(this.previousOverlayList != null) {
			Log.e("", "removing previous overlays from mapview");
			for(String key : previousOverlayList) {
				this.mapView.removeOverlayByKey(key);
			}
			sqliteDatabaseTsuperHelper.deleteTsuperTrafficProfileToDB();
		} 
		
		for(DefaultItemizedOverlay d : currentOverlayList) {
			this.mapView.getOverlays().add(d);
			sqliteDatabaseTsuperHelper.addTsuperTrafficProfileToDB(d.getKey());
		}
			
		this.previousOverlayList = null;
		this.currentOverlayList = null;
		return "";
	}
	
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		this.mapView.postInvalidate();
	}

	@Override
	protected void onPostExecute(String val) {
		
		super.onPostExecute(val);
		this.mapView.postInvalidate();
	}
}

package com.example.pin_o_memo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LocationReceiver extends BroadcastReceiver {
	@Override
    public void onReceive(Context context, Intent intent)
    {
		int lat = intent.getIntExtra("lat",0);
	 	int lng = intent.getIntExtra("lng",0);
	 	Intent service1 = new Intent(context, MyLocationService.class);
	 	service1.putExtra("lat", lat);
	 	service1.putExtra("lng", lng);
	 	context.startService(service1);
    } 
}

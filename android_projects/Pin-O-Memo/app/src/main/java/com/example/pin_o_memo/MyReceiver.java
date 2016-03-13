package com.example.pin_o_memo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver{
	 @Override
	    public void onReceive(Context context, Intent intent)
	    {
		 	String lat = intent.getStringExtra("lat");
		 	String lng = intent.getStringExtra("lng");
	       Intent service1 = new Intent(context, MyAlarmService.class);
	       service1.putExtra("lat", lat);
	       service1.putExtra("lng", lng);
	       context.startService(service1);
	    }   
}

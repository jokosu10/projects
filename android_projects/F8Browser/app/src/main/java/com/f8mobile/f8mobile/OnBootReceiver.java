package com.f8mobile.f8mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /*Intent i = new Intent("com.prac.test.MyPersistingService");
        i.setClass(context, MyPersistingService.class);
        context.startService(i);*/
    	
    	
    	if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED))
    	   {
    	     //here we start the service             
    	     //Intent serviceIntent = new Intent(context, AndroidStartServiceOnBoot.class);
    	     //context.startService(serviceIntent);
	    		Intent i = new Intent(context, Login.class);
	            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	
	            context.startActivity(i);
    	   }
    }
}
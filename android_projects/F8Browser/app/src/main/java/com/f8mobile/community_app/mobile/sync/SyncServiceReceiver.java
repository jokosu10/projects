package com.f8mobile.community_app.mobile.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SyncServiceReceiver extends BroadcastReceiver { 

    @Override
    public void onReceive(Context context, Intent intent) {
    	
           Intent weeklyUpdater = new Intent(context, SyncService.class); 
           context.startService(weeklyUpdater);
           Log.e("AlarmReceiver", "Called context.startService from AlarmReceiver.onReceive");
   } 
}

package com.f8mobile.f8mobile;

import java.util.List;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class NetworkStateReceiver extends BroadcastReceiver {
	private Context mContext;
	@Override
    public void onReceive(final Context context, final Intent intent) {
		mContext = context;
        String status = NetworkUtil.getConnectivityStatusString(context);
        
          
        
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);



        // get the info from the currently running task
        List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(1); 

        //Log.d("topActivity", "CURRENT Activity ::"
        //        + taskInfo.get(0).topActivity.getClassName());

        try{
        	ComponentName componentInfo = taskInfo.get(0).topActivity;
        	componentInfo.getPackageName();
        }
        catch(Exception e){
        	
        }
      
      String className = taskInfo.get(0).topActivity.getClassName();
      
      //Log.d("topActivity", "CURRENT Activity ::"
       //       + className);
      
        //Toast.makeText(context, status, Toast.LENGTH_LONG).show();
      
      if (status.equals("3") & className.equals("com.f8mobile.f8mobile.Login") ){
      	Intent mIntent = new Intent(mContext,Login.class); //Same as above two lines
      	//mIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
  		mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
  		context.startActivity(mIntent);
      }
 
	}
	
	
}

package com.iloilo.iloilotaxidriverapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.os.Handler;
import android.preference.PreferenceManager;

public class SplashActivity extends Activity {
	
	private final int SPLASH_DISPLAY_LENGTH = 1500;
	private boolean isFirstRun = true;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		 super.onCreate(savedInstanceState);
		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                 WindowManager.LayoutParams.FLAG_FULLSCREEN);
         requestWindowFeature(Window.FEATURE_NO_TITLE);
		 
	     setContentView(R.layout.splash_layout);

	     final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());	
	     String temp = sharedPreferences.getString("IloiloTaxi_FirstRun", "null");
	     Log.e("", "value From SharedPreferences: " + temp);
	     if(temp.equalsIgnoreCase("true") || temp.equalsIgnoreCase("null")) {
	        isFirstRun = true;
	     } else {
	        isFirstRun = false;
	     }
	     
	        /* New Handler to start the another activity 
	         * and close this Splash-Screen after 3 seconds.*/
	        new Handler().postDelayed(new Runnable(){
	            @Override
	            public void run() {
	            		
	            	try {
	            		
	            		Log.e("","value of isFirstRun: " + isFirstRun);
	            		if(isFirstRun) {
			                // store in shared preferences
	            			isFirstRun = false; 
		                    SharedPreferences.Editor editor = sharedPreferences.edit();
		                    editor.putString("IloiloTaxi_FirstRun", String.valueOf(isFirstRun));
		                    editor.commit(); 
		                    Intent mainIntent = new Intent(SplashActivity.this,RegisterActivity.class);
		            		SplashActivity.this.startActivity(mainIntent);
			                
	            		} else {
	            			
	            			Intent mainIntent = new Intent(SplashActivity.this, RequestorsActivity.class);
	            			SplashActivity.this.startActivity(mainIntent);	             			
	            		}
	            		
	            	} catch(Exception e) {
	            		Log.e("", "", e);
	            		isFirstRun = false; 
		               
	                    SharedPreferences.Editor editor = sharedPreferences.edit();
	                    editor.putString("IloiloTaxi_FirstRun", String.valueOf(isFirstRun));
	                    editor.commit(); 
	            		Intent mainIntent = new Intent(SplashActivity.this,RequestorsActivity.class);
	            		SplashActivity.this.startActivity(mainIntent);
		               
	            	}
	                SplashActivity.this.finish();
	            }
	        }, SPLASH_DISPLAY_LENGTH);  
	    }
}

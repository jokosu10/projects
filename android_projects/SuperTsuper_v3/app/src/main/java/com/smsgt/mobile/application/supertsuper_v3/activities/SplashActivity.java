package com.smsgt.mobile.application.supertsuper_v3.activities;

import java.util.ArrayList;

import com.smsgt.mobile.application.supertsuper_v3.R;
import com.smsgt.mobile.application.supertsuper_v3.asynctask_classes.GetAllRewardsFromStrongloop;
import com.smsgt.mobile.application.supertsuper_v3.database.model.RewardsModel;
import com.smsgt.mobile.application.supertsuper_v3.strongloop.model.StrongloopRewardsModel;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class SplashActivity extends Activity {

	 	private final int SPLASH_DISPLAY_LENGTH = 3000;
		private boolean isFirstRun;
		private ArrayList<StrongloopRewardsModel> strongLoopRewardsList;
		private ArrayList<RewardsModel> rewardsList = new ArrayList<RewardsModel>();
		private static Handler asyncHandler;
//		private ConnectivityManager cm;
		
	    /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle icicle) {
	        super.onCreate(icicle);
	        
	        if (Build.VERSION.SDK_INT < 16) {
	            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
	            requestWindowFeature(Window.FEATURE_NO_TITLE);
	        }
	        
	        setContentView(R.layout.splash_activity);
	       	        
			final Handler handler = new Handler();
	        handler.postDelayed(new Runnable() {
	            @Override
	            public void run() {
	                
	            	((ImageView)findViewById(R.id.splash_image_place_holder)).setImageResource(R.drawable.splash_logo1);
	            }
	        }, 500);
    	
			final Handler handler2 = new Handler();
	        handler2.postDelayed(new Runnable() {
	            @Override
	            public void run() {
	                
	            	((ImageView)findViewById(R.id.splash_image_place_holder)).setImageResource(R.drawable.splash_logo2);
	            }
	        }, 600);
	     
			final Handler handler3 = new Handler();
	        handler3.postDelayed(new Runnable() {
	            @Override
	            public void run() {
	                
	            	((ImageView)findViewById(R.id.splash_image_place_holder)).setImageResource(R.drawable.splash_logo3);
	            }
	        }, 700);
	       
			final Handler handler4 = new Handler();
	        handler4.postDelayed(new Runnable() {
	            @Override
	            public void run() {
	                
	            	((ImageView)findViewById(R.id.splash_image_place_holder)).setImageResource(R.drawable.splash_logo4);
	            }
	        }, 800);
	       
			final Handler handler5 = new Handler();
	        handler5.postDelayed(new Runnable() {
	            @Override
	            public void run() {
	                
	            	((ImageView)findViewById(R.id.splash_image_place_holder)).setImageResource(R.drawable.splash_logo5);
	            }
	        }, 900);
	        
//	        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	        
//	        boolean isInternetConnected = isInternetConnected();
//	        Log.e("", "" + isInternetConnected);
	        
/*	        if(!isInternetConnected) {
	        	Toast.makeText(getApplicationContext(), "Internet Connection is needed to run this application!",  Toast.LENGTH_LONG).show();
	        	finish();
	        	//startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
	        } else {*/
		        asyncHandler = new Handler() { 
					@SuppressWarnings("unchecked")
					public void handleMessage(Message msg) {
		                switch (msg.what) {
		                case 1:
		                	try {
			                	strongLoopRewardsList = (ArrayList<StrongloopRewardsModel>) msg.obj;
			                	for(StrongloopRewardsModel sr : strongLoopRewardsList) {	        				
			                		RewardsModel r = new RewardsModel();
			                		r.setRewardImageName(sr.getRewards_image_name());
			                		r.setRewardsItemEquivalentPoints(String.valueOf(sr.getRewards_equivalent_points()));
			                		r.setRewardsImageId(sr.getRewards_id());
			                		rewardsList.add(r);
			                	}
		                	} catch(Exception e) {
		                		Log.e("", "", e);
		                	}
		                    break;
		                }
		        	}
		        };

		        final Handler getRewardsHandler = new Handler();
		        getRewardsHandler.post(new Runnable() {
					
					@Override
					public void run() {
				        String[] params = new String[2];
						params[0] = "Rewards";
						params[1] = "GET";
						
						try {
							new GetAllRewardsFromStrongloop(getApplicationContext(), asyncHandler).execute(params);
							
						} catch (Exception e) {
							Log.e("", "", e);
						}
					}
				});
		        		        
		        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());	
		        String temp = sharedPreferences.getString("SuperTsuper_FirstRun", "null");
		        Log.e("", "value From SharedPreferences: " + temp);
		        if(temp.equalsIgnoreCase("true") || temp.equalsIgnoreCase("null")) {
		        	isFirstRun = true;
		        } else {
		        	isFirstRun = false;
		        }
		        
		        /* New Handler to start the another activity 
		         * and close this Splash-Screen after 2 seconds.*/
		        new Handler().postDelayed(new Runnable(){
		            @Override
		            public void run() {
		            		
		            	try {
		            		
		            		if(isFirstRun) {
				                // store in shared preferences
		            			isFirstRun = false; 
			                    SharedPreferences.Editor editor = sharedPreferences.edit();
			                    editor.putString("SuperTsuper_FirstRun", String.valueOf(isFirstRun));
			                    editor.commit(); 
			                    
			                    Intent mainIntent = new Intent(SplashActivity.this,UserRegistrationActivity.class);
			            		mainIntent.putExtra("rewardsList", rewardsList);
			            		SplashActivity.this.startActivity(mainIntent);
				                
		            		} else {
		            			
		            			Intent mainIntent = new Intent(SplashActivity.this,UserLoginActivity.class);
			            		mainIntent.putExtra("rewardsList", rewardsList);
			            		SplashActivity.this.startActivity(mainIntent);	             			
		            		}
		            	} catch(Exception e) {
		            		Log.e("", "", e);
	            			isFirstRun = false; 
			               
		                    SharedPreferences.Editor editor = sharedPreferences.edit();
		                    editor.putString("SuperTsuper_FirstRun", String.valueOf(isFirstRun));
		                    editor.commit(); 
		            		Intent mainIntent = new Intent(SplashActivity.this,UserRegistrationActivity.class);
		            		mainIntent.putExtra("rewardsList", rewardsList);
		            		SplashActivity.this.startActivity(mainIntent);
			               
		            	}
		                SplashActivity.this.finish();
		            }
		        }, SPLASH_DISPLAY_LENGTH);  
	     //   } 
	    }
	    
/*	 protected boolean isInternetConnected() {
		 
		 if(cm == null) {
			 return false;
		 }
		        
	    final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
	    if (activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED) {
	    	if(activeNetwork.isConnected()) {
	    		return true;
	    	} else {
	    		return false;
	    	}   
	    }
		 return false;
	 }*/
	    
	    
}

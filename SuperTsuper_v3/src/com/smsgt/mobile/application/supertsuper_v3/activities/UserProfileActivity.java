package com.smsgt.mobile.application.supertsuper_v3.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.smsgt.mobile.application.supertsuper_v3.R;
import com.smsgt.mobile.application.supertsuper_v3.R.id;
import com.smsgt.mobile.application.supertsuper_v3.asynctask_classes.AsyncTaskStrongloopCommonHttpCall;
import com.smsgt.mobile.application.supertsuper_v3.asynctask_classes.GetAllTrafficProfilesFromStrongloop;
import com.smsgt.mobile.application.supertsuper_v3.database.model.AchievementsModel;
import com.smsgt.mobile.application.supertsuper_v3.database.model.RewardsModel;
import com.smsgt.mobile.application.supertsuper_v3.database.model.TsuperTrafficProfileModel;
import com.smsgt.mobile.application.supertsuper_v3.database.model.TsuperUserProfileModel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
//import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserProfileActivity extends Activity {
	
	private Button StartDrive, Achievements, Rewards;
	private TextView nameTextView, memberSinceTextView, totalDistanceTextView, totalPointsTextView;
	private TsuperUserProfileModel tsuperUserProfileModel;
	private ArrayList<RewardsModel> rewardsList;
	private ArrayList<AchievementsModel> achievementsList = new ArrayList<AchievementsModel>();
	private ArrayList<TsuperTrafficProfileModel> tsuperTrafficProfileList = new ArrayList<TsuperTrafficProfileModel>();
	private static Handler achievementsHandler;
	private String /*tsuperId, tsuperJSONProfile, */achievementsJSONResponse;
	//private JSONObject tsuperJObject;
	private JSONArray achievementsJArray;
	private boolean isGpsEnabled = false;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		try {
	        if (Build.VERSION.SDK_INT < 16) {
	            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
	            requestWindowFeature(Window.FEATURE_NO_TITLE);
	        }
			
	        super.onCreate(savedInstanceState);
			setContentView(R.layout.user_profile); 
			
			tsuperUserProfileModel = (TsuperUserProfileModel) getIntent().getSerializableExtra("tsuperUserProfile");
			
			rewardsList = (ArrayList<RewardsModel>) getIntent().getSerializableExtra("rewardsList");
			
			 Achievements = (Button)findViewById(id.Achievements);
			 Rewards = (Button)findViewById(id.Rewards);
			 nameTextView = (TextView) findViewById(R.id.userProfileNameTextView);
			 memberSinceTextView = (TextView) findViewById(R.id.userProfileMemberSinceTextView);
			 totalDistanceTextView = (TextView) findViewById(R.id.totalDistanceTextView);
			 totalPointsTextView = (TextView) findViewById(R.id.totalPointsTextView);
			 
			 final View startDriveButtonLayout = getWindow().getLayoutInflater().inflate(R.layout.userprofile_startdrive_layout, null);
		        
	         int width = getWindow().getAttributes().width, height = getWindow().getAttributes().height;
	         LayoutParams params = new LayoutParams(width, height);
	         getWindow().addContentView(startDriveButtonLayout, params);
	         
	         StartDrive = (Button) startDriveButtonLayout.findViewById(R.id.StartDrive); 
	         
			 nameTextView.setText(tsuperUserProfileModel.getFirstName() + " " + tsuperUserProfileModel.getLastName());
			 memberSinceTextView.setText("Member Since: " + tsuperUserProfileModel.getMembershipDate());
			 totalDistanceTextView.setText(String.format("%06d", (int) Math.round(Float.parseFloat(tsuperUserProfileModel.getTotalDistance()))));
			 totalPointsTextView.setText(String.format("%06d", (int) Math.round(Float.parseFloat(tsuperUserProfileModel.getTotalPoints())))); 
//			 AddVehicle =(Button) findViewById(id.AddVehicle);
//			 Tips = (Button) findViewById(R.id.tooltip);
			 
			 achievementsHandler = new Handler() { 
					public void handleMessage(Message msg) {
		                switch (msg.what) {
		                case 1:		                	
		                	// parse json response then convert to AchievementsModel object
		                	achievementsJSONResponse = (String) msg.obj;
		                	if(!achievementsJSONResponse.trim().isEmpty()) {
		                		try {
									achievementsJArray = new JSONArray(achievementsJSONResponse);
								} catch (JSONException e) {
									e.printStackTrace();
								}
		                	
		                		for(int index = 0; index < achievementsJArray.length(); index++) {
		                			try {
										String indexString = achievementsJArray.getString(index);
										JSONObject jObj = new JSONObject(indexString);
										
										AchievementsModel ac = new AchievementsModel();
				                		ac.setAchievementDescription(jObj.getString("achievement_description"));
				                		ac.setAchievementId(jObj.getString("achievement_id"));
				                		ac.setAchievementImageName(jObj.getString("achievement_image_name"));
				                		ac.setAchievementName(jObj.getString("achievement_name"));
				                		ac.setAchievementValue(jObj.getString("achievement_value"));
				                		achievementsList.add(ac);
										
									} catch (JSONException e) {
										e.printStackTrace();
									}	
		                		} 	                		
		                	}
		                    break;
		                case 2:  
							ArrayList<TsuperTrafficProfileModel> listTrafficProfiles = (ArrayList<TsuperTrafficProfileModel>) msg.obj;
                    		
							if(listTrafficProfiles.size() > 0 && listTrafficProfiles != null) {
                    			//showTrafficProfiles(listTrafficProfiles);
								tsuperTrafficProfileList = listTrafficProfiles;
                    		} 
                    	    break;   
		                }
		        	}
		        };

//		        tsuperId = (String) getIntent().getStringExtra("tsuperId"); 
//				if(tsuperId.trim().isEmpty()) {
//					// fetch from Strongloop
//					String[] params2 = new String[2];
//					params2[0] = "UserRegistrations/findOne?filter[where][userName]=" + tsuperUserProfileModel.getUserName();
//					params2[1] = "GET";
//					
//					try {
//						
//						Log.e("", "calling SendTsuperUserProfileToStrongloop");
//						
//						String response = new AsyncTaskStrongloopCommonHttpCall().execute(params2).get();
//						tsuperJSONProfile = response;
//	                	if(!tsuperJSONProfile.equalsIgnoreCase("null")) {
//	                		try {
//								tsuperJObject = new JSONObject(tsuperJSONProfile);
//								tsuperId = tsuperJObject.getString("id");
//								tsuperUserProfileModel.setId(tsuperId);
//							} catch (JSONException e) {
//								e.printStackTrace();
//							}
//	                	}
//						
//						//new SendTsuperUserProfileToStrongloop(tsuperUserProfileModel, getApplicationContext(), achievementsHandler).execute(params2);
//						
//					} catch (Exception e) {
//						Log.e("", "", e);
//					}
//				}		        
		        
			// fetch achievements
			getOrUpdateAchievements();	
			
			LocationManager locManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
			
			isGpsEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if(!isGpsEnabled) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			    builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
			           .setCancelable(false)
			           .setTitle("")
			           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			               public void onClick(final DialogInterface dialog, final int id) {
			                   startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
			               }
			           })
			           .setNegativeButton("No", new DialogInterface.OnClickListener() {
			               public void onClick(final DialogInterface dialog, final int id) {
			                    dialog.cancel();
			               }
			           });
			    final AlertDialog alert = builder.create();
			    alert.show();
			} 
			
			
			 StartDrive.setOnClickListener(new OnClickListener(){
	            @Override
	            public void onClick(View v) 
	            {
	            	Intent i = new Intent(UserProfileActivity.this,DriveActivity.class);
	            	i.putExtra("tsuperUserProfile", tsuperUserProfileModel);
	            	i.putExtra("rewardsList", rewardsList);
	            	i.putExtra("trafficProfileList", tsuperTrafficProfileList);
	   			 	startActivityForResult(i, 1);  
	            }});
		
			Achievements.setOnClickListener(new OnClickListener(){
	            @Override
	            public void onClick(View v) 
	            {
	            	Intent i = new Intent(UserProfileActivity.this,UserAchievementsActivity.class);
	            	i.putExtra("achievementsList", achievementsList);
	   			 	startActivity(i);  
	            }});
				 
			Rewards.setOnClickListener(new OnClickListener(){
	            @Override
	            public void onClick(View v) 
	            {
	            	Intent i = new Intent(UserProfileActivity.this,UserRewardsActivity.class);
	            	i.putExtra("tsuperUserProfile", tsuperUserProfileModel);
	            	i.putExtra("rewardsList", rewardsList);
	   			 	startActivityForResult(i, 2);  
	            }});
			
			} catch(Exception e) {
				Log.e("UserProfileActivity", "Error on onCreate", e);
			}	
		
		runVehicleImageSlide();
		new GetAllTrafficProfilesFromStrongloop(achievementsHandler,"UserProfileActivity").execute();
	     
	}   
	
	
	protected void getOrUpdateAchievements() {
		
			 final Handler getAchievementsHandler = new Handler();
		        getAchievementsHandler.post(new Runnable() {
					
					@Override
					public void run() {
				        String[] params = new String[2];
				        
						params[0] = "UserAchievements?filter[where][achievement_owner]=" + tsuperUserProfileModel.getUserName();
						params[1] = "GET";
						
						Log.e("", "url: " + params[0]);
							
						try {
							new AsyncTaskStrongloopCommonHttpCall(achievementsHandler).execute(params);
							//new AsyncTaskStrongloopCommonHttpCall(achievementsHandler).execute(params2);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}
	
	
	protected void runVehicleImageSlide() {
		
		final LinearLayout layout = (LinearLayout) findViewById(R.id.userProfileLinearLayout);
		
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
		    @SuppressWarnings("deprecation")
			@Override
		    public void run() {
		        
		    	layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.select_1));
		    	//((ImageView)findViewById(R.id.image_place_holder)).setImageResource(R.drawable.select_1);
		    }
		}, 500);
		handler.postDelayed(new Runnable() {
		    @SuppressWarnings("deprecation")
			@Override
		    public void run() {
		    	
		    	layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.select_2));
				
		    	//((ImageView)findViewById(R.id.image_place_holder)).setImageResource(R.drawable.select_2);
		    }
		}, 4000);
		handler.postDelayed(new Runnable() {
		    @SuppressWarnings("deprecation")
			@Override
		    public void run() {
		        
		    	layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.select_3));
				
		    	//((ImageView)findViewById(R.id.image_place_holder)).setImageResource(R.drawable.select_3);
		    }
		}, 8000);
	    handler.postDelayed(new Runnable() {
	        @SuppressWarnings("deprecation")
			@Override
	        public void run() {
	            
	        	layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.select_4));
	    		
	        	//((ImageView)findViewById(R.id.image_place_holder)).setImageResource(R.drawable.select_4);
	        }
	    }, 12000);
        handler.postDelayed(new Runnable() {
            @SuppressWarnings("deprecation")
			@Override
            public void run() {
                
            	layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.select_5));
        		
            	//((ImageView)findViewById(R.id.image_place_holder)).setImageResource(R.drawable.select_5);
            }
        }, 16000);
        handler.postDelayed(new Runnable() {
            @SuppressWarnings("deprecation")
			@Override
            public void run() {
                
            	layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.select_6));
        		
            	//((ImageView)findViewById(R.id.image_place_holder)).setImageResource(R.drawable.select_6);
            }
        }, 20000);
        handler.postDelayed(new Runnable() {
            @SuppressWarnings("deprecation")
			@Override
            public void run() {
                
            	layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.select_7));
        		
            	//((ImageView)findViewById(R.id.image_place_holder)).setImageResource(R.drawable.select_7);
            }
	    }, 24000);
	}
	

	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {    
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("",""+resultCode); 
		if (resultCode == RESULT_OK && data != null) {
			
			if(requestCode == 1 || requestCode == 2) {
				
				tsuperUserProfileModel = (TsuperUserProfileModel) data.getSerializableExtra("tsuperUserProfile");
				nameTextView.setText(tsuperUserProfileModel.getFirstName() + " " + tsuperUserProfileModel.getLastName());
				memberSinceTextView.setText("Member Since: " + tsuperUserProfileModel.getMembershipDate());
				totalDistanceTextView.setText(String.format("%06d", (int) Math.round(Float.parseFloat(tsuperUserProfileModel.getTotalDistance()))));
				totalPointsTextView.setText(String.format("%06d", (int) Math.round(Float.parseFloat(tsuperUserProfileModel.getTotalPoints()))));
			
				// try to update achievements
				achievementsList.clear();
				getOrUpdateAchievements();
			
			} 
		} 
	}	
} 
			

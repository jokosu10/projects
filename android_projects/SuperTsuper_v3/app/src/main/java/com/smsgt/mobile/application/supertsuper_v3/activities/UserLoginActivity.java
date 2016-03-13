package com.smsgt.mobile.application.supertsuper_v3.activities;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import com.smsgt.mobile.application.supertsuper_v3.R;
import com.smsgt.mobile.application.supertsuper_v3.asynctask_classes.AsyncTaskStrongloopCommonHttpCall;
import com.smsgt.mobile.application.supertsuper_v3.database.model.RewardsModel;
import com.smsgt.mobile.application.supertsuper_v3.database.model.TsuperUserProfileModel;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserLoginActivity extends Activity {
	
	 private TsuperUserProfileModel tsuperUserProfile;
	 private Button userLoginButton;
	 private EditText usernameEditText, passwordEditText;
	 private int counter;
	 private ArrayList<RewardsModel> rewardsList;
	 private JSONObject jObject;
	 private ProgressDialog progressDialog;
	 
	 @SuppressWarnings("unchecked")
	 public void onCreate(Bundle icicle) {
	        super.onCreate(icicle);
	        
	        if (Build.VERSION.SDK_INT < 16) {
	            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
	            requestWindowFeature(Window.FEATURE_NO_TITLE);
	        }
	        
	        setContentView(R.layout.user_login);
	        userLoginButton = (Button) findViewById(R.id.loginButton);
	        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
	        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
	        progressDialog = new ProgressDialog(UserLoginActivity.this);	 
	        
	        rewardsList = (ArrayList<RewardsModel>) getIntent().getSerializableExtra("rewardsList");
	        
	        userLoginButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					if(usernameEditText.getText().toString().trim().isEmpty() || passwordEditText.getText().toString().trim().isEmpty()) {
						Toast.makeText(getApplicationContext(), "Mandatory field/s has empty values. Please try again!", Toast.LENGTH_SHORT).show();
					} else {
						// 3 max tries
						final String un = usernameEditText.getText().toString();
						final String pw = passwordEditText.getText().toString();
	

						String params[] = new String[2];
						params[0] = "UserRegistrations/findOne?filter[where][userName]=" + un + "&filter[where][password]=" + pw;
						params[1] = "GET";
						
						try {
							String jsonString = new AsyncTaskStrongloopCommonHttpCall(progressDialog).execute(params).get();
							if(!jsonString.equalsIgnoreCase("null")) {
								jObject = new JSONObject(jsonString);
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						if(jObject != null) {
							String tsuperId = "";
							try {
								tsuperId = jObject.getString("userId");
								tsuperUserProfile = new TsuperUserProfileModel();
								tsuperUserProfile.setAverageMovingSpeed(jObject.getString("averageMovingSpeed"));
								tsuperUserProfile.setCity(jObject.getString("city"));
								tsuperUserProfile.setContactNumber(jObject.getString("contactNumber"));
								tsuperUserProfile.setCurrDistance(jObject.getString("currDistance"));
								tsuperUserProfile.setFirstName(jObject.getString("firstName"));
								tsuperUserProfile.setLastName(jObject.getString("lastName"));
								tsuperUserProfile.setMembershipDate(jObject.getString("membershipDate"));
								tsuperUserProfile.setPassword(jObject.getString("password"));
								tsuperUserProfile.setPrevDistance(jObject.getString("prevDistance"));
								tsuperUserProfile.setTotalDistance(jObject.getString("totalDistance"));
								tsuperUserProfile.setTotalPoints(jObject.getString("totalPoints"));
								tsuperUserProfile.setTravelBearing(jObject.getString("travelBearing"));
								tsuperUserProfile.setTravelLatitude(jObject.getString("travelLatitude"));
								tsuperUserProfile.setTravelLongitude(jObject.getString("travelLongitude"));
								tsuperUserProfile.setUserName(jObject.getString("userName"));
								tsuperUserProfile.setUserId(tsuperId);
								
								Log.e("", "tsuperId: " + tsuperId);
							} catch (JSONException e) {
								Log.e("", "", e);
							}
							
							if(!tsuperId.trim().isEmpty()) {
			            		Intent userProfileIntent = new Intent(UserLoginActivity.this,UserProfileActivity.class);
			            		userProfileIntent.putExtra("tsuperUserProfile", tsuperUserProfile);
			            		userProfileIntent.putExtra("rewardsList", rewardsList);
			            		userProfileIntent.putExtra("tsuperId", tsuperId);
				                UserLoginActivity.this.startActivity(userProfileIntent);
				                finish();
							}
						} else {
							Toast.makeText(getApplicationContext(), "Unknown user/Invalid credentials. Please try again!", Toast.LENGTH_SHORT).show();
		            		counter++;
		            		usernameEditText.setText("");
		            		passwordEditText.setText("");
		            		usernameEditText.requestFocus();
		            		
		            		if(counter >= 3) {
		            			Toast.makeText(getApplicationContext(), "Max count of invalid login attempts had been reached. Taking you to Registration Screen", Toast.LENGTH_SHORT).show();
			            		Intent userRegistrationIntent = new Intent(UserLoginActivity.this,UserRegistrationActivity.class);
			            		userRegistrationIntent.putExtra("rewardsList", rewardsList);
			            		UserLoginActivity.this.startActivity(userRegistrationIntent);
				                finish();
		            		}
						}	
					}
				}
			});      
	 }
}

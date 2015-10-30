package com.smsgt.mobile.application.supertsuper_v3.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import com.smsgt.mobile.application.supertsuper_v3.R;
import com.smsgt.mobile.application.supertsuper_v3.asynctask_classes.AsyncTaskStrongloopCommonHttpCall;
import com.smsgt.mobile.application.supertsuper_v3.asynctask_classes.SendTsuperUserProfileToStrongloop;
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

public class UserRegistrationActivity extends Activity {
	
	private Button RegButton;
	private EditText UserName;
	private EditText Password;
	private EditText FirstName;
	private EditText LastName;
	private EditText ContactNo;
	private EditText City;
	private ProgressDialog progressDialog;
	
	private TsuperUserProfileModel tsuperUserProfile;
	private ArrayList<RewardsModel> rewardsList;
	private String stringForUserValidation = "";
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		try {
			super.onCreate(savedInstanceState);
			 if (Build.VERSION.SDK_INT < 16) {
		            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
		            requestWindowFeature(Window.FEATURE_NO_TITLE);
		     }
			 
			 setContentView(R.layout.user_registration);
			 
			 RegButton = (Button)findViewById(R.id.Register);
			 UserName  = (EditText)findViewById(R.id.UserName);
			 Password = (EditText)findViewById(R.id.Password);
			 FirstName = (EditText)findViewById(R.id.FirstName);
			 LastName = (EditText)findViewById(R.id.LastName);
			 ContactNo = (EditText)findViewById(R.id.ContactNo);
			 City = (EditText)findViewById(R.id.City);
	     	
			 progressDialog = new ProgressDialog(UserRegistrationActivity.this);	 
			 rewardsList = (ArrayList<RewardsModel>) getIntent().getSerializableExtra("rewardsList");
			 
			 RegButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						final String un = UserName.getText().toString();
						String pw = Password.getText().toString();
						String fn = FirstName.getText().toString();
						String ln = LastName.getText().toString();
						String cn = ContactNo.getText().toString();
						String ct = City.getText().toString();
						
						if(un.trim().isEmpty() || pw.trim().isEmpty() || fn.trim().isEmpty() || ln.trim().isEmpty()
								|| cn.trim().isEmpty() || ct.trim().isEmpty()) {
							
							Toast.makeText(getApplicationContext(), "Mandatory field/s has empty value. Please try again!", Toast.LENGTH_SHORT).show();
							
						} else {
							
							String[] param = new String[] {"UserRegistrations/findOne?filter[where][userName]=" + un , "GET"};	
							try {
								stringForUserValidation = new AsyncTaskStrongloopCommonHttpCall(progressDialog).execute(param).get();
							} catch (Exception e) {
								Log.e("", "", e);
							}
														
							if(stringForUserValidation.equalsIgnoreCase("null")) {
								// save to strongloop (via AsyncTask)
								
							    Log.e("", "" + UUID.randomUUID().toString());
								tsuperUserProfile = new TsuperUserProfileModel();
								tsuperUserProfile.setUserId(UUID.randomUUID().toString());
				                tsuperUserProfile.setUserName(un);
				                tsuperUserProfile.setPassword(pw);
				                tsuperUserProfile.setFirstName(fn);
				                tsuperUserProfile.setLastName(ln);
				                tsuperUserProfile.setContactNumber(cn);
				                tsuperUserProfile.setCity(ct);
				                tsuperUserProfile.setTotalPoints("0");
				                tsuperUserProfile.setAverageMovingSpeed("0");
				                tsuperUserProfile.setTravelBearing("0");
				                tsuperUserProfile.setTravelLongitude("0");
				                tsuperUserProfile.setTravelLatitude("0");
				                tsuperUserProfile.setPrevDistance("0");
				                tsuperUserProfile.setCurrDistance("0");
				                tsuperUserProfile.setTotalDistance("0"); 
				                
				                Date dateNow = new Date();
				                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");   
				                tsuperUserProfile.setMembershipDate(format.format(dateNow));
				                
				                String[] params = new String[2];
				                params[0] = "UserRegistrations";
				                params[1] = "POST";
				                
								new SendTsuperUserProfileToStrongloop(tsuperUserProfile, getApplicationContext()).execute(params);
								
								Intent myIntent = new Intent(getApplicationContext(), UserProfileActivity.class); 
								myIntent.putExtra("tsuperUserProfile", tsuperUserProfile);
								myIntent.putExtra("rewardsList", rewardsList);
								startActivity(myIntent);
								finish();

							} else {
								Toast.makeText(getApplicationContext(), "User already exists! Please try with a different username instead!", Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
			 
			
			} catch(Exception e) {
				Log.e("UserRegistrationActivity", "Error on onCreate", e);
			}
	}
}
package com.iloilo.iloilotaxidriverapp;

import android.app.Activity;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.Intent;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.iloilo.iloilotaxidriverapp.asynctask.GcmAsyncTask;
import com.iloilo.iloilotaxidriverapp.asynctask.IloiloAsyncTaskHttpCall;

import android.view.View;

public class RegisterActivity extends Activity {
	
   private EditText editTextAddress;
   private EditText editTextContactNumber;
   private EditText editTextFname;
   private EditText editTextLname;
   private EditText editTextTaxiCompany;
   private EditText editTextTaxiPlateNumber;
   private Boolean isFirstRun;
   private Button registerBtn;
   private String userId;
   
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                 WindowManager.LayoutParams.FLAG_FULLSCREEN);
       requestWindowFeature(Window.FEATURE_NO_TITLE);
       setContentView(R.layout.taxi_registration_layout);
       final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
       
       new GcmAsyncTask(GoogleCloudMessaging.getInstance(getApplicationContext()), sharedPreferences).execute();
       userId = generateRandomId();
       editTextFname = (EditText)findViewById(R.id.editTextFName);
       editTextLname = (EditText)findViewById(R.id.editTextLName);
       editTextAddress = (EditText)findViewById(R.id.editTextAddress);
       editTextContactNumber = (EditText)findViewById(R.id.editTextContactNumber);
       editTextTaxiCompany = (EditText)findViewById(R.id.editTextTaxiCompany);
       editTextTaxiPlateNumber = (EditText)findViewById(R.id.editTextTaxiPlateNumber);
       registerBtn = (Button)findViewById(R.id.buttonRegister);
       registerBtn.setOnClickListener(new View.OnClickListener() {
         
           public void onClick(View v) {
        	   
               String fname = editTextFname.getText().toString();
               String lname = editTextLname.getText().toString();
               String address = editTextAddress.getText().toString();
               String contactNumber = editTextContactNumber.getText().toString();
               String taxiCompanyName = editTextTaxiCompany.getText().toString();
               String taxiPlateNumber = editTextTaxiPlateNumber.getText().toString();
               if((!fname.trim().isEmpty()) && (!lname.trim().isEmpty()) && (!address.trim().isEmpty())) {
                   if((!contactNumber.trim().isEmpty()) && (!taxiCompanyName.trim().isEmpty())) {
                       if(!taxiPlateNumber.trim().isEmpty()) {
                           isFirstRun = Boolean.valueOf(false);
                           SharedPreferences.Editor editor = sharedPreferences.edit();
                           editor.putString("IloiloTaxi_FirstRun", String.valueOf(isFirstRun));
                           editor.putString("IloiloTaxi_UserId", userId);
                           editor.putString("IloiloTaxi_ContactNumber", contactNumber);
                           editor.commit();
                           String[] params = new String[2];
                           params[0] = "method=1";
                           params[1] = "POST";
                           ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                           nameValuePairs.add(new BasicNameValuePair("user_id", userId));
                           nameValuePairs.add(new BasicNameValuePair("first_name", fname));
                           nameValuePairs.add(new BasicNameValuePair("last_name", lname));
                           nameValuePairs.add(new BasicNameValuePair("address", address));
                           nameValuePairs.add(new BasicNameValuePair("contact_number", contactNumber));
                           nameValuePairs.add(new BasicNameValuePair("user_type", "driver"));
                           nameValuePairs.add(new BasicNameValuePair("taxi_name", taxiCompanyName));
                           nameValuePairs.add(new BasicNameValuePair("taxi_plate_number", taxiPlateNumber));
                           nameValuePairs.add(new BasicNameValuePair("registration_id", sharedPreferences.getString("IloiloTaxi_GCM_RegistrationId", "")));
                           new IloiloAsyncTaskHttpCall(nameValuePairs).execute(params);
                           Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                           startActivity(mainIntent);
                           finish();
                       }
                   }
                   return;
               }
               Toast.makeText(getApplicationContext(), "Please fill up all the fields", Toast.LENGTH_SHORT).show();
           }
       });
   }
   
   private String generateRandomId() {
       SecureRandom random = new SecureRandom();
       return new BigInteger(16, random).toString();
   }
}


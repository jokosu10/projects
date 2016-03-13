package com.liniumtech.mobile.uRun;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class GetBodyInformationActivity extends Activity {
	
	private EditText weightEditText, ageEditText;
	private String weight, age, gender;
	private RadioGroup genderRadioGroup;
	private RadioButton selectedGenderRadioBtn;
	private Button btnSaveInfo;

	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                 WindowManager.LayoutParams.FLAG_FULLSCREEN);
         requestWindowFeature(Window.FEATURE_NO_TITLE);
         
		setContentView(R.layout.body_info);
				
		weightEditText = (EditText) findViewById(R.id.weightEditText);
		ageEditText = (EditText) findViewById(R.id.ageEditText);
		
		genderRadioGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);
		btnSaveInfo = (Button) findViewById(R.id.bodyInfoSubmitBtn);
		
		btnSaveInfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				weight = weightEditText.getText().toString();
				age = ageEditText.getText().toString();
				int selected =  genderRadioGroup.getCheckedRadioButtonId();
				selectedGenderRadioBtn = (RadioButton) findViewById(selected);
				gender = selectedGenderRadioBtn.getText().toString();
				
				if(weight.trim().isEmpty() && age.trim().isEmpty() && gender.trim().isEmpty()) {
					Toast.makeText(GetBodyInformationActivity.this,
							"Missing mandatory fields! Please fill them up!", Toast.LENGTH_SHORT).show();
				} else {
					
					final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					
					SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("uRun_weight", weight);
                    editor.putString("uRun_age", age);
                    editor.putString("uRun_gender", gender);
                    editor.commit();
					
					Intent mainIntent = new Intent(GetBodyInformationActivity.this,GetGoalsInformationActivity.class);
					GetBodyInformationActivity.this.startActivity(mainIntent);
					GetBodyInformationActivity.this.finish();
					
				}
				
			}
		});
		
		
	}
	
}

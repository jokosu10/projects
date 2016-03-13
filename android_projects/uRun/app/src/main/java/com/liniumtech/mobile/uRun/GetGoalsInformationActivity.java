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
import android.widget.Toast;

public class GetGoalsInformationActivity extends Activity {

	private EditText goalCaloriesEditText, goalStepsEditText;
	private Button startWorkOutBtn;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                 WindowManager.LayoutParams.FLAG_FULLSCREEN);
         requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.goals_info);
		
		goalCaloriesEditText = (EditText) findViewById(R.id.goalCaloriesEditText);
		goalStepsEditText = (EditText) findViewById(R.id.goalStepsEditText);
		startWorkOutBtn = (Button) findViewById(R.id.startWorkOutBtn);
		
		startWorkOutBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String goalCalories = goalCaloriesEditText.getText().toString();
				String goalSteps = goalStepsEditText.getText().toString();
				
				if(!goalCalories.trim().isEmpty() && !goalSteps.trim().isEmpty()) {
					
					final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					
					SharedPreferences.Editor editor = sharedPreferences.edit();
	                editor.putString("uRun_goal_calories", goalCalories);
	                editor.putString("uRun_goal_steps", goalSteps);
	                editor.commit();
					
					Intent mainIntent = new Intent(GetGoalsInformationActivity.this,URunMainActivity.class);
					GetGoalsInformationActivity.this.startActivity(mainIntent);
					GetGoalsInformationActivity.this.finish();
					
				} else {
					
					Toast.makeText(GetGoalsInformationActivity.this,
							"Missing mandatory fields! Please fill them up!", Toast.LENGTH_SHORT).show();
					
				}                
			}
		});
		
	}
}

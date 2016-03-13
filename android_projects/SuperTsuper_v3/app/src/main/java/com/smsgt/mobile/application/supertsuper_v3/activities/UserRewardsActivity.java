package com.smsgt.mobile.application.supertsuper_v3.activities;
 
import java.util.ArrayList;

import com.smsgt.mobile.application.supertsuper_v3.R;
import com.smsgt.mobile.application.supertsuper_v3.R.id;
import com.smsgt.mobile.application.supertsuper_v3.custom_ui.RewardsAdapter;
import com.smsgt.mobile.application.supertsuper_v3.database.model.RewardsModel;
import com.smsgt.mobile.application.supertsuper_v3.database.model.TsuperUserProfileModel;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;  
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
  
public class UserRewardsActivity extends Activity {  
    
  private ListView RewardsView ;  
  private RewardsAdapter rewardsAdapter;
  private Button RewardsBack;
  private TsuperUserProfileModel tsuperUserProfileModel;
  private static Handler mHandler;
  private ArrayList<RewardsModel> rewardsList;
  
  /** Called when the activity is first created. */  
  @SuppressWarnings("unchecked")
  @Override  
  public void onCreate(Bundle savedInstanceState) {  
    super.onCreate(savedInstanceState);  
    if (Build.VERSION.SDK_INT < 16) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    setContentView(R.layout.activity_user_rewards);  
    
    // Find the ListView resource.   
    RewardsView = (ListView) findViewById( R.id.RewardsView );
    RewardsBack = (Button)findViewById(id.RewardsBack);
  
    tsuperUserProfileModel = (TsuperUserProfileModel) getIntent().getSerializableExtra("tsuperUserProfile");
    rewardsList = (ArrayList<RewardsModel>) getIntent().getSerializableExtra("rewardsList");
	     
    // Create Adapter using the Rewards list.  
    rewardsAdapter = new RewardsAdapter(rewardsList, getApplicationContext(), tsuperUserProfileModel, "UserRewardsActivity");      
    // Set the Adapter as the ListView's adapter.  
    RewardsView.setAdapter(rewardsAdapter);
    RewardsBack.setOnClickListener(new OnClickListener() {
		
		  @Override
		  public void onClick(View v) {
			  Intent updatedUserProfileIntent = UserRewardsActivity.this.getIntent();
			  updatedUserProfileIntent.putExtra("tsuperUserProfile", tsuperUserProfileModel);
			  setResult(RESULT_OK,updatedUserProfileIntent); 
			  finish();
			}
		});
    
    mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: 
                		tsuperUserProfileModel = (TsuperUserProfileModel) msg.obj;
                		rewardsAdapter = new RewardsAdapter(rewardsList, getApplicationContext(), tsuperUserProfileModel, "UserRewardsActivity");
                		RewardsView.invalidate();
                		RewardsView.setAdapter(rewardsAdapter);
                	 	break;
            }
        } 
    };
    
  }
  
	 public static void refreshListViewUIData(TsuperUserProfileModel t) {
		 Message.obtain(mHandler, 1, t).sendToTarget();	 
	 }
  
}
package com.smsgt.mobile.application.supertsuper_v3.activities;
 
import java.util.List;

import com.smsgt.mobile.application.supertsuper_v3.R;
import com.smsgt.mobile.application.supertsuper_v3.custom_ui.AchievementsAdapter;
import com.smsgt.mobile.application.supertsuper_v3.database.model.AchievementsModel;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;  
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
  
public class UserAchievementsActivity extends Activity {  
    
  private ListView AchievementsView ;  
  private AchievementsAdapter achievementsAdapter;
  private List<AchievementsModel> achievementsList;
  Button AchievementsBack; 
 
  @SuppressWarnings("unchecked")
  @Override  
  public void onCreate(Bundle savedInstanceState) {  
	  
    super.onCreate(savedInstanceState); 
    if (Build.VERSION.SDK_INT < 16) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    } 
    setContentView(R.layout.activity_user_achievements);  
    
    AchievementsView = (ListView) findViewById( R.id.AchievementsView );
    AchievementsBack = (Button)findViewById(R.id.AchievementsBack);

    achievementsList = (List<AchievementsModel>) getIntent().getSerializableExtra("achievementsList");
    Log.e("", "list: " + achievementsList + ", size: " + achievementsList.size());
    
    if(achievementsList != null && !achievementsList.isEmpty()) {
    	 achievementsAdapter = new AchievementsAdapter(achievementsList, getApplicationContext()); 
    	    AchievementsView.setAdapter(achievementsAdapter);
    } else {
    	String[] noAchievements = new String[] { "You currently have no achievements" };
    	AchievementsView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, noAchievements));   	
    }
    
    AchievementsBack.setOnClickListener(new OnClickListener() {
		
		  @Override
		  public void onClick(View v) {
			  //setResult(RESULT_OK);
			  UserAchievementsActivity.this.onBackPressed();  
	    }
	});
  }
}
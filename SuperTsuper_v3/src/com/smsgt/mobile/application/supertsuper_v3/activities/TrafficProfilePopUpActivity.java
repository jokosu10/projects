package com.smsgt.mobile.application.supertsuper_v3.activities;

import java.util.ArrayList;
import java.util.List;

import com.smsgt.mobile.application.supertsuper_v3.R;
import com.smsgt.mobile.application.supertsuper_v3.custom_ui.TrafficProfilePagerAdapter;
//import com.smsgt.mobile.application.supertsuper_v3.custom_ui.TrafficProfile_12pm_Fragment;
//import com.smsgt.mobile.application.supertsuper_v3.custom_ui.TrafficProfile_3pm_Fragment;
//import com.smsgt.mobile.application.supertsuper_v3.custom_ui.TrafficProfile_6pm_Fragment;
//import com.smsgt.mobile.application.supertsuper_v3.custom_ui.TrafficProfile_9am_Fragment;
//import com.smsgt.mobile.application.supertsuper_v3.custom_ui.TrafficProfile_9pm_Fragment;
import com.viewpagerindicator.CirclePageIndicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class TrafficProfilePopUpActivity extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.gravity = Gravity.NO_GRAVITY;  
		params.x = 0; params.y = -200;
		getWindow().setAttributes(params);
		setContentView(R.layout.traffic_profile_viewpager_prompt_layout);

		TrafficProfilePagerAdapter pageAdapter = new TrafficProfilePagerAdapter(getSupportFragmentManager(), getFragments());
		ViewPager pager = (ViewPager)findViewById(R.id.myViewPager);
		pager.setAdapter(pageAdapter);
		
		CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.viewPagerIndicator);
		circlePageIndicator.setViewPager(pager,0);
	}

	private List<Fragment> getFragments(){
    	List<Fragment> fList = new ArrayList<Fragment>();
/*		fList.add(new TrafficProfile_9am_Fragment());
		fList.add(new TrafficProfile_12pm_Fragment());
		fList.add(new TrafficProfile_3pm_Fragment());
		fList.add(new TrafficProfile_6pm_Fragment());
		fList.add(new TrafficProfile_9pm_Fragment());*/
    	return fList;
    }
}





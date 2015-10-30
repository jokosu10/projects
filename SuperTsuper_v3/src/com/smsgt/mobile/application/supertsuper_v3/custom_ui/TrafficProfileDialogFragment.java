package com.smsgt.mobile.application.supertsuper_v3.custom_ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.smsgt.mobile.application.supertsuper_v3.R;
import com.smsgt.mobile.application.supertsuper_v3.asynctask_classes.AsyncTaskStrongloopCommonHttpCall;
import com.smsgt.mobile.application.supertsuper_v3.database.model.TsuperTrafficProfileStatsModels;
import com.viewpagerindicator.CirclePageIndicator;

import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class TrafficProfileDialogFragment extends android.support.v4.app.DialogFragment {
	
	private String trafficProfilePolygonTag;
	private String jsonResponse;
	
	public TrafficProfileDialogFragment(String trafficProfilePolygonTag) {
		this.trafficProfilePolygonTag = trafficProfilePolygonTag;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	 public Dialog onCreateDialog(Bundle savedInstanceState) {
		final View trafficProfileDialogFragment = this.onCreateView(getActivity().getLayoutInflater(), null, savedInstanceState);
		final Dialog dialog = new Dialog(getActivity(), R.style.DialogCustomTheme);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();  
		lp.dimAmount=0.90f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
		lp.y = 75;
		lp.height = 300;
		lp.width = 300;
		dialog.getWindow().setAttributes(lp);
		dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);
		dialog.getWindow().setBackgroundDrawable(new BitmapDrawable());		
		dialog.setContentView(trafficProfileDialogFragment);
		dialog.setCanceledOnTouchOutside(true);
	    return dialog;
	 }	
	 
	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);  
	    }
	 
	 @Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			   
		 	try {
				jsonResponse = new AsyncTaskStrongloopCommonHttpCall().execute("highway_stats/findOne?filter[where][tag]=" + Uri.encode(trafficProfilePolygonTag), "GET").get();
		 	} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} 
		 	
			final View trafficProfileDialogFragment = inflater.inflate(R.layout.traffic_profile_viewpager_prompt_layout, container, false);
			TrafficProfilePagerAdapter pageAdapter = new TrafficProfilePagerAdapter(getChildFragmentManager(), getFragments(jsonResponse));
			ViewPager pager = (ViewPager) trafficProfileDialogFragment.findViewById(R.id.myViewPager);
			pager.setAdapter(pageAdapter);
			
			CirclePageIndicator circlePageIndicator = (CirclePageIndicator) trafficProfileDialogFragment.findViewById(R.id.viewPagerIndicator);
			circlePageIndicator.setViewPager(pager,0);
			return trafficProfileDialogFragment;
	 }	
	 
	 
	 private List<Fragment> getFragments(String json){
		
		 	ArrayList<TsuperTrafficProfileStatsModels> trafficProfileData_9am = new ArrayList<TsuperTrafficProfileStatsModels>();
		 	ArrayList<TsuperTrafficProfileStatsModels> trafficProfileData_12pm = new ArrayList<TsuperTrafficProfileStatsModels>();
		 	ArrayList<TsuperTrafficProfileStatsModels> trafficProfileData_3pm = new ArrayList<TsuperTrafficProfileStatsModels>();
		 	ArrayList<TsuperTrafficProfileStatsModels> trafficProfileData_6pm = new ArrayList<TsuperTrafficProfileStatsModels>();
		 	ArrayList<TsuperTrafficProfileStatsModels> trafficProfileData_9pm = new ArrayList<TsuperTrafficProfileStatsModels>();
		 	
		 	SimpleDateFormat sdf = new SimpleDateFormat("F,MM/dd/yyyy HH:mm");
		 	sdf.setTimeZone(TimeZone.getTimeZone("GMT+08"));
		 	
		// 	json = "{\"tag\":\"Rizal Avenue_7\",\"daily\": [{\"timestamp\": 1406093357,\"avespeed\": 45}]}";
		 
			if(!json.trim().isEmpty()) {
				try {
					JSONObject jObj = new JSONObject(json);
					JSONArray jArr = jObj.getJSONArray("daily");
					for(int ind = 0; ind < jArr.length(); ind++) {
						
						TsuperTrafficProfileStatsModels stats = new TsuperTrafficProfileStatsModels();
						String jString = jArr.getString(ind);
						JSONObject jObj2 = new JSONObject(jString);
						String timestamp = jObj2.getString("timestamp");
						String avespeed = jObj2.getString("avespeed");
						
						String formattedTimeTemp = sdf.format((Long.parseLong(timestamp)*1000)), formattedTime = "";
						formattedTime = formattedTimeTemp.substring(formattedTimeTemp.indexOf("/") + 8,formattedTimeTemp.indexOf(":")).trim();
						int dayOfWeek = Integer.parseInt(formattedTimeTemp.substring(0, formattedTimeTemp.indexOf(",")));		
						
						stats.setAverageValue(avespeed);
						stats.setDayOfWeek(dayOfWeek);
						
						if(Integer.parseInt(formattedTime) <= 9 && Integer.parseInt(formattedTime) < 12) { // 1am - 11:59am
							trafficProfileData_9am.add(stats);
						} else if(Integer.parseInt(formattedTime) >= 12 && Integer.parseInt(formattedTime) < 15) { // 12pm-2:59pm
							trafficProfileData_12pm.add(stats);
						} else if(Integer.parseInt(formattedTime) >= 15 && Integer.parseInt(formattedTime) < 18) { // 3pm-5:59pm
							trafficProfileData_3pm.add(stats);
						} else if(Integer.parseInt(formattedTime) >= 18 && Integer.parseInt(formattedTime) < 21) { // 6pm-8:59pm
							trafficProfileData_6pm.add(stats);
						} else if(Integer.parseInt(formattedTime) >= 21 && Integer.parseInt(formattedTime) < 24) { // 9pm onwards until midnight
							trafficProfileData_9pm.add(stats);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}	
			}
			
	    	List<Fragment> fList = new ArrayList<Fragment>();
			fList.add(new TrafficProfile_9am_Fragment(trafficProfileData_9am));
			fList.add(new TrafficProfile_12pm_Fragment(trafficProfileData_12pm));
			fList.add(new TrafficProfile_3pm_Fragment(trafficProfileData_3pm));
			fList.add(new TrafficProfile_6pm_Fragment(trafficProfileData_6pm));
			fList.add(new TrafficProfile_9pm_Fragment(trafficProfileData_9pm));	 
			return fList;
    }	 
}

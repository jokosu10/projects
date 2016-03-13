package com.smsgt.mobile.application.supertsuper_v3.custom_ui;

import java.util.ArrayList;

import com.smsgt.mobile.application.supertsuper_v3.R;
import com.smsgt.mobile.application.supertsuper_v3.database.model.TsuperTrafficProfileStatsModels;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TrafficProfile_9am_Fragment extends Fragment {
	
	private ArrayList<TsuperTrafficProfileStatsModels> trafficData = new ArrayList<TsuperTrafficProfileStatsModels>();
	
	public TrafficProfile_9am_Fragment(ArrayList<TsuperTrafficProfileStatsModels> data) {
		this.trafficData = data;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.traffic_profile_9am, container, false);
		
		if(this.trafficData.size() > 0 && !this.trafficData.isEmpty()) {
			for(TsuperTrafficProfileStatsModels s : this.trafficData){
				
				switch(s.getDayOfWeek()) {
				case 0: RelativeLayout layOutSun = (RelativeLayout) view.findViewById(R.id.traffic_profile_9am_Sunday_layout);
						TextView tvSun = (TextView) view.findViewById(R.id.traffic_profile_9am_Sunday_image_textView_number);
						tvSun.setText(s.getAverageValue());
						setBackgroundImage(layOutSun, tvSun.getText().toString());
						break;
				case 1: RelativeLayout layOutMon = (RelativeLayout) view.findViewById(R.id.traffic_profile_9am_Monday_layout);
						TextView tvMon = (TextView) view.findViewById(R.id.traffic_profile_9am_Monday_image_textView_number);
						tvMon.setText(s.getAverageValue());
						setBackgroundImage(layOutMon, tvMon.getText().toString());
						break;
				case 2: RelativeLayout layOutTue = (RelativeLayout) view.findViewById(R.id.traffic_profile_9am_Tuesday_layout);
						TextView tvTue = (TextView) view.findViewById(R.id.traffic_profile_9am_Tuesday_image_textView_number);
						tvTue.setText(s.getAverageValue());
						setBackgroundImage(layOutTue, tvTue.getText().toString());
						break;
				case 3: RelativeLayout layOutWed = (RelativeLayout) view.findViewById(R.id.traffic_profile_9am_Wednesday_layout);
						TextView tvWed = (TextView) view.findViewById(R.id.traffic_profile_9am_Wednesday_image_textView_number);
						tvWed.setText(s.getAverageValue());
						setBackgroundImage(layOutWed, tvWed.getText().toString());
						break;
				case 4: RelativeLayout layOutThurs = (RelativeLayout) view.findViewById(R.id.traffic_profile_9am_Thursday_layout);
						TextView tvThurs = (TextView) view.findViewById(R.id.traffic_profile_9am_Thursday_image_textView_number);
						tvThurs.setText(s.getAverageValue());
						setBackgroundImage(layOutThurs, tvThurs.getText().toString());
						break;
				case 5: RelativeLayout layOutFri = (RelativeLayout) view.findViewById(R.id.traffic_profile_9am_Friday_layout);
						TextView tvFri = (TextView) view.findViewById(R.id.traffic_profile_9am_Friday_image_textView_number);
						tvFri.setText(s.getAverageValue());
						setBackgroundImage(layOutFri, tvFri.getText().toString());
						break;
				case 6: RelativeLayout layOutSat = (RelativeLayout) view.findViewById(R.id.traffic_profile_9am_Saturday_layout);
						TextView tvSat = (TextView) view.findViewById(R.id.traffic_profile_9am_Saturday_image_textView_number);
						tvSat.setText(s.getAverageValue());
						setBackgroundImage(layOutSat, tvSat.getText().toString());
						break;
				}
			}
		} 
		
		return view;
	}
	
	@SuppressWarnings("deprecation")
	public void setBackgroundImage(View v, String profileNumber) {
		if(Integer.parseInt(profileNumber) >= 0 && Integer.parseInt(profileNumber) < 35) {
			v.setBackgroundDrawable(getResources().getDrawable(R.drawable.re));
			//v.setBackground(getResources().getDrawable(R.drawable.re));
		} else if (Integer.parseInt(profileNumber) >= 35 && Integer.parseInt(profileNumber) < 50) {
			v.setBackgroundDrawable(getResources().getDrawable(R.drawable.yel));
		} else if (Integer.parseInt(profileNumber) >= 50) {
			v.setBackgroundDrawable(getResources().getDrawable(R.drawable.gr));
		}
	}
}

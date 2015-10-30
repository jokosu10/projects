package com.smsgt.mobile.application.supertsuper_v3.custom_interfaces.impl;

import java.util.ArrayList;
import com.smsgt.mobile.application.supertsuper_v3.database.model.TsuperTrafficProfileModel;

public class TrafficProfileMockDataSource {

	private ArrayList<TsuperTrafficProfileModel> trafficProfileDummyList = new ArrayList<TsuperTrafficProfileModel>();
	
	public TrafficProfileMockDataSource() {
		
		TsuperTrafficProfileModel dtp1 = new TsuperTrafficProfileModel();
		dtp1.setTrafficProfileHistorical("33");
		dtp1.setLat("14.619793");
		dtp1.setLon("121.051018");
		//dtp1.getGeopoint();
		
		TsuperTrafficProfileModel dtp2 = new TsuperTrafficProfileModel();
		dtp2.setTrafficProfileHistorical("35");
		dtp2.setLat("14.619800");
		dtp2.setLon("121.0600");
		//dtp2.getGeopoint();
		
		TsuperTrafficProfileModel dtp3 = new TsuperTrafficProfileModel();
		dtp3.setTrafficProfileHistorical("40");
		dtp3.setLat("14.617800");
		dtp3.setLon("121.0500");
		//dtp3.getGeopoint();
		
		TsuperTrafficProfileModel dtp4 = new TsuperTrafficProfileModel();
		dtp4.setTrafficProfileHistorical("40");
		dtp4.setLat("14.517800");
		dtp4.setLon("121.0500");
		//dtp4.getGeopoint();
		
		TsuperTrafficProfileModel dtp5 = new TsuperTrafficProfileModel();
		dtp5.setTrafficProfileHistorical("30");
		dtp5.setLat("14.517200");
		dtp5.setLon("121.0400");
		//dtp5.getGeopoint();
		
		TsuperTrafficProfileModel dtp6 = new TsuperTrafficProfileModel();
		dtp6.setTrafficProfileHistorical("35");
		dtp6.setLat("14.657418");
		dtp6.setLon("121.003766");
		//dtp6.getGeopoint();
		
		TsuperTrafficProfileModel dtp7 = new TsuperTrafficProfileModel();
		dtp7.setTrafficProfileHistorical("40");
		dtp7.setLat("14.584549");
		dtp7.setLon("121.056853");
		//dtp7.getGeopoint();
		
		TsuperTrafficProfileModel dtp8 = new TsuperTrafficProfileModel();
		dtp8.setTrafficProfileHistorical("45");
		dtp8.setLat("14.693119");
		dtp8.setLon("121.028071");
		//dtp8.getGeopoint();
		
		TsuperTrafficProfileModel dtp9 = new TsuperTrafficProfileModel();
		dtp9.setTrafficProfileHistorical("50");
		dtp9.setLat("14.591892");
		dtp9.setLon("121.058769");
		//dtp9.getGeopoint();
		
		TsuperTrafficProfileModel dtp10 = new TsuperTrafficProfileModel();
		dtp10.setTrafficProfileHistorical("55");
		dtp10.setLat("14.555277");
		dtp10.setLon("121.034543");
		//dtp10.getGeopoint();
		
		TsuperTrafficProfileModel dtp11 = new TsuperTrafficProfileModel();
		dtp11.setTrafficProfileHistorical("60");
		dtp11.setLat("14.54923");
		dtp11.setLon("121.028192");
		//dtp11.getGeopoint();
		
		TsuperTrafficProfileModel dtp12 = new TsuperTrafficProfileModel();
		dtp12.setTrafficProfileHistorical("65");
		dtp12.setLat("14.540758");
		dtp12.setLon("121.016966");
		//dtp12.getGeopoint();
		
		
		trafficProfileDummyList.add(dtp1);
		trafficProfileDummyList.add(dtp2);
		trafficProfileDummyList.add(dtp3);
		trafficProfileDummyList.add(dtp4);
		trafficProfileDummyList.add(dtp5);
		trafficProfileDummyList.add(dtp6);
		trafficProfileDummyList.add(dtp7);
		trafficProfileDummyList.add(dtp8);
		trafficProfileDummyList.add(dtp9);
		trafficProfileDummyList.add(dtp10);	
		trafficProfileDummyList.add(dtp11);
		trafficProfileDummyList.add(dtp12);			
	}
	
	public ArrayList<TsuperTrafficProfileModel> getTrafficProfiles() {
		return trafficProfileDummyList;
	}
	
}

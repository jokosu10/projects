package com.smsgt.mobile.application.supertsuper_v3.asynctask_classes;

import java.util.TimerTask;

public class TrafficProfileTimer extends TimerTask {

	private GetAllTrafficProfilesFromStrongloop getAllTrafficProfilesFromStrongloop;
	
	public TrafficProfileTimer(GetAllTrafficProfilesFromStrongloop g) {
		this.getAllTrafficProfilesFromStrongloop = g;
	}
	
	@Override
	public void run() {
		this.getAllTrafficProfilesFromStrongloop.execute();
	}
}

package com.smsgt.mobile.application.supertsuper_v3.database.model;

import java.io.Serializable;

public class TsuperTrafficProfileStatsModels implements Serializable {

	private static final long serialVersionUID = 1L;
	private int dayOfWeek;
	private String averageValue;
	
	public int getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	public String getAverageValue() {
		return averageValue;
	}
	public void setAverageValue(String averageValue) {
		this.averageValue = averageValue;
	}
	

}

package com.smsgt.mobile.application.supertsuper_v3.database.model;

import java.io.Serializable;

public class TsuperTrafficProfileModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private String trafficProfilePolygonTag;
	private String trafficProfileCurrent;
	private String trafficProfileHistorical;
	private String lat;
	private String lon;
	
	public String getTrafficProfilePolygonTag() {
		return trafficProfilePolygonTag;
	}
	public void setTrafficProfilePolygonTag(String trafficProfilePolygonTag) {
		this.trafficProfilePolygonTag = trafficProfilePolygonTag;
	}
	public String getTrafficProfileCurrent() {
		return trafficProfileCurrent;
	}
	public void setTrafficProfileCurrent(String trafficProfileCurrent) {
		this.trafficProfileCurrent = trafficProfileCurrent;
	}
	public String getTrafficProfileHistorical() {
		return trafficProfileHistorical;
	}
	public void setTrafficProfileHistorical(String trafficProfileHistorical) {
		this.trafficProfileHistorical = trafficProfileHistorical;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}	
}

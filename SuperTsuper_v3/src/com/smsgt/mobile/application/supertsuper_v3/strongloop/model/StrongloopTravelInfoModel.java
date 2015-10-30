package com.smsgt.mobile.application.supertsuper_v3.strongloop.model;

import com.strongloop.android.loopback.Model;

public class StrongloopTravelInfoModel extends Model {
	
	private String userId;
	private String longitude;
	private String latitude;
	private String speed;
	private String totalDistance;
	private String bearing;
	private String type;
	private String name;
	private String description;
	private String timestamp;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	public String getTotalDistance() {
		return totalDistance;
	}
	public void setTotalDistance(String totalDistance) {
		this.totalDistance = totalDistance;
	}
	public String getBearing() {
		return bearing;
	}
	public void setBearing(String bearing) {
		this.bearing = bearing;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}

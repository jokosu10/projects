package com.smsgt.mobile.application.supertsuper_v3.strongloop.model;

import com.strongloop.android.loopback.Model;

public class StrongloopRegistrationModel extends Model {

	private String userId;
	private String userName;
	private String password;
	private String firstName;
	private String lastName;
	private String contactNumber;
	private String city;
	private String totalPoints;
	private String averageMovingSpeed;
	private String travelBearing;
	private String travelLongitude;
	private String travelLatitude;
	private String prevDistance;
	private String currDistance;
	private String totalDistance;
	private String membershipDate;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getTotalPoints() {
		return totalPoints;
	}
	public void setTotalPoints(String totalPoints) {
		this.totalPoints = totalPoints;
	}
	public String getPrevDistance() {
		return prevDistance;
	}
	public void setPrevDistance(String prevDistance) {
		this.prevDistance = prevDistance;
	}
	public String getCurrDistance() {
		return currDistance;
	}
	public void setCurrDistance(String currDistance) {
		this.currDistance = currDistance;
	}
	public String getTotalDistance() {
		return totalDistance;
	}
	public void setTotalDistance(String totalDistance) {
		this.totalDistance = totalDistance;
	}
	public String getAverageMovingSpeed() {
		return averageMovingSpeed;
	}
	public void setAverageMovingSpeed(String averageMovingSpeed) {
		this.averageMovingSpeed = averageMovingSpeed;
	}
	public String getTravelBearing() {
		return travelBearing;
	}
	public void setTravelBearing(String travelBearing) {
		this.travelBearing = travelBearing;
	}
	public String getTravelLongitude() {
		return travelLongitude;
	}
	public void setTravelLongitude(String travelLongitude) {
		this.travelLongitude = travelLongitude;
	}
	public String getTravelLatitude() {
		return travelLatitude;
	}
	public void setTravelLatitude(String travelLatitude) {
		this.travelLatitude = travelLatitude;
	}
	public String getMembershipDate() {
		return membershipDate;
	}
	public void setMembershipDate(String membershipDate) {
		this.membershipDate = membershipDate;
	}	
}

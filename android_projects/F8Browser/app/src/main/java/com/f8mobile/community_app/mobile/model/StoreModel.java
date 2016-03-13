package com.f8mobile.community_app.mobile.model;

import java.io.Serializable;

public class StoreModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String taxiUserId;
	private String taxiName;
	private String plateNumber;
	private String driverName;
	private String profilePicture;
	private String longitude;
	private String latitude;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTaxiName() {
		return taxiName;
	}
	public void setTaxiName(String taxiName) {
		this.taxiName = taxiName;
	}
	public String getPlateNumber() {
		return plateNumber;
	}
	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getProfilePicture() {
		return profilePicture;
	}
	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
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
	public String getTaxiUserId() {
		return taxiUserId;
	}
	public void setTaxiUserId(String taxiUserId) {
		this.taxiUserId = taxiUserId;
	}

}

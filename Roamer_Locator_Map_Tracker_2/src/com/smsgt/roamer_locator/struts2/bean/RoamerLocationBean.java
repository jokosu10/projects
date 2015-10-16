package com.smsgt.roamer_locator.struts2.bean;

import java.io.Serializable;

public class RoamerLocationBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String timestamp;
	private String position;
	private String cellId;
	private String cellName;
	private String cellLac;
	private String latitude;
	private String longitude;
	private String town;
	private String barangay;
	private String site_address;
	
	public String getCellId() {
		return cellId;
	}
	public void setCellId(String cellId) {
		this.cellId = cellId;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getCellName() {
		return cellName;
	}
	public void setCellName(String cellName) {
		this.cellName = cellName;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getCellLac() {
		return cellLac;
	}
	public void setCellLac(String cellLac) {
		this.cellLac = cellLac;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getTown() {
		return town;
	}
	public void setTown(String town) {
		this.town = town;
	}
	public String getBarangay() {
		return barangay;
	}
	public void setBarangay(String barangay) {
		this.barangay = barangay;
	}
	public String getSite_address() {
		return site_address;
	}
	public void setSite_address(String site_address) {
		this.site_address = site_address;
	}

}

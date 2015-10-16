package com.smsgt.roamer_locator.struts2.bean;

import java.io.Serializable;

public class RoamerLocatorUser implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String userName;
	private String hashedPassword;
	private String userType;
	private String adminType;
	private String createdBy;
	private String updatedBy;
	private String origPassword;
	private String name;
	private String dateTimeCreated;
	private String dateTimeUpdated;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getHashedPassword() {
		return hashedPassword;
	}
	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getAdminType() {
		return adminType;
	}
	public void setAdminType(String adminType) {
		this.adminType = adminType;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getOrigPassword() {
		return origPassword;
	}
	public void setOrigPassword(String origPassword) {
		this.origPassword = origPassword;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDateTimeCreated() {
		return dateTimeCreated;
	}
	public void setDateTimeCreated(String dateTimeCreated) {
		this.dateTimeCreated = dateTimeCreated;
	}
	public String getDateTimeUpdated() {
		return dateTimeUpdated;
	}
	public void setDateTimeUpdated(String dateTimeUpdated) {
		this.dateTimeUpdated = dateTimeUpdated;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	
	
}

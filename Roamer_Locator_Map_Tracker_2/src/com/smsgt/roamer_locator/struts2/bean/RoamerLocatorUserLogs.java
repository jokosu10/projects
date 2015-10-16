package com.smsgt.roamer_locator.struts2.bean;

import java.io.Serializable;

public class RoamerLocatorUserLogs implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String userId;
	private String name;
	private String action;
	private String accessedPage;
	private String dateTime;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getAccessedPage() {
		return accessedPage;
	}
	public void setAccessedPage(String accessedPage) {
		this.accessedPage = accessedPage;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	
	
}

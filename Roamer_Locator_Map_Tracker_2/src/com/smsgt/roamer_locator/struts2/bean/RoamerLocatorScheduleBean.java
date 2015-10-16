package com.smsgt.roamer_locator.struts2.bean;

import java.io.Serializable;

public class RoamerLocatorScheduleBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String date;
	private String time;
	private String status;
	private String date_executed;
	private String date_created;
	private String time_created;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDate_executed() {
		return date_executed;
	}
	public void setDate_executed(String date_executed) {
		this.date_executed = date_executed;
	}
	public String getDate_created() {
		return date_created;
	}
	public void setDate_created(String date_created) {
		this.date_created = date_created;
	}
	public String getTime_created() {
		return time_created;
	}
	public void setTime_created(String time_created) {
		this.time_created = time_created;
	}
}

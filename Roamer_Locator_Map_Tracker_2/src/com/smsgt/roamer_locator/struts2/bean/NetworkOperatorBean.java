package com.smsgt.roamer_locator.struts2.bean;

import java.io.Serializable;

public class NetworkOperatorBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String tadicCode;
	private String mncMcc;
	private String destination;
	private String networkOpName;
	
	public String getTadicCode() {
		return tadicCode;
	}
	public void setTadicCode(String tadicCode) {
		this.tadicCode = tadicCode;
	}
	public String getMncMcc() {
		return mncMcc;
	}
	public void setMncMcc(String mncMcc) {
		this.mncMcc = mncMcc;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getNetworkOpName() {
		return networkOpName;
	}
	public void setNetworkOpName(String networkOpName) {
		this.networkOpName = networkOpName;
	}
}

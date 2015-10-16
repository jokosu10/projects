package com.smsgt.roamer_locator.struts2.bean;

import java.io.Serializable;

public class ImsiMsisdnForAdhocBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String imsi;
	private String msisdn;
	
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
}

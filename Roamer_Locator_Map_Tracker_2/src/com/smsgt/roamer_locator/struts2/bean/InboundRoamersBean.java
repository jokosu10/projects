package com.smsgt.roamer_locator.struts2.bean;

import java.io.Serializable;

public class InboundRoamersBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String inboundRoamersLocation;
	private String inboundRoamersTotalCount;
	private String operatorName;
	private String operatorCountry;
	
	public String getInboundRoamersLocation() {
		return inboundRoamersLocation;
	}
	public void setInboundRoamersLocation(String inboundRoamersLocation) {
		this.inboundRoamersLocation = inboundRoamersLocation;
	}
	public String getInboundRoamersTotalCount() {
		return inboundRoamersTotalCount;
	}
	public void setInboundRoamersTotalCount(String inboundRoamersTotalCount) {
		this.inboundRoamersTotalCount = inboundRoamersTotalCount;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getOperatorCountry() {
		return operatorCountry;
	}
	public void setOperatorCountry(String operatorCountry) {
		this.operatorCountry = operatorCountry;
	}

}

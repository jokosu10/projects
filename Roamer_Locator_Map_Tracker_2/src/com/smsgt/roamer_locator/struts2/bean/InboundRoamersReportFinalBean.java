package com.smsgt.roamer_locator.struts2.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class InboundRoamersReportFinalBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String inboundRoamersLocation;
	private String inboundRoamersTotalCount;
	private String inboundRoamersCountPerOperator;
	private ArrayList<InboundRoamersReportBean> inboundRoamersBeanList;
	
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
	public ArrayList<InboundRoamersReportBean> getInboundRoamersBeanList() {
		return inboundRoamersBeanList;
	}
	public void setInboundRoamersBeanList(
			ArrayList<InboundRoamersReportBean> inboundRoamersBeanList) {
		this.inboundRoamersBeanList = inboundRoamersBeanList;
	}
	public String getInboundRoamersCountPerOperator() {
		return inboundRoamersCountPerOperator;
	}
	public void setInboundRoamersCountPerOperator(
			String inboundRoamersCountPerOperator) {
		this.inboundRoamersCountPerOperator = inboundRoamersCountPerOperator;
	}

}

package com.smsgt.roamer_locator.struts2.controller;

import java.util.ArrayList;

import com.opensymphony.xwork2.ActionSupport;
import com.smsgt.roamer_locator.struts2.bean.InboundRoamersBean;
import com.smsgt.roamer_locator.struts2.database.RoamerLocatorDatabaseAccess;
import com.smsgt.roamer_locator.struts2.util.JSONConverterUtility;

public class DetermineInboundRoamerLocationAction extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private String location;
	private String jsonResponse;
	private String column;
	private ArrayList<InboundRoamersBean> inboundRoamersBeanList;
	
	public String execute() {
		
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		inboundRoamersBeanList = rDBAccess.getInboundRoamersInformation(RoamerMapTrackerUtilitiesAction.fixEncoding(location), column);
		jsonResponse = JSONConverterUtility.convertToJSONFormat(inboundRoamersBeanList);
		return ActionSupport.SUCCESS;
	}
	
	public String execute2() {
		
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		inboundRoamersBeanList = rDBAccess.getInboundRoamersInformationLocal(RoamerMapTrackerUtilitiesAction.fixEncoding(location), column);
		jsonResponse = JSONConverterUtility.convertToJSONFormat(inboundRoamersBeanList);
		return ActionSupport.SUCCESS;
	}
	
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getJsonResponse() {
		return jsonResponse;
	}

	public void setJsonResponse(String jsonResponse) {
		this.jsonResponse = jsonResponse;
	}

	public ArrayList<InboundRoamersBean> getInboundRoamersBeanList() {
		return inboundRoamersBeanList;
	}

	public void setInboundRoamersBeanList(
			ArrayList<InboundRoamersBean> inboundRoamersBeanList) {
		this.inboundRoamersBeanList = inboundRoamersBeanList;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

}

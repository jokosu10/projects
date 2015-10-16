package com.smsgt.roamer_locator.struts2.controller;

import java.util.ArrayList;

import com.opensymphony.xwork2.ActionSupport;
import com.smsgt.roamer_locator.struts2.bean.NetworkOperatorBean;
import com.smsgt.roamer_locator.struts2.database.RoamerLocatorDatabaseAccess;
import com.smsgt.roamer_locator.struts2.util.JSONConverterUtility;

public class GetAllOperatorsFromDatabaseAction extends ActionSupport {

	private static final long serialVersionUID = 1L;
	
	private String jsonResponse;
	private ArrayList<NetworkOperatorBean> networkOperatorArrayList;
	private String mode;
	
	public String execute() {
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		networkOperatorArrayList = rDBAccess.returnAllOperator(mode);
		jsonResponse = JSONConverterUtility.convertToJSONFormat(networkOperatorArrayList);
		return ActionSupport.SUCCESS;
	}

	public String getJsonResponse() {
		return jsonResponse;
	}

	public void setJsonResponse(String jsonResponse) {
		this.jsonResponse = jsonResponse;
	}

	public ArrayList<NetworkOperatorBean> getNetworkOperatorArrayList() {
		return networkOperatorArrayList;
	}

	public void setNetworkOperatorArrayList(
			ArrayList<NetworkOperatorBean> networkOperatorArrayList) {
		this.networkOperatorArrayList = networkOperatorArrayList;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}	
	
}

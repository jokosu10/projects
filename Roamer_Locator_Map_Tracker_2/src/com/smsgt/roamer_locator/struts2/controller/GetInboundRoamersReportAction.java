package com.smsgt.roamer_locator.struts2.controller;

import java.util.ArrayList;
import com.opensymphony.xwork2.ActionSupport;
import com.smsgt.roamer_locator.struts2.bean.InboundRoamersReportFinalBean;
import com.smsgt.roamer_locator.struts2.database.RoamerLocatorDatabaseAccess;
import com.smsgt.roamer_locator.struts2.util.JSONConverterUtility;

public class GetInboundRoamersReportAction extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private String jsonResponse;
	private String operator;
	private String imsi;
	private String dateFrom;
	private String dateTo;
	private String location;
	private String isProvincialSelected;
	private String isTouristDestination;
	private ArrayList<InboundRoamersReportFinalBean> inboundRoamersReportArrayList;
	
	public String generateReportsBaseOnNetwork() {
		
		boolean isNetwork = true;
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		//operator = operator.replaceAll(",", "").trim();
		
		if(Boolean.valueOf(isTouristDestination).booleanValue()) {
			inboundRoamersReportArrayList = rDBAccess.getInboundRoamersReportForTouristDestination2(location, rDBAccess.returnProvince(location), operator, isNetwork, dateFrom, dateTo);
		} else {
			inboundRoamersReportArrayList = rDBAccess.getInboundRoamersReport2(operator, isNetwork, dateFrom, dateTo, location, Boolean.valueOf(isProvincialSelected).booleanValue());
		}
		
		jsonResponse = JSONConverterUtility.convertToJSONFormat(inboundRoamersReportArrayList);
		return ActionSupport.SUCCESS;
	}
	
	public String generateReportsBaseOnIMSI() {
		boolean isNetwork = false;
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		
		if(Boolean.valueOf(isTouristDestination).booleanValue()) {
			inboundRoamersReportArrayList = rDBAccess.getInboundRoamersReportForTouristDestination2(location, rDBAccess.returnProvince(location) , imsi, isNetwork, dateFrom, dateTo);
		} else {
			inboundRoamersReportArrayList = rDBAccess.getInboundRoamersReport2(imsi, isNetwork, dateFrom, dateTo, location, Boolean.valueOf(isProvincialSelected).booleanValue());
		}		
		
		jsonResponse = JSONConverterUtility.convertToJSONFormat(inboundRoamersReportArrayList);
		return ActionSupport.SUCCESS;
	}

	public String generateReportsBaseOnNetwork2() {
		
		boolean isNetwork = true;
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		//operator = operator.replaceAll(",", "").trim();
		
		if(Boolean.valueOf(isTouristDestination).booleanValue()) {
			inboundRoamersReportArrayList = rDBAccess.getInboundRoamersReportForTouristDestinationLocal(location, rDBAccess.returnProvince(location), operator, isNetwork, dateFrom, dateTo);
		} else {
			inboundRoamersReportArrayList = rDBAccess.getInboundRoamersReportLocal(operator, isNetwork, dateFrom, dateTo, location, Boolean.valueOf(isProvincialSelected).booleanValue());
		}
		
		jsonResponse = JSONConverterUtility.convertToJSONFormat(inboundRoamersReportArrayList);
		return ActionSupport.SUCCESS;
	}
	
	public String generateReportsBaseOnIMSI2() {
		boolean isNetwork = false;
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		
		if(Boolean.valueOf(isTouristDestination).booleanValue()) {
			inboundRoamersReportArrayList = rDBAccess.getInboundRoamersReportForTouristDestinationLocal(location, rDBAccess.returnProvince(location), imsi, isNetwork, dateFrom, dateTo);
		} else {
			inboundRoamersReportArrayList = rDBAccess.getInboundRoamersReportLocal(imsi, isNetwork, dateFrom, dateTo, location, Boolean.valueOf(isProvincialSelected).booleanValue());
		}		
		
		jsonResponse = JSONConverterUtility.convertToJSONFormat(inboundRoamersReportArrayList);
		return ActionSupport.SUCCESS;
	}	
	
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getImsi() {
		return imsi;
	}

	public String getJsonResponse() {
		return jsonResponse;
	}

	public void setJsonResponse(String jsonResponse) {
		this.jsonResponse = jsonResponse;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public ArrayList<InboundRoamersReportFinalBean> getInboundRoamersReportArrayList() {
		return inboundRoamersReportArrayList;
	}

	public void setInboundRoamersReportArrayList(
			ArrayList<InboundRoamersReportFinalBean> inboundRoamersReportArrayList) {
		this.inboundRoamersReportArrayList = inboundRoamersReportArrayList;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getIsProvincialSelected() {
		return isProvincialSelected;
	}

	public void setIsProvincialSelected(String isProvincialSelected) {
		this.isProvincialSelected = isProvincialSelected;
	}

	public String getIsTouristDestination() {
		return isTouristDestination;
	}

	public void setIsTouristDestination(String isTouristDestination) {
		this.isTouristDestination = isTouristDestination;
	}
}

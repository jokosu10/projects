package com.smsgt.roamer_locator.struts2.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionSupport;
import com.smsgt.roamer_locator.struts2.bean.NetworkOperatorBean;
import com.smsgt.roamer_locator.struts2.bean.RoamerLocatorScheduleBean;
import com.smsgt.roamer_locator.struts2.database.RoamerLocatorDatabaseAccess;
import com.smsgt.roamer_locator.struts2.util.JSONConverterUtility;
import com.smsgt.roamer_locator.struts2.util.MapTrackerPropertiesReader;

public class CreateCampaignAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private String selectedNetworks;
	private String date;
	private String time;
	private String triggerType;
	private String jsonResponse;
	private String id;
	private String dateCreated;
	private String timeCreated;
	private String pauseType;
	private RoamerLocatorScheduleBean rSchedBean;
	
	private static final Logger logger = Logger.getLogger(CreateCampaignAction.class);
	private MapTrackerPropertiesReader mapTrackerPropertiesReader = new MapTrackerPropertiesReader("maptracker.properties");
	
	public String execute() {
		
		try { 
		
			String campaignFileDir = mapTrackerPropertiesReader.getValueFromProperty("campaignDir");	
			String campaignFileName = mapTrackerPropertiesReader.getValueFromProperty("campaignFileName");	
			
			File file = new File(campaignFileDir + campaignFileName);
			StringBuilder campaignBuilder = new StringBuilder();
			
			if(selectedNetworks.equalsIgnoreCase("all")) {
				RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
				ArrayList<NetworkOperatorBean> networkOperatorArrayList = rDBAccess.returnAllOperator("all");
				
				for(NetworkOperatorBean n: networkOperatorArrayList) {
					String fileInput = "NULL,NULL,NULL," + n.getMncMcc().substring(0, 3) + "," + n.getMncMcc().substring(3, n.getMncMcc().length()) + "\n";
					campaignBuilder.append(fileInput);
				}	
			} /*else {
				
				// create input file based on string
				String[] values = selectedNetworks.split(",", -1);
				
				for(int i = 0; i < values.length; i++) {
					String insertToCampaignMcc =  values[i].substring(0,values[i].indexOf("-"));
					String insertToCampaignMnc = values[i].substring(values[i].indexOf("-") + 1, values[i].length());
					String input = "NULL,NULL,NULL,";
					campaignBuilder.append(input + insertToCampaignMcc + "," + insertToCampaignMnc + "\n");
				}	
			}*/	
					
			FileUtils.writeStringToFile(file, campaignBuilder.toString());
			
			// insert schedule into database
			RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
			rDBAccess.insertCampaignSchedule(date, time, triggerType, id, dateCreated, timeCreated);
		
		} catch (IOException e) {
			logger.error("execute() IOException=>" + e.getMessage(), e);
		} 
		
		jsonResponse = ActionSupport.SUCCESS;
		return jsonResponse;
		
	}

	public String checkForExistingCampaign() {
		
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		rSchedBean = rDBAccess.checkForExistingCampaign();
		jsonResponse = ActionSupport.SUCCESS;
		return jsonResponse;
	}	
	
	public String pauseResumeCampaign() {
		
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		String retVal = rDBAccess.pauseResumeCampaign(pauseType);
		jsonResponse = JSONConverterUtility.convertToJSONFormat(retVal);
		return ActionSupport.SUCCESS;
	}
	
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public String getJsonResponse() {
		return jsonResponse;
	}

	public void setJsonResponse(String jsonResponse) {
		this.jsonResponse = jsonResponse;
	}

	public String getSelectedNetworks() {
		return selectedNetworks;
	}

	public void setSelectedNetworks(String selectedNetworks) {
		this.selectedNetworks = selectedNetworks;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public RoamerLocatorScheduleBean getrSchedBean() {
		return rSchedBean;
	}

	public void setrSchedBean(RoamerLocatorScheduleBean rSchedBean) {
		this.rSchedBean = rSchedBean;
	}

	public String getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(String timeCreated) {
		this.timeCreated = timeCreated;
	}

	public String getPauseType() {
		return pauseType;
	}

	public void setPauseType(String pauseType) {
		this.pauseType = pauseType;
	}
}

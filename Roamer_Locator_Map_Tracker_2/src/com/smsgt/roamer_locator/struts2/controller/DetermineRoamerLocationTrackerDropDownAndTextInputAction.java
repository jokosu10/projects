package com.smsgt.roamer_locator.struts2.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.opensymphony.xwork2.ActionSupport;
import com.smsgt.roamer_locator.struts2.bean.ImsiMsisdnForAdhocBean;
import com.smsgt.roamer_locator.struts2.database.RoamerLocatorDatabaseAccess;
import com.smsgt.roamer_locator.struts2.util.JSONConverterUtility;
import com.smsgt.roamer_locator.struts2.util.MapTrackerPropertiesReader;

public class DetermineRoamerLocationTrackerDropDownAndTextInputAction extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private String inputMode;
	private String stringInput;
	private String jsonResponse;
	private String sessionId;

	private static final Logger logger = Logger.getLogger(DetermineRoamerLocationTrackerDropDownAndTextInputAction.class);
	private MapTrackerPropertiesReader mapTrackerPropertiesReader = new MapTrackerPropertiesReader("maptracker.properties");
	private boolean hasNoImsi = false;
	
	public String execute() {
		
		if(!inputMode.trim().isEmpty()) {
			
			boolean sftpSuccess = false;
			StringBuilder sb = new StringBuilder();
			String adhocFileDir = mapTrackerPropertiesReader.getValueFromProperty("adhocDir");
			String adhocFileName = mapTrackerPropertiesReader.getValueFromProperty("adhocFileName");
			File file = new File(adhocFileDir + adhocFileName); // modify path and filename
			
			try {
				
				if(inputMode.trim().equalsIgnoreCase("dropDownInput")) {
					RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
					List<ImsiMsisdnForAdhocBean> imsiList = rDBAccess.returnAllImsi(stringInput);
					
					if(imsiList.size() > 0) {
						for(ImsiMsisdnForAdhocBean s : imsiList) {
							sb.append(s.getImsi()).append(",").append("NULL").append(",").append("NULL,NULL,NULL\n");
						}
						hasNoImsi = false;
					} else {
						hasNoImsi = true;
						// create an upload file with campaign-like format
						sb.append("NULL,NULL,NULL," + stringInput.substring(0, 3) + "," + stringInput.substring(3, stringInput.length()));
					}
				} else if(inputMode.trim().equalsIgnoreCase("textInput")) {
					String[] valFromSelect = stringInput.split("-", -1);
					for(int i = 0; i < valFromSelect.length; i++) {
						String[] valFromSelectParsed = valFromSelect[i].split(",",-1);
						String imsi = valFromSelectParsed[0];
						String msisdn = valFromSelectParsed[1];
						if(imsi.trim().isEmpty()) {
							imsi = "NULL";
						}
						if(msisdn.trim().isEmpty()) {
							msisdn = "NULL";
						}
						
						if(!imsi.equalsIgnoreCase("NULL")) {
							msisdn = "NULL";
						}
						
						sb.append(imsi).append(",").append(msisdn).append(",").append("NULL,NULL,NULL\n");
					}
					hasNoImsi = false;
				}
			
				/*if(hasNoImsi) {
					jsonResponse = JSONConverterUtility.convertToJSONFormat("No imsi found in database for the selected entry. Please try another one");
				} else {*/
					FileUtils.writeStringToFile(file, sb.toString()); 	
					sftpSuccess = pushFileToServer(file);
					//sftpSuccess = true;
					if(sftpSuccess) {
						// insert into database
						Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
						String line = "";
						BufferedReader bReader = new BufferedReader(new FileReader(file));
						while((line = bReader.readLine()) != null) {
							String[] parseLine = line.split(",", -1);
							String imsi = "", msisdn = "", vlr = "";
							
							if(hasNoImsi) {
								imsi = stringInput;
								msisdn = "NULL";
								vlr = "NULL";
							} else {
								imsi = parseLine[0];
								msisdn = parseLine[1];
								vlr = parseLine[2];
							}
							
							String timestamp = Long.toString((cal.getTimeInMillis()/1000));
							
							insertToAdHocTable(imsi, msisdn, vlr, timestamp);
						}
						bReader.close();
						jsonResponse = JSONConverterUtility.convertToJSONFormat("Ad-Hoc trigger is successful");
					} else {
						jsonResponse = JSONConverterUtility.convertToJSONFormat("Ad-Hoc trigger has failed due to SFTP problems!");
					}
				//}
			} catch (IOException e) {
				logger.error("execute() IOException => " + e.getMessage(), e);
			}	
		}
		return ActionSupport.SUCCESS;
	}
	
	public void insertToAdHocTable(String imsi, String msisdn, String vlr, String timestamp) {
		
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		rDBAccess.insertToAdhocTable(imsi, msisdn, vlr, timestamp);
		
	}

	public String execute2() {
		
		if(!inputMode.trim().isEmpty()) {
			
			boolean sftpSuccess = false;
			StringBuilder sb = new StringBuilder();
			String adhocFileDir = mapTrackerPropertiesReader.getValueFromProperty("adhocDir");
			String adhocFileName = mapTrackerPropertiesReader.getValueFromProperty("adhocFileName2");
			File file = new File(adhocFileDir + adhocFileName); // modify path and filename
			
			try {
				
				if(inputMode.trim().equalsIgnoreCase("dropDownInput")) {
					RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
					List<ImsiMsisdnForAdhocBean> imsiList = rDBAccess.returnAllImsi(stringInput);
					
					if(imsiList.size() > 0) {
						for(ImsiMsisdnForAdhocBean s : imsiList) {
							sb.append(s.getImsi()).append(",").append("NULL").append(",").append("NULL,NULL,NULL\n");
						}
						hasNoImsi = false;
					} else {
						hasNoImsi = true;
						// create an upload file with campaign-like format
						sb.append("NULL,NULL,NULL," + stringInput.substring(0, 3) + "," + stringInput.substring(3, stringInput.length()));
					}
				} else if(inputMode.trim().equalsIgnoreCase("textInput")) {
					String[] valFromSelect = stringInput.split("-", -1);
					for(int i = 0; i < valFromSelect.length; i++) {
						String[] valFromSelectParsed = valFromSelect[i].split(",",-1);
						String imsi = valFromSelectParsed[0];
						String msisdn = valFromSelectParsed[1];
						if(imsi.trim().isEmpty()) {
							imsi = "NULL";
						}
						if(msisdn.trim().isEmpty()) {
							msisdn = "NULL";
						}
						
						if(!imsi.equalsIgnoreCase("NULL")) {
							msisdn = "NULL";
						}
						
						sb.append(imsi).append(",").append(msisdn).append(",").append("NULL,NULL,NULL\n");
					}
					hasNoImsi = false;
				}
			
				/*if(hasNoImsi) {
					jsonResponse = JSONConverterUtility.convertToJSONFormat("No imsi found in database for the selected entry. Please try another one");
				} else {*/
					FileUtils.writeStringToFile(file, sb.toString()); 	
					//sftpSuccess = pushFileToServer(file);
					sftpSuccess = true;
					if(sftpSuccess) {
						// insert into database
						Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
						String line = "";
						BufferedReader bReader = new BufferedReader(new FileReader(file));
						while((line = bReader.readLine()) != null) {
							String[] parseLine = line.split(",", -1);
							String imsi = "", msisdn = "", vlr = "";
							
							if(hasNoImsi) {
								imsi = stringInput;
								msisdn = "NULL";
								vlr = "NULL";
							} else {
								imsi = parseLine[0];
								msisdn = parseLine[1];
								vlr = parseLine[2];
							}
							
							String timestamp = Long.toString((cal.getTimeInMillis()/1000));
							
							insertToAdHocTable2(imsi, msisdn, vlr, timestamp,sessionId);
						}
						bReader.close();
						jsonResponse = JSONConverterUtility.convertToJSONFormat("Ad-Hoc trigger is successful");
					} else {
						jsonResponse = JSONConverterUtility.convertToJSONFormat("Ad-Hoc trigger has failed due to SFTP problems!");
					}
				//}
			} catch (IOException e) {
				logger.error("execute() IOException => " + e.getMessage(), e);
			}	
		}
		return ActionSupport.SUCCESS;
	}
	
	public void insertToAdHocTable2(String imsi, String msisdn, String vlr, String timestamp, String sessionId) {
		
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		rDBAccess.insertToAdhocTableLocal(imsi, msisdn, vlr, timestamp, sessionId);
		
	}	
	
	public boolean pushFileToServer(File file) {
		
		String serverUser = mapTrackerPropertiesReader.getValueFromProperty("psiUser");
		String serverPass = mapTrackerPropertiesReader.getValueFromProperty("psiPassword");
		String serverAddress = mapTrackerPropertiesReader.getValueFromProperty("psiServer");
		String serverDir = mapTrackerPropertiesReader.getValueFromProperty("psiInputFolder");
		String psiTriggerCommand=mapTrackerPropertiesReader.getValueFromProperty("psiTriggerCommand");
		
		JSch jsch = new JSch();
        Session session = null;
        boolean success = false;
        try {
            session = jsch.getSession(serverUser, serverAddress, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(serverPass);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
           
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            sftpChannel.cd(serverDir);
           
            sftpChannel.put(new FileInputStream(file), file.getName());
            sftpChannel.exit();
            
            // run psi trigger (?)
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(psiTriggerCommand);
            channelExec.connect();
            channelExec.start();
            channelExec.disconnect();
            
            session.disconnect();
            success = true;
            
        } catch (JSchException e) {
            logger.error("pushFileToServer() JSchException=>" + e.getMessage(),e); 
        } catch (SftpException e) {
        	logger.error("pushFileToServer() SftpException=>" + e.getMessage(),e); 
        } catch (FileNotFoundException e) {
        	logger.error("pushFileToServer() FileNotFoundException=>" + e.getMessage(),e); 
		}
        
        return success;
	}

	public String getInputMode() {
		return inputMode;
	}

	public void setInputMode(String inputMode) {
		this.inputMode = inputMode;
	}

	public String getStringInput() {
		return stringInput;
	}

	public void setStringInput(String stringInput) {
		this.stringInput = stringInput;
	}

	public String getJsonResponse() {
		return jsonResponse;
	}

	public void setJsonResponse(String jsonResponse) {
		this.jsonResponse = jsonResponse;
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}

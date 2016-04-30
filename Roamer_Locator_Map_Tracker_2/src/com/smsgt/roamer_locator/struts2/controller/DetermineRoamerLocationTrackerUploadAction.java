package com.smsgt.roamer_locator.struts2.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import com.smsgt.roamer_locator.struts2.database.RoamerLocatorDatabaseAccess;
import com.smsgt.roamer_locator.struts2.util.JSONConverterUtility;
import com.smsgt.roamer_locator.struts2.util.MapTrackerPropertiesReader;

public class DetermineRoamerLocationTrackerUploadAction extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private File uploadFile;
	private String uploadFileContentType;
	private String uploadFileFileName;
	private String jsonResult;
	private String isInternationalRP;
	private StringBuilder sbValid, sbInvalid;
	private String operatorImsiTemp = "", operatorMsisdnTemp = "";
	
	private boolean isValuesValid = false, hasInvalidChars = false;
	private String sessionUserId;
	
	private static final Logger logger = Logger.getLogger(DetermineRoamerLocationTrackerUploadAction.class);
	private MapTrackerPropertiesReader mapTrackerPropertiesReader = new MapTrackerPropertiesReader("maptracker.properties");
	private Set<String> msisdnSet = new HashSet<String>();
	
	
	public String parseUploadedFile() {
		
			try {
				
				initializeMsisdnMap();
				
				String adhocFileDir = mapTrackerPropertiesReader.getValueFromProperty("adhocDir");
				String adhocFileName = mapTrackerPropertiesReader.getValueFromProperty("adhocFileName");
				
				sbValid = new StringBuilder();
				sbInvalid = new StringBuilder();
		
				try {
					// try to use Thread.sleep to create a delay
					// call Elantra server to trigger "near real-time" PSI
					File file = new File(adhocFileDir + adhocFileName); // modify path and filename
					
					if(getUploadFileFileName().endsWith(".csv")) {
						// check if file contents conforms to the format
						BufferedReader br = new BufferedReader(new FileReader(getUploadFile()));
						String lineContent = "";
						
						while((lineContent = br.readLine()) != null) {
							
							boolean hasInvalidCharset = lineContent.matches("^[0-9,]+$");
							
							if(!hasInvalidCharset) {
								isValuesValid = false;
								hasInvalidChars = true;
								break;
							
							} else {
								
								String[] lContent = lineContent.split(",", -1);
								String imsi = "NULL", msisdn = "NULL", vlr = "NULL";
								
								isValuesValid = true;
								
								if(lContent.length == 3) {
									
									if(lContent[0] != null && !lContent[0].equalsIgnoreCase("")) {
										imsi = lContent[0];
									}
									
									if(lContent[1] != null &&  !lContent[1].equalsIgnoreCase("")) {
										msisdn = lContent[1];
									}
									
									if(lContent[2] != null && !lContent[2].equalsIgnoreCase("")) {
										vlr = lContent[2];
									}
										
								} else if (lContent.length == 2) {
									
									if(lContent[0] != null && !lContent[0].equalsIgnoreCase("")) {
										imsi = lContent[0];
									} 
									
									if(lContent[1] != null &&  !lContent[1].equalsIgnoreCase("")) {
										msisdn = lContent[1];
									}	
								} else if (lContent.length == 1) {
									
									if(lContent[0] != null && !lContent[0].equalsIgnoreCase("")) {
										imsi = lContent[0];
									} 
								}
								
								if(isValuesValid) {
									if(!imsi.equalsIgnoreCase("NULL")) {
										
										String operatorToCompare = imsi.substring(0,3);
										validateEntriesForImsi(operatorImsiTemp, operatorToCompare, imsi, msisdn, vlr);
										
									} 
									if(!msisdn.equalsIgnoreCase("NULL")) {
										
										String operatorToCompare = msisdn.substring(0,5);
										validateEntriesForMsisdn(operatorMsisdnTemp, operatorToCompare, imsi, msisdn, vlr);
										
									}
								} 	
							}
						}
						br.close();
						
						// proceed only if sbInvalid does not have value
						logger.debug("parseUpload2 -> sbInvalid contents: " + sbInvalid.toString());
						if(sbInvalid.toString().trim().isEmpty()) {
							boolean sftpSuccess = false;
							FileUtils.writeStringToFile(file, sbValid.toString());		
							sftpSuccess = pushFileToServer(file);	
							//sftpSuccess = true;
							if(sftpSuccess) {
								// insert into database
								Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
								String line = "";
								BufferedReader bReader = new BufferedReader(new FileReader(file));
								while((line = bReader.readLine()) != null) {

									String[] parseLine = line.split(",", -1);
									String imsi = parseLine[0];
									String msisdn = parseLine[1];
									
									if(!imsi.equalsIgnoreCase("NULL")) {
										msisdn = "NULL";
									}
									
									String vlr = parseLine[2];
									String timestamp = Long.toString((cal.getTimeInMillis()/1000));
									
									insertToAdHocTable(imsi, msisdn, vlr, timestamp);
								}
								bReader.close();
								jsonResult = JSONConverterUtility.convertToJSONFormat("Upload success!");
								
							} else {
								jsonResult = JSONConverterUtility.convertToJSONFormat("Upload failed due to SFTP problems! Please contact support!");
							}
							
						} else {
							String details = "";
							
							if(hasInvalidChars) {
								details = "Invalid characters found in the uploaded file. Kindly remove";
							} else {
								if(Boolean.valueOf(isInternationalRP)) {
									details = "Local imsis/msisdns were found in the uploaded file as this ad-hoc mode is for international roaming partners only!";
								} else {
									details = "International imsis/msisdns were found in the uploaded file as this ad-hoc mode is for local only";
								}
							}
							jsonResult = JSONConverterUtility.convertToJSONFormat("Upload failed! " + details);
						}
					} else {
						jsonResult = JSONConverterUtility.convertToJSONFormat("Upload failed! File uploaded is not a csv");
					}
				} catch (IOException e2) {
					logger.error("parseUploadedFile() IOException => " + e2.getMessage(), e2);
					jsonResult = JSONConverterUtility.convertToJSONFormat("Upload failed! Error in uploaded file");
				} catch (Exception e2) {
					logger.error("parseUploadedFile() Exception => " + e2.getMessage(), e2);
					jsonResult = JSONConverterUtility.convertToJSONFormat("Upload failed! Error in uploaded file");
				}
				
				operatorImsiTemp = "";
				operatorMsisdnTemp = "";
				isValuesValid = true;
				hasInvalidChars = false;
				
			} catch (Exception e) {
				logger.error("parseUploadedFile() Exception => " + e.getMessage(), e);
				jsonResult = JSONConverterUtility.convertToJSONFormat("Upload failed! Error in uploaded file");
			}
			return ActionSupport.SUCCESS; 				
	}

	public String parseUploadedFile2() {
		
		try {
			
			initializeMsisdnMap(); 
			
			String adhocFileDir = mapTrackerPropertiesReader.getValueFromProperty("adhocDir");
			String adhocFileName = mapTrackerPropertiesReader.getValueFromProperty("adhocFileName2");
			
			sbValid = new StringBuilder();
			sbInvalid = new StringBuilder();
	
			try {
				// try to use Thread.sleep to create a delay
				// call Elantra server to trigger "near real-time" PSI
				File file = new File(adhocFileDir + adhocFileName); // modify path and filename
				
				if(getUploadFileFileName().endsWith(".csv")) {
					// check if file contents conforms to the format
					BufferedReader br = new BufferedReader(new FileReader(getUploadFile()));
					String lineContent = "";
					
					while((lineContent = br.readLine()) != null) {
						
						boolean hasInvalidCharset = lineContent.matches("^[0-9,]+$");
						
						if(!hasInvalidCharset) {
							isValuesValid = false;
							hasInvalidChars = true;
							break;
						
						} else {
							
							String[] lContent = lineContent.split(",", -1);
							String imsi = "NULL", msisdn = "NULL", vlr = "NULL";
							
							isValuesValid = true;
							
							if(lContent.length == 3) {
								
								if(lContent[0] != null && !lContent[0].equalsIgnoreCase("")) {
									imsi = lContent[0];
								}
								
								if(lContent[1] != null &&  !lContent[1].equalsIgnoreCase("")) {
									msisdn = lContent[1];
								}
								
								if(lContent[2] != null && !lContent[2].equalsIgnoreCase("")) {
									vlr = lContent[2];
								}
									
							} else if (lContent.length == 2) {
								
								if(lContent[0] != null && !lContent[0].equalsIgnoreCase("")) {
									imsi = lContent[0];
								} 
								
								if(lContent[1] != null &&  !lContent[1].equalsIgnoreCase("")) {
									msisdn = lContent[1];
								}	
							} else if (lContent.length == 1) {
								
								if(lContent[0] != null && !lContent[0].equalsIgnoreCase("")) {
									imsi = lContent[0];
								} 
							}

							if(isValuesValid) {
								if(!imsi.equalsIgnoreCase("NULL")) {
									
									String operatorToCompare = imsi.substring(0,3);
									validateEntriesForImsi(operatorImsiTemp, operatorToCompare, imsi, msisdn, vlr);
									
								} 
								
								if(!msisdn.equalsIgnoreCase("NULL")) {
									
									String operatorToCompare = msisdn.substring(0,5);
									validateEntriesForMsisdn(operatorMsisdnTemp, operatorToCompare, imsi, msisdn, vlr);
									
								}
							} 	
						}
					}
					
					br.close();
					// proceed only if sbInvalid does not have value
					logger.debug("parseUpload2 -> sbInvalid contents: " + sbInvalid.toString());
					if(sbInvalid.toString().trim().isEmpty()) {
						boolean sftpSuccess = false;
						FileUtils.writeStringToFile(file, sbValid.toString());		
						//sftpSuccess = pushFileToServer(file);	
						sftpSuccess = true;
						if(sftpSuccess) {
							// insert into database
							Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
							String line = "";
							BufferedReader bReader = new BufferedReader(new FileReader(file));
							while((line = bReader.readLine()) != null) {

								String[] parseLine = line.split(",", -1);
								String imsi = parseLine[0];
								String msisdn = parseLine[1];
								
								if(!imsi.equalsIgnoreCase("NULL")) {
									msisdn = "NULL";
								}
								
								String vlr = parseLine[2];
								String timestamp = Long.toString((cal.getTimeInMillis()/1000));
								
								insertToAdHocTable2(imsi, msisdn, vlr, timestamp, sessionUserId);
							}
							bReader.close();
							jsonResult = JSONConverterUtility.convertToJSONFormat("Upload success!");
							
						} else {
							jsonResult = JSONConverterUtility.convertToJSONFormat("Upload failed due to SFTP problems! Please contact support!");
						}
						
					} else {
						String details = "";
						
						if(hasInvalidChars) {
							details = "Invalid characters found in the uploaded file. Kindly remove";
						} else {
							if(Boolean.valueOf(isInternationalRP)) {
								details = "Local imsis/msisdns were found in the uploaded file as this ad-hoc mode is for international roaming partners only!";
							} else {
								details = "International imsis/msisdns were found in the uploaded file as this ad-hoc mode is for local only";
							}
						}
						jsonResult = JSONConverterUtility.convertToJSONFormat("Upload failed! " + details);
					}
				} else {
					jsonResult = JSONConverterUtility.convertToJSONFormat("Upload failed! File uploaded is not a csv");
				}
			} catch (IOException e2) {
				logger.error("parseUploadedFile() IOException => " + e2.getMessage(), e2);
				jsonResult = JSONConverterUtility.convertToJSONFormat("Upload failed! Error in uploaded file");
			} catch (Exception e2) {
				logger.error("parseUploadedFile() Exception => " + e2.getMessage(), e2);
				jsonResult = JSONConverterUtility.convertToJSONFormat("Upload failed! Error in uploaded file");
			}
			
			operatorImsiTemp = "";
			operatorMsisdnTemp = "";
			isValuesValid = true;
			hasInvalidChars = false;
			
		} catch (Exception e) {
			logger.error("parseUploadedFile() Exception => " + e.getMessage(), e);
			jsonResult = JSONConverterUtility.convertToJSONFormat("Upload failed! Error in uploaded file");
		}
		return ActionSupport.SUCCESS; 				
	}	
	
	public void validateEntriesForImsi(String operatorImsiTemp, String currentImsiOperator, String imsi, String msisdn, String vlr) {
		
		if(Boolean.valueOf(isInternationalRP)) {
			
			if(!imsi.startsWith("515")) {
				msisdn = "NULL";
				sbValid.append(imsi).append(",")
				.append(msisdn).append(",")
					.append(vlr).append(",")
						.append("NULL,NULL\n");	
				isValuesValid = true;
			} else {
				msisdn = "NULL";
				sbInvalid.append(imsi).append(",")
				.append(msisdn).append(",")
					.append(vlr).append(",")
						.append("NULL,NULL\n");	
				isValuesValid = false;
			}
		} else {
			
			if(imsi.startsWith("515")) {
				msisdn = "NULL";
				sbValid.append(imsi).append(",")
				.append(msisdn).append(",")
					.append(vlr).append(",")
						.append("NULL,NULL\n");	
				isValuesValid = true;
			} else {
				msisdn = "NULL";
				sbInvalid.append(imsi).append(",")
				.append(msisdn).append(",")
					.append(vlr).append(",")
						.append("NULL,NULL\n");	
				isValuesValid = false;
			}
		}
		
	}
	
	public void validateEntriesForMsisdn(String operatorMsisdnTemp, String currentMsisdnOperator, String imsi, String msisdn, String vlr) {
		
		String msisdnPrefix = "";
		if (msisdn.startsWith("0")) {
			msisdnPrefix = msisdn.substring(0,4);
		} else if (msisdn.startsWith("63")){
			msisdnPrefix = msisdn.substring(0,5);
		}
		
		if(Boolean.valueOf(isInternationalRP)) {	
	
			if (msisdnSet.contains(msisdnPrefix)) {
				sbValid.append(imsi).append(",")
				.append(msisdn).append(",")
					.append(vlr).append(",")
						.append("NULL,NULL\n");	
			} else {
				sbInvalid.append(imsi).append(",")
				.append(msisdn).append(",")
					.append(vlr).append(",")
						.append("NULL,NULL\n");	
			}
			
		} else {
			
			if (msisdnSet.contains(msisdnPrefix)) {
				sbValid.append(imsi).append(",")
				.append(msisdn).append(",")
					.append(vlr).append(",")
						.append("NULL,NULL\n");	
			} else {
				sbInvalid.append(imsi).append(",")
				.append(msisdn).append(",")
					.append(vlr).append(",")
						.append("NULL,NULL\n");	
			}
		}
	}
	
	private void initializeMsisdnMap() {
		
		List<String> localMsisdnList = Arrays.asList("0813","0907","0908","0909","0910","0912","0918","0919","0920","0921","0928","0929","0930","0938","0939","0946","0947","0948","0949","0989","0998","0999","0922","0923","0925","0932","0933","0934","0942","0943","0817","0905","0906","0915","0916","0917","0926","0927","0935","0936","0937","0994","0996","0997","0977","0979","0973","0974","0975"
				,"63913","63907","63908","63909","63910","63912","63918","63919","63920","63921","63928","63929","63930","63938","63939","63946","63947","63948","63949","63989","63998","63999","63922","63923","63925","63932","63933","63934","63942","63943","63817","63905","63906","63915","63916","63917","63926","63927","63935","63936","63937","63994","63996","63997","63977","63979","63973","63974", "63975");
		
		for (String s: localMsisdnList) {
			msisdnSet.add(s);
		}
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

	public void insertToAdHocTable(String imsi, String msisdn, String vlr, String timestamp) {
		
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		rDBAccess.insertToAdhocTable(imsi, msisdn, vlr, timestamp);
		
	}

	public void insertToAdHocTable2(String imsi, String msisdn, String vlr, String timestamp, String sessionId) {
		
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		rDBAccess.insertToAdhocTableLocal(imsi, msisdn, vlr, timestamp, sessionId);
		
	}	
	
	public File getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(File uploadFile) {
		this.uploadFile = uploadFile;
	}

	public String getUploadFileContentType() {
		return uploadFileContentType;
	}


	public void setUploadFileContentType(String uploadFileContentType) {
		this.uploadFileContentType = uploadFileContentType;
	}


	public String getUploadFileFileName() {
		return uploadFileFileName;
	}


	public void setUploadFileFileName(String uploadFileName) {
		this.uploadFileFileName = uploadFileName;
	}


	public String getJsonResult() {
		return jsonResult;
	}

	public void setJsonResult(String jsonResult) {
		this.jsonResult = jsonResult;
	}

	public String getIsInternationalRP() {
		return isInternationalRP;
	}

	public void setIsInternationalRP(String isInternationalRP) {
		this.isInternationalRP = isInternationalRP;
	}

	public String getSessionUserId() {
		return sessionUserId;
	}

	public void setSessionUserId(String sessionUserId) {
		this.sessionUserId = sessionUserId;
	}
}
package com.smsgt.roamer_locator.struts2.controller;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionSupport;
import com.smsgt.roamer_locator.struts2.bean.ImsiMsisdnForAdhocBean;
import com.smsgt.roamer_locator.struts2.bean.RoamerLocationBean;
import com.smsgt.roamer_locator.struts2.bean.RoamerLocationBeanAdHoc;
import com.smsgt.roamer_locator.struts2.bean.RoamerLocatorScheduleBean;
import com.smsgt.roamer_locator.struts2.database.RoamerLocatorDatabaseAccess;
import com.smsgt.roamer_locator.struts2.util.JSONConverterUtility;
import com.smsgt.roamer_locator.struts2.util.RoamwareFileReaderAndDBAccessor;

public class RoamerMapTrackerUtilitiesAction extends ActionSupport {

	private static final long serialVersionUID = 1L;
	
	private String serverDate;
	private String scheduleInterval;
	private String jsonResponse;
	private Boolean isAdhocRunning;
	private String inboundMapDate;
	private String sessionUId;
	private String psiStatus;
	private List<RoamerLocationBeanAdHoc> roamwareAdhocList = new ArrayList<RoamerLocationBeanAdHoc>();
	private RoamerLocatorScheduleBean rSchedBean;
	
	private static final Logger logger = Logger.getLogger(RoamerMapTrackerUtilitiesAction.class);
	
	public String checkIfPSIIsRunningOnServer() {
		
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		psiStatus = rDBAccess.checkIfPSIIsRunning();
		psiStatus = JSONConverterUtility.convertToJSONFormat(psiStatus);
		return ActionSupport.SUCCESS;
	}
	
	public String fetchDateFromServer() {
		
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		serverDate = sdf.format(cal.getTime());
		serverDate = JSONConverterUtility.convertToJSONFormat(serverDate);
		return ActionSupport.SUCCESS;
		
	}

	public String fetchScheduleInterval() {
		
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		int schedInterval = rDBAccess.getScheduleInterval();
		scheduleInterval = Integer.toString(new Integer(schedInterval));
		scheduleInterval = JSONConverterUtility.convertToJSONFormat(scheduleInterval);
		return ActionSupport.SUCCESS;
	}
	
	public String fetchCurrentTimeForInboundMap() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		String timestamp = rDBAccess.returnLatestDateTimeForInboundMapCount();
		if(timestamp != null ) {
			
			if(!timestamp.trim().isEmpty()) {
				inboundMapDate = sdf.format(Long.parseLong(timestamp) * 1000);
			} else {
				Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
				inboundMapDate = sdf.format(c.getTime());
			}
			
		} else {
			Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
			inboundMapDate = sdf.format(c.getTime());
		}
		inboundMapDate = JSONConverterUtility.convertToJSONFormat(inboundMapDate);
		return ActionSupport.SUCCESS;
	}

	public String fetchCurrentTimeForInboundMap2() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		String timestamp = rDBAccess.returnLatestDateTimeForInboundMapCountLocal();
		if(timestamp != null ) {
			
			if(!timestamp.trim().isEmpty()) {
				inboundMapDate = sdf.format(Long.parseLong(timestamp) * 1000);
			} else {
				Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
				inboundMapDate = sdf.format(c.getTime());
			}
			
		} else {
			Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
			inboundMapDate = sdf.format(c.getTime());
		}
		inboundMapDate = JSONConverterUtility.convertToJSONFormat(inboundMapDate);
		return ActionSupport.SUCCESS;
	}	
	
	public String checkForUpdatedValues() {
		
		try {
			
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
			
			Integer checkFirstCount = rDBAccess.checkAdHocEntryIfToday(sdf.format(cal.getTime()));
			if(checkFirstCount > 0) {
				
				ArrayList<ImsiMsisdnForAdhocBean> adhocBeanList = rDBAccess.viewAdHocResults(sdf.format(cal.getTime()));
				
				if(adhocBeanList.size() > 0) {
					
					for(ImsiMsisdnForAdhocBean iAdhocBean : adhocBeanList) {
						
						String imsi = "", msisdn = "";
						
						RoamwareFileReaderAndDBAccessor rFReaderDB = new RoamwareFileReaderAndDBAccessor();
				    	RoamerLocationBeanAdHoc roamerLocationBeanUpload = new RoamerLocationBeanAdHoc();
				    	
				    	imsi = iAdhocBean.getImsi();
				    	msisdn = iAdhocBean.getMsisdn();
				    	
				    	if(imsi.equalsIgnoreCase("NULL")) {
				    		// get imsi from database using msisdn
				    		imsi = rDBAccess.getImsiForMSISDNInput(msisdn);
				    	}
				    	
				    	if(msisdn.equalsIgnoreCase("NULL")) {
				    		// get msisdn from database using imsi
				    		msisdn = rDBAccess.getMSISDNForIMSIInput(imsi);
				    	}
				    	
				    	String fileName = rDBAccess.getRoamerLocationFromDB(imsi,msisdn);
				    	
				    	String country = "No country found", operator = "No operator found";
				    	
				    	if(!imsi.trim().isEmpty()) {
				    		
				    		String[] arr = rDBAccess.returnCountryAndOperatorName(imsi);
				    		country = (arr[0] == null || arr[0].trim().isEmpty()) ? "No country found" : arr[0];
				    		operator = (arr[1] == null || arr[1].trim().isEmpty()) ? "No operator found" : arr[1];
				    		
				    	} else {
				    		imsi = "No imsi found";
				    	}
				    	
				    	if(msisdn.trim().isEmpty()) {
				    		msisdn = "No msisdn found";
				    	}
				    	
				    	roamerLocationBeanUpload.setOperatorCountry(country);
				    	roamerLocationBeanUpload.setOperatorName(operator);
						roamerLocationBeanUpload.setImsi(imsi);
						roamerLocationBeanUpload.setMsisdn(msisdn);
						
						if(fileName.trim().isEmpty()) {
							
							List<RoamerLocationBean> rList =  addToList(cal);
							roamerLocationBeanUpload.setRoamerLocationBean(rList);
							
						} else {
							
							List<RoamerLocationBean> rList = rFReaderDB.retrieveAllInfo(fileName, msisdn);
							if(rList.size() > 0) {
								roamerLocationBeanUpload.setRoamerLocationBean(rList);
							} else {
								rList = addToList(cal);
								roamerLocationBeanUpload.setRoamerLocationBean(rList);
							}
						}
						roamwareAdhocList.add(roamerLocationBeanUpload);
					}
					jsonResponse = JSONConverterUtility.convertToJSONFormat(roamwareAdhocList);	
				} else {
					jsonResponse = JSONConverterUtility.convertToJSONFormat("PSI is still processing.. Please try again later!");
				}
				
			} else if (checkFirstCount <= 0) {
				jsonResponse = JSONConverterUtility.convertToJSONFormat("No AdHoc trigger was made for today. Kindly request or make a trigger.");
			}
						
		} catch(Exception e) {
			logger.error("checkForUpdatedValues() Exception => " + e.getMessage(), e);
		}
		
		return ActionSupport.SUCCESS;
	}

	public String checkForUpdatedValues2() {
		
		try {
			
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
			
			Integer checkFirstCount = rDBAccess.checkAdHocEntryIfTodayLocal(sdf.format(cal.getTime()));
			if(checkFirstCount > 0) {
				
				ArrayList<ImsiMsisdnForAdhocBean> adhocBeanList = rDBAccess.viewAdHocResultsLocal(sdf.format(cal.getTime()), sessionUId);
				
				if(adhocBeanList.size() > 0) {
					
					for(ImsiMsisdnForAdhocBean iAdhocBean : adhocBeanList) {
						
						String imsi = "", msisdn = "";
						
						RoamwareFileReaderAndDBAccessor rFReaderDB = new RoamwareFileReaderAndDBAccessor();
				    	RoamerLocationBeanAdHoc roamerLocationBeanUpload = new RoamerLocationBeanAdHoc();
				    	
				    	imsi = iAdhocBean.getImsi();
				    	msisdn = iAdhocBean.getMsisdn();
				    	
				    	if(imsi.equalsIgnoreCase("NULL")) {
				    		// get imsi from database using msisdn
				    		imsi = rDBAccess.getImsiForMSISDNInput2(msisdn);
				    	}
				    	
				    	if(msisdn.equalsIgnoreCase("NULL")) {
				    		// get msisdn from database using imsi
				    		msisdn = rDBAccess.getMSISDNForIMSIInput2(imsi);
				    	}
				    	
				    	String fileName = rDBAccess.getRoamerLocationFromDB2(imsi,msisdn);
				    	
				    	String country = "Philippines", operator = "Globe Telecom";
				    	
				    	roamerLocationBeanUpload.setOperatorCountry(country);
				    	roamerLocationBeanUpload.setOperatorName(operator);
						roamerLocationBeanUpload.setImsi(imsi.trim().isEmpty() ? "No imsi found" : imsi);
						roamerLocationBeanUpload.setMsisdn(msisdn.trim().isEmpty() ? "No msisdn found" : msisdn);
						
						if(fileName.trim().isEmpty()) {
							
							List<RoamerLocationBean> rList =  addToList(cal);
							roamerLocationBeanUpload.setRoamerLocationBean(rList);
							
						} else {
							
							List<RoamerLocationBean> rList = rFReaderDB.retrieveAllInfo(fileName, msisdn);
							if(rList.size() > 0) {
								roamerLocationBeanUpload.setRoamerLocationBean(rList);
							} else {
								rList = addToList(cal);
								roamerLocationBeanUpload.setRoamerLocationBean(rList);
							}
						}
						roamwareAdhocList.add(roamerLocationBeanUpload);
					}
					jsonResponse = JSONConverterUtility.convertToJSONFormat(roamwareAdhocList);	
				} else {
					jsonResponse = JSONConverterUtility.convertToJSONFormat("PSI is still processing.. Please try again later!");
				}
				
			} else if (checkFirstCount <= 0) {
				jsonResponse = JSONConverterUtility.convertToJSONFormat("No AdHoc trigger was made for today. Kindly request or make a trigger.");
			}
						
		} catch(Exception e) {
			logger.error("checkForUpdatedValues() Exception => " + e.getMessage(), e);
		}
		
		return ActionSupport.SUCCESS;
	}	
	
	private List<RoamerLocationBean> addToList(Calendar cal) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		List<RoamerLocationBean> rBeanList = new ArrayList<RoamerLocationBean>();
		RoamerLocationBean rBean = new RoamerLocationBean();
		rBean.setPosition("");
		rBean.setCellName("Nothing found");
		rBean.setCellId("No returned cell id");
		rBean.setCellLac("No returned cell lac");
		rBean.setLatitude("Nothing found");
		rBean.setLongitude("Nothing found");
		rBean.setBarangay("Nothing found");
		rBean.setTown("Nothing found");
		rBean.setSite_address("Nothing found");
		rBean.setTimestamp(sdf.format(cal.getTimeInMillis()));
		rBeanList.add(rBean);
		
		return rBeanList;
	}

	public String isAdhocRunning() {
		// check if adhoc table has entries
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
		isAdhocRunning = rDBAccess.checkIfAdHocIsRunning(sdf.format(cal.getTime()));
		return ActionSupport.SUCCESS;
	}
	
	public String isCampaignRunning() {
		
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		rSchedBean = rDBAccess.checkForCurrentCampaign();
		return ActionSupport.SUCCESS;
	}

	public static String fixEncoding(String latin1) {
	  try {
	   byte[] bytes = latin1.getBytes("ISO-8859-1");
	   if (!validUTF8(bytes))
	    return latin1;   
	   return new String(bytes, "UTF-8");  
	  } catch (UnsupportedEncodingException e) {
	   // Impossible, throw unchecked
	   throw new IllegalStateException("No Latin1 or UTF-8: " + e.getMessage());
	  }

	 }

	 public static boolean validUTF8(byte[] input) {
	  int i = 0;
	  // Check for BOM
	  if (input.length >= 3 && (input[0] & 0xFF) == 0xEF
	    && (input[1] & 0xFF) == 0xBB & (input[2] & 0xFF) == 0xBF) {
	   i = 3;
	  }

	  int end;
	  for (int j = input.length; i < j; ++i) {
	   int octet = input[i];
	   if ((octet & 0x80) == 0) {
	    continue; // ASCII
	   }

	   // Check for UTF-8 leading byte
	   if ((octet & 0xE0) == 0xC0) {
	    end = i + 1;
	   } else if ((octet & 0xF0) == 0xE0) {
	    end = i + 2;
	   } else if ((octet & 0xF8) == 0xF0) {
	    end = i + 3;
	   } else {
	    // Java only supports BMP so 3 is max
	    return false;
	   }

	   while (i < end) {
	    i++;
	    octet = input[i];
	    if ((octet & 0xC0) != 0x80) {
	     // Not a valid trailing byte
	     return false;
	    }
	   }
	  }
	  return true;
	 }
	
	
	public String getServerDate() {
		return serverDate;
	}

	public void setServerDate(String serverDate) {
		this.serverDate = serverDate;
	}

	public String getScheduleInterval() {
		return scheduleInterval;
	}

	public void setScheduleInterval(String scheduleInterval) {
		this.scheduleInterval = scheduleInterval;
	}

	public String getJsonResponse() {
		return jsonResponse;
	}

	public void setJsonResponse(String jsonResponse) {
		this.jsonResponse = jsonResponse;
	}

	public Boolean getIsAdhocRunning() {
		return isAdhocRunning;
	}

	public void setIsAdhocRunning(Boolean isAdhocRunning) {
		this.isAdhocRunning = isAdhocRunning;
	}

	public List<RoamerLocationBeanAdHoc> getRoamwareAdhocList() {
		return roamwareAdhocList;
	}

	public void setRoamwareAdhocList(List<RoamerLocationBeanAdHoc> roamwareAdhocList) {
		this.roamwareAdhocList = roamwareAdhocList;
	}

	public RoamerLocatorScheduleBean getrSchedBean() {
		return rSchedBean;
	}

	public void setrSchedBean(RoamerLocatorScheduleBean rSchedBean) {
		this.rSchedBean = rSchedBean;
	}

	public String getInboundMapDate() {
		return inboundMapDate;
	}

	public void setInboundMapDate(String inboundMapDate) {
		this.inboundMapDate = inboundMapDate;
	}

	public String getSessionUId() {
		return sessionUId;
	}

	public void setSessionUId(String sessionUId) {
		this.sessionUId = sessionUId;
	}

	public String getPsiStatus() {
		return psiStatus;
	}

	public void setPsiStatus(String psiStatus) {
		this.psiStatus = psiStatus;
	}
}

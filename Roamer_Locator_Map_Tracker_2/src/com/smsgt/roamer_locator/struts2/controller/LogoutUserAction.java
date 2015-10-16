package com.smsgt.roamer_locator.struts2.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;
import com.smsgt.roamer_locator.struts2.database.RoamerLocatorDatabaseAccess;

public class LogoutUserAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private Map<String,Object> session;
	private static final Logger logger = Logger.getLogger(LogoutUserAction.class);
	
	public String logout() {
		
		try {
			if(session != null) {
				//if(((String) session.get("loginId")).equalsIgnoreCase("admin")) {

					Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					String dateTime = sdf.format(cal.getTime());					
					
					RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
					rDBAccess.insertToUserLogs(((String) session.get("loginId")), ((String) session.get("loginName")), "Logout", "", dateTime);
					
					session.remove("loginId");
					session.remove("loginPassword");
					session.remove("loginType");
					session.remove("loginName");
					session.clear();
					
				//}
			}
		} catch(Exception e) {
			logger.error("logout() Exception => " + e.getMessage(), e);
		}
		
		return ActionSupport.SUCCESS;
	}
	

	@Override
	public void setSession(Map<String, Object> arg0) {
		this.session = arg0;
	}

}

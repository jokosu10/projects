package com.smsgt.roamer_locator.struts2.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;
import com.smsgt.roamer_locator.struts2.bean.RoamerLocatorUser;
import com.smsgt.roamer_locator.struts2.database.RoamerLocatorDatabaseAccess;

public class ValidateUserAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = 1L;
	private String userName;
	private String password;
	private String returnValue;
	private String type;
	
	private Map<String,Object> session;
	
	public String execute() {
		return returnValue;
	}
		
	@Override
	public void validate() {
		
		if(!userName.trim().isEmpty() && !password.trim().isEmpty()) {
			// validate user, if admin or regular user, then return success
			RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
			RoamerLocatorUser rUser = rDBAccess.getUserType(userName, password);
			//String[] userDetails = rDBAccess.getUserType(userName, password);
			
			if(rUser == null) {
				addActionError("Invalid User specified!");
				type = "";
				returnValue = ActionSupport.INPUT;
			} else {
				String userId = String.valueOf(rUser.getId());
				String userType =  rUser.getAdminType();
				if(userType.trim().equalsIgnoreCase("")) {
					addActionError("Invalid User specified!");
					type = "";
					returnValue = ActionSupport.INPUT;
				} else {
					// check what type of user
					// if 1 - admin
					// if 2 - super admin
					// if 3 - regular user (view only)
					
					if(userType.equalsIgnoreCase("2")) {
						returnValue = "success_admin";
					} else if(userType.equalsIgnoreCase("1")) {
						returnValue = "success_admin";
					} else {
						returnValue = ActionSupport.SUCCESS;
					}
					
					session.put("loginId", userId);
					session.put("loginUname", userName);
					session.put("loginPassword", password);
					session.put("loginType",userType);
					session.put("loginName", rUser.getName());
					type = userType;
					
					Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					String dateTime = sdf.format(cal.getTime());
					
					rDBAccess.insertToUserLogs(String.valueOf(rUser.getId()), rUser.getName(), "Login", "Login.jsp", dateTime);
					
				}
			}			
		} else {
			
			if(userName.trim().isEmpty() && !password.trim().isEmpty()) {
				addFieldError("userName","Username is required");
			} else if(password.trim().isEmpty() && !userName.trim().isEmpty()) {
				addFieldError("password","Password is required");
			} else {
				addActionError("Missing mandatory fields!");
			}
			returnValue = ActionSupport.INPUT;
		}
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void setSession(Map<String, Object> arg0) {
		this.session = arg0;
	} 

	public Map<String, Object> getSession() {
		return session;
	}

	public String getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}

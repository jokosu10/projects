package com.smsgt.roamer_locator.struts2.controller;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;
import com.smsgt.roamer_locator.struts2.bean.RoamerLocatorUser;
import com.smsgt.roamer_locator.struts2.database.RoamerLocatorDatabaseAccess;

public class ValidateUser2Action extends ActionSupport implements SessionAware {

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
			
			String userId = String.valueOf(rUser.getId());
			String userType =  rUser.getAdminType();
			if(userType.trim().equalsIgnoreCase("") || userType.trim().equalsIgnoreCase("1")) {
				addActionError("Invalid User specified!");
				type = "";
				returnValue = ActionSupport.INPUT;
			} else {
				
				if(!userType.trim().equalsIgnoreCase("1")) {
				     
					type = userType;
					session.put("sess_id", userId);
					session.put("loginId", userName);
					session.put("loginPassword", password);
					session.put("loginType", type);
					returnValue = ActionSupport.SUCCESS;
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

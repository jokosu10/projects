package com.smsgt.roamer_locator.struts2.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.opensymphony.xwork2.ActionSupport;
import com.smsgt.roamer_locator.struts2.bean.RoamerLocatorUser;
import com.smsgt.roamer_locator.struts2.bean.RoamerLocatorUserLogs;
import com.smsgt.roamer_locator.struts2.database.RoamerLocatorDatabaseAccess;
import com.smsgt.roamer_locator.struts2.util.JSONConverterUtility;

public class UserOperationsAction extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private String userId;
	private String userName;
	private String password;
	private String createUserReturn;
	private String deleteUserReturn;
	private String updateUserReturn;
	private String viewUsersReturn;
	private String viewLogsReturn;
	private String userType;
	private String createdBy;
	private String updatedBy;
	private String fullName;
	private String adminType;
	private String action;
	private String page;
	private String dateSearch;
	private List<RoamerLocatorUser> roamerLocatorUserList = new ArrayList<RoamerLocatorUser>();
	private List<RoamerLocatorUserLogs> roamerLocatorUserLogsList = new ArrayList<RoamerLocatorUserLogs>();
		
	public String create() {
		
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
		
		if(userType.equalsIgnoreCase("Regular User")) {
			adminType = "3";
		} else if (userType.equalsIgnoreCase("Admin User")) {
			adminType = "1";
		} else if (userType.equalsIgnoreCase("Super Admin User")){
			adminType = "2";
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String dateTime = sdf.format(cal.getTime());	
		createUserReturn = rDBAccess.createUser(userName, password, userType, adminType, createdBy, fullName, dateTime);
		createUserReturn = JSONConverterUtility.convertToJSONFormat(createUserReturn);
		return ActionSupport.SUCCESS;
	}
	
	public String delete() {
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		deleteUserReturn = rDBAccess.deleteUser(userId);
		deleteUserReturn = JSONConverterUtility.convertToJSONFormat(deleteUserReturn);
		return ActionSupport.SUCCESS;
	}	
	
	public String update() {
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		RoamerLocatorUser roamerLocatorUser = new RoamerLocatorUser();
		
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
		
		if(userType.equalsIgnoreCase("Regular User")) {
			adminType = "3";
		} else if (userType.equalsIgnoreCase("Admin User")) {
			adminType = "1";
		} else if (userType.equalsIgnoreCase("Super Admin User")){
			adminType = "2";
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String dateTime = sdf.format(cal.getTime());
		
		roamerLocatorUser.setUserName(userName);
		roamerLocatorUser.setName(fullName);
		roamerLocatorUser.setOrigPassword(password);
		roamerLocatorUser.setUserType(userType);
		roamerLocatorUser.setAdminType(adminType);
		roamerLocatorUser.setDateTimeUpdated(dateTime);
		roamerLocatorUser.setUpdatedBy(updatedBy);
		roamerLocatorUser.setId(Long.valueOf(userId));
		updateUserReturn = rDBAccess.updateUser(roamerLocatorUser);
		updateUserReturn = JSONConverterUtility.convertToJSONFormat(updateUserReturn);
		return ActionSupport.SUCCESS;
	}	

	public String viewAllUsers() {
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		roamerLocatorUserList = rDBAccess.getAllUsers(userName, userId);
		viewUsersReturn = JSONConverterUtility.convertToJSONFormat(roamerLocatorUserList);
		return ActionSupport.SUCCESS;
	}		
	
	public String viewUserLogs() {
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		roamerLocatorUserLogsList = rDBAccess.viewUserLogs(adminType, userName, action, dateSearch);
		viewLogsReturn = JSONConverterUtility.convertToJSONFormat(roamerLocatorUserLogsList);
		return ActionSupport.SUCCESS;
	}		
	
	public String insertToUserLogs() {
		RoamerLocatorDatabaseAccess rDBAccess = new RoamerLocatorDatabaseAccess();
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String dateTime = sdf.format(cal.getTime());
		rDBAccess.insertToUserLogs(userId, fullName, action.replaceAll("_", " "), page, dateTime);
		return ActionSupport.SUCCESS;
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

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public List<RoamerLocatorUser> getRoamerLocatorUserList() {
		return roamerLocatorUserList;
	}

	public List<RoamerLocatorUserLogs> getRoamerLocatorUserLogsList() {
		return roamerLocatorUserLogsList;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAdminType() {
		return adminType;
	}

	public void setAdminType(String adminType) {
		this.adminType = adminType;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getDateSearch() {
		return dateSearch;
	}

	public void setDateSearch(String dateSearch) {
		this.dateSearch = dateSearch;
	}

	public void setRoamerLocatorUserList(
			List<RoamerLocatorUser> roamerLocatorUserList) {
		this.roamerLocatorUserList = roamerLocatorUserList;
	}

	public void setRoamerLocatorUserLogsList(
			List<RoamerLocatorUserLogs> roamerLocatorUserLogsList) {
		this.roamerLocatorUserLogsList = roamerLocatorUserLogsList;
	}

	public String getCreateUserReturn() {
		return createUserReturn;
	}

	public void setCreateUserReturn(String createUserReturn) {
		this.createUserReturn = createUserReturn;
	}

	public String getDeleteUserReturn() {
		return deleteUserReturn;
	}

	public void setDeleteUserReturn(String deleteUserReturn) {
		this.deleteUserReturn = deleteUserReturn;
	}

	public String getUpdateUserReturn() {
		return updateUserReturn;
	}

	public void setUpdateUserReturn(String updateUserReturn) {
		this.updateUserReturn = updateUserReturn;
	}

	public String getViewUsersReturn() {
		return viewUsersReturn;
	}

	public void setViewUsersReturn(String viewUsersReturn) {
		this.viewUsersReturn = viewUsersReturn;
	}

	public String getViewLogsReturn() {
		return viewLogsReturn;
	}

	public void setViewLogsReturn(String viewLogsReturn) {
		this.viewLogsReturn = viewLogsReturn;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
}

package com.f8mobile.community_app.mobile.model;

import java.io.Serializable;

public class UserMessages implements Serializable {

	private static final long serialVersionUID = 1L;
	private String senderId;
	private String recipientId;
	private String user;
	private String message;
	private String timestamp;
	private String regIdSender;
	private String regIdReceiver;
	
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getRecipientId() {
		return recipientId;
	}
	public void setRecipientId(String recipientId) {
		this.recipientId = recipientId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getRegIdSender() {
		return regIdSender;
	}
	public void setRegIdSender(String regIdSender) {
		this.regIdSender = regIdSender;
	}
	public String getRegIdReceiver() {
		return regIdReceiver;
	}
	public void setRegIdReceiver(String regIdReceiver) {
		this.regIdReceiver = regIdReceiver;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	
}

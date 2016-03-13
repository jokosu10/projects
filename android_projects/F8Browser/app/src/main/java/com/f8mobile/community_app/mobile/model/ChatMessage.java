package com.f8mobile.community_app.mobile.model;

public class ChatMessage  {

	private boolean left;
	private String message;
	private String timestamp;

	public ChatMessage(boolean left, String message, String timestamp) {
		super();
		setLeft(left);
		setMessage(message);
		setTimestamp(timestamp);
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
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
}

package com.smsgt.mobile.application.supertsuper_v3.database.model;

import java.io.Serializable;

public class AchievementsModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String achievementId;
	private String achievementImageName;
	private String achievementName;
	private String achievementDescription;
	private String achievementValue;
	
	public String getAchievementId() {
		return achievementId;
	}
	public void setAchievementId(String achievementId) {
		this.achievementId = achievementId;
	}
	public String getAchievementName() {
		return achievementName;
	}
	public void setAchievementName(String achievementName) {
		this.achievementName = achievementName;
	}
	public String getAchievementDescription() {
		return achievementDescription;
	}
	public void setAchievementDescription(String achievementDescription) {
		this.achievementDescription = achievementDescription;
	}
	public String getAchievementValue() {
		return achievementValue;
	}
	public void setAchievementValue(String achievementValue) {
		this.achievementValue = achievementValue;
	}
	public String getAchievementImageName() {
		return achievementImageName;
	}
	public void setAchievementImageName(String achievementImageName) {
		this.achievementImageName = achievementImageName;
	}
	

}

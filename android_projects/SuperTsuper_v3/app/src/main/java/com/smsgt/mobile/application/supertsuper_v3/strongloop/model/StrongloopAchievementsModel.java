package com.smsgt.mobile.application.supertsuper_v3.strongloop.model;

import com.strongloop.android.loopback.Model;

public class StrongloopAchievementsModel extends Model {
	
	private Integer achievement_id;
	private String achievement_category_identifier;
	private String achievement_image_name;
	private String achievement_name;
	private String achievement_description;
	private Integer achievement_value;
	
	public Integer getAchievement_id() {
		return achievement_id;
	}
	public void setAchievement_id(Integer achievement_id) {
		this.achievement_id = achievement_id;
	}
	public String getAchievement_category_identifier() {
		return achievement_category_identifier;
	}
	public void setAchievement_category_identifier(
			String achievement_category_identifier) {
		this.achievement_category_identifier = achievement_category_identifier;
	}
	public String getAchievement_image_name() {
		return achievement_image_name;
	}
	public void setAchievement_image_name(String achievement_image_name) {
		this.achievement_image_name = achievement_image_name;
	}
	public String getAchievement_name() {
		return achievement_name;
	}
	public void setAchievement_name(String achievement_name) {
		this.achievement_name = achievement_name;
	}
	public String getAchievement_description() {
		return achievement_description;
	}
	public void setAchievement_description(String achievement_description) {
		this.achievement_description = achievement_description;
	}
	public Integer getAchievement_value() {
		return achievement_value;
	}
	public void setAchievement_value(Integer achievement_value) {
		this.achievement_value = achievement_value;
	}
	
}
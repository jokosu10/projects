package com.smsgt.mobile.application.supertsuper_v3.strongloop.model;

import com.strongloop.android.loopback.Model;

public class StrongloopRewardsModel extends Model {
	
	private Integer rewards_id;
	private String rewards_image_name;
	private Integer rewards_equivalent_points;
	
	public Integer getRewards_id() {
		return rewards_id;
	}
	public void setRewards_id(Integer rewards_id) {
		this.rewards_id = rewards_id;
	}
	public String getRewards_image_name() {
		return rewards_image_name;
	}
	public void setRewards_image_name(String rewards_image_name) {
		this.rewards_image_name = rewards_image_name;
	}
	public Integer getRewards_equivalent_points() {
		return rewards_equivalent_points;
	}
	public void setRewards_equivalent_points(Integer rewards_equivalent_points) {
		this.rewards_equivalent_points = rewards_equivalent_points;
	}
}

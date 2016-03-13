package com.smsgt.mobile.application.supertsuper_v3.database.model;

import java.io.Serializable;

public class RewardsModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer rewardsImageId;
	private String rewardImageName;
	private String rewardsItemEquivalentPoints;
	
	public Integer getRewardsImageId() {
		return rewardsImageId;
	}

	public void setRewardsImageId(Integer rewardsImageId) {
		this.rewardsImageId = rewardsImageId;
	}

	public String getRewardImageName() {
		return rewardImageName;
	}

	public void setRewardImageName(String rewardImageName) {
		this.rewardImageName = rewardImageName;
	}

	public String getRewardsItemEquivalentPoints() {
		return rewardsItemEquivalentPoints;
	}

	public void setRewardsItemEquivalentPoints(String rewardsItemEquivalentPoints) {
		this.rewardsItemEquivalentPoints = rewardsItemEquivalentPoints;
	}	
}	 
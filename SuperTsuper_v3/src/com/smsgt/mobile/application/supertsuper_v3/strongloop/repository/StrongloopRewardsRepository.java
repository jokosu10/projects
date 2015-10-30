package com.smsgt.mobile.application.supertsuper_v3.strongloop.repository;

import com.smsgt.mobile.application.supertsuper_v3.strongloop.model.StrongloopRewardsModel;
import com.strongloop.android.loopback.ModelRepository;

public class StrongloopRewardsRepository extends ModelRepository<StrongloopRewardsModel>{

	public StrongloopRewardsRepository() {
		super("Reward", "Rewards", StrongloopRewardsModel.class);	
	}

}

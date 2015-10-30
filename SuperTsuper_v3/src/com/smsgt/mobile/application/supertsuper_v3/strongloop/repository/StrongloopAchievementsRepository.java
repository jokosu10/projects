package com.smsgt.mobile.application.supertsuper_v3.strongloop.repository;

import com.smsgt.mobile.application.supertsuper_v3.strongloop.model.StrongloopAchievementsModel;
import com.strongloop.android.loopback.ModelRepository;

public class StrongloopAchievementsRepository extends ModelRepository<StrongloopAchievementsModel>{

	public StrongloopAchievementsRepository() {
		super("Achievement","Achievements",StrongloopAchievementsModel.class);
	}

}

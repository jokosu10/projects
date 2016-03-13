package com.smsgt.mobile.application.supertsuper_v3.strongloop.repository;

import com.smsgt.mobile.application.supertsuper_v3.strongloop.model.StrongloopTravelInfoModel;
import com.strongloop.android.loopback.ModelRepository;

public class StrongloopTravelInfoRepository extends ModelRepository<StrongloopTravelInfoModel> {

	public StrongloopTravelInfoRepository() {
		super("TravelInfo", "TravelInfos", StrongloopTravelInfoModel.class);
	}

}

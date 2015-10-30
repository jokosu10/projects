package com.smsgt.mobile.application.supertsuper_v3.strongloop.repository;

import com.smsgt.mobile.application.supertsuper_v3.strongloop.model.StrongloopRegistrationModel;
import com.strongloop.android.loopback.ModelRepository;

public class StrongloopRegistrationRepository extends ModelRepository<StrongloopRegistrationModel> {

	public StrongloopRegistrationRepository() {
		super("UserRegistration", "UserRegistrations", StrongloopRegistrationModel.class);
	}	
}

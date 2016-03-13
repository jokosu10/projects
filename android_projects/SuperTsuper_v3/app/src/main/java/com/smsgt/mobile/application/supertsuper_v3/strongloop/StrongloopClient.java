package com.smsgt.mobile.application.supertsuper_v3.strongloop;

import android.content.Context;

import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.remoting.adapters.RestContractItem;

public class StrongloopClient {
		
	private Context context;
	private RestAdapter adapter;
	private String URL;
	
	public StrongloopClient(Context contxt, String url) {
		this.context = contxt;
		this.URL = url;
	}
	
    public RestAdapter getLoopBackAdapter(String transaction, String operation) {
        if (adapter == null) {
            // Instantiate the shared RestAdapter. In most circumstances,
            // you'll do this only once; putting that reference in a singleton
            // is recommended for the sake of simplicity.
            // However, some applications will need to talk to more than one
            // server - create as many Adapters as you need.
            adapter = new RestAdapter(this.context, this.URL);
            adapter.getContract().addItem(
                    new RestContractItem("/" + transaction, operation),
                    transaction);
        }
        return adapter;
    }
    
}

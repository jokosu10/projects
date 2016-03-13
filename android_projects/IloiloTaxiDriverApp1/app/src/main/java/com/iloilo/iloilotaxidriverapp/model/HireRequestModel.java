package com.iloilo.iloilotaxidriverapp.model;

import java.io.Serializable;

public class HireRequestModel implements Serializable {
	
	private String requestId;
    private String address;
    private String gcmRegId;
    
    public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	private static final long serialVersionUID = 1L;
    
    public String getGcmRegId() {
        return gcmRegId;
    }
    
    public void setGcmRegId(String gcmRegId) {
        this.gcmRegId = gcmRegId;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
}

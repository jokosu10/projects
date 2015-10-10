package com.iloilo.iloilotaxidriverapp.model;

import java.io.Serializable;

public class TaxiModel implements Serializable {
	
    private String driverName;
    private String gcmRegistrationId;
    private Integer id;
    private String latitude;
    private String longitude;
    private String plateNumber;
    private String profilePicture;
    private static final long serialVersionUID = 1L;
    private String taxiName;
    private String taxiUserId;
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getTaxiName() {
        return taxiName;
    }
    
    public void setTaxiName(String taxiName) {
        this.taxiName = taxiName;
    }
    
    public String getPlateNumber() {
        return plateNumber;
    }
    
    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }
    
    public String getDriverName() {
        return driverName;
    }
    
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
    
    public String getProfilePicture() {
        return profilePicture;
    }
    
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    
    public String getLongitude() {
        return longitude;
    }
    
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    
    public String getLatitude() {
        return latitude;
    }
    
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    
    public String getTaxiUserId() {
        return taxiUserId;
    }
    
    public void setTaxiUserId(String taxiUserId) {
        this.taxiUserId = taxiUserId;
    }
    
    public String getGcmRegistrationId() {
        return gcmRegistrationId;
    }
    
    public void setGcmRegistrationId(String gcmRegistrationId) {
        this.gcmRegistrationId = gcmRegistrationId;
    }
}

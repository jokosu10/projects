package com.f8mobile.communitymapadmin.model;

import java.io.Serializable;

public class StoreModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private String driverName;
    private Integer id;
    private String latitude;
    private String longitude;
    private String plateNumber;
    private String profilePicture;
    private String taxiName;
    private String taxiUserId;

    public String getDriverName() {
        return driverName;
    }

    public Integer getId() {
        return id;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getTaxiName() {
        return taxiName;
    }

    public String getTaxiUserId() {
        return taxiUserId;
    }

    public void setDriverName(String s) {
        driverName = s;
    }

    public void setId(Integer integer) {
        id = integer;
    }

    public void setLatitude(String s) {
        latitude = s;
    }

    public void setLongitude(String s) {
        longitude = s;
    }

    public void setPlateNumber(String s) {
        plateNumber = s;
    }

    public void setProfilePicture(String s) {
        profilePicture = s;
    }

    public void setTaxiName(String s) {
        taxiName = s;
    }

    public void setTaxiUserId(String s) {
        taxiUserId = s;
    }
}

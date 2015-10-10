package com.f8mobile.communitymapadmin.model;

import java.io.Serializable;

public class UserModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private String firstName;
    private String lastName;
    private String latitude;
    private String longitude;
    private String middleName;
    private String userId;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getUserId() {
        return userId;
    }

    public void setFirstName(String s) {
        firstName = s;
    }

    public void setLastName(String s) {
        lastName = s;
    }

    public void setLatitude(String s) {
        latitude = s;
    }

    public void setLongitude(String s) {
        longitude = s;
    }

    public void setMiddleName(String s) {
        middleName = s;
    }

    public void setUserId(String s) {
        userId = s;
    }
}

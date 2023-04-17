package com.openlab.personalarticle.Models;

public class UserModel {

    private String userId;
    private String username;
    private String profile;
    private double locLong;
    private double locLat;
    private String address;
    private String mobileNo;

    public UserModel(String userId, String username, String profile, double locLong, double locLat, String address, String mobileNo) {
        this.userId = userId;
        this.username = username;
        this.profile = profile;
        this.locLong = locLong;
        this.locLat = locLat;
        this.address = address;
        this.mobileNo = mobileNo;
    }

    public UserModel() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public double getLocLong() {
        return locLong;
    }

    public void setLocLong(long locLong) {
        this.locLong = locLong;
    }

    public double getLocLat() {
        return locLat;
    }

    public void setLocLat(long locLat) {
        this.locLat = locLat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }
}

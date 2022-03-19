package com.dharma.shopinar.models;

public class UserInfoModel {
    String userID;
    String name;
    String email;
    String phone;
    String profile;

    public UserInfoModel(String userID, String name, String email, String phone, String profile) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.profile = profile;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public UserInfoModel() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}

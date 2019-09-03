package com.lovelycoding.whatapp.model;

public class Contact {
    String userName,userStatus,userProfileUrl;

    public Contact() {
        userName="";
        userProfileUrl="";
        userStatus="";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserProfileUrl() {
        return userProfileUrl;
    }

    public void setUserProfileUrl(String userProfileUrl) {
        this.userProfileUrl = userProfileUrl;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "userName='" + userName + '\'' +
                ", userStatus='" + userStatus + '\'' +
                ", userProfileUrl='" + userProfileUrl + '\'' +
                '}';
    }
}

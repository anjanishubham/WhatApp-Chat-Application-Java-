package com.lovelycoding.whatapp.model;

public class GroupChatModel {

    private String userMassage,messageDate,messageTime,userName;

    @Override
    public String toString() {
        return "GroupChatModel{" +
                "userMassage='" + userMassage + '\'' +
                ", messageDate='" + messageDate + '\'' +
                ", messageTime='" + messageTime + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }

    public String getUserMassage() {
        return userMassage;
    }

    public void setUserMassage(String userMassage) {
        this.userMassage = userMassage;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


}

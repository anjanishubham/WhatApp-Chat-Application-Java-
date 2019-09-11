package com.lovelycoding.whatapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PrivateChatMessage implements Parcelable {

    public PrivateChatMessage() {

    }

    private String message,messageFrom,messageType, timeStamp;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public static Creator<PrivateChatMessage> getCREATOR() {
        return CREATOR;
    }

    protected PrivateChatMessage(Parcel in) {
        message = in.readString();
        messageFrom = in.readString();
        messageType = in.readString();
        timeStamp = in.readString();
    }

    public static final Creator<PrivateChatMessage> CREATOR = new Creator<PrivateChatMessage>() {
        @Override
        public PrivateChatMessage createFromParcel(Parcel in) {
            return new PrivateChatMessage(in);
        }

        @Override
        public PrivateChatMessage[] newArray(int size) {
            return new PrivateChatMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(message);
        parcel.writeString(messageFrom);
        parcel.writeString(messageType);
        parcel.writeString(timeStamp);
    }

    @Override
    public String toString() {
        return "PrivateChatMessage{" +
                "message='" + message + '\'' +
                ", messageFrom='" + messageFrom + '\'' +
                ", messageType='" + messageType + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}

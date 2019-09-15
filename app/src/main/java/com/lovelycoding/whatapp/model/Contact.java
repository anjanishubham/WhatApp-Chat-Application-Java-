package com.lovelycoding.whatapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Contact implements Parcelable {
    String name, status, image, uid,device_token,last_online_time,last_online_date,login_state;

    public Contact() {
        name ="";
        image ="";
        status ="";
        uid ="";
        device_token="";
        last_online_time="";
        last_online_date="";
        login_state="";
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", image='" + image + '\'' +
                ", uid='" + uid + '\'' +
                ", device_token='" + device_token + '\'' +
                ", last_online_time='" + last_online_time + '\'' +
                ", last_online_date='" + last_online_date + '\'' +
                ", login_state='" + login_state + '\'' +
                '}';
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("last_online_time", last_online_time);
        result.put("last_online_date",last_online_date);
        result.put("login_state",login_state);


        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getLast_online_time() {
        return last_online_time;
    }

    public void setLast_online_time(String last_online_time) {
        this.last_online_time = last_online_time;
    }

    public String getLast_online_date() {
        return last_online_date;
    }

    public void setLast_online_date(String last_online_date) {
        this.last_online_date = last_online_date;
    }

    public String getLogin_state() {
        return login_state;
    }

    public void setLogin_state(String login_state) {
        this.login_state = login_state;
    }

    public static Creator<Contact> getCREATOR() {
        return CREATOR;
    }

    protected Contact(Parcel in) {
        name = in.readString();
        status = in.readString();
        image = in.readString();
        uid = in.readString();
        device_token = in.readString();
        last_online_time = in.readString();
        last_online_date = in.readString();
        login_state = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(status);
        parcel.writeString(image);
        parcel.writeString(uid);
        parcel.writeString(device_token);
        parcel.writeString(last_online_time);
        parcel.writeString(last_online_date);
        parcel.writeString(login_state);
    }
}

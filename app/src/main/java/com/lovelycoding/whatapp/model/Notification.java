package com.lovelycoding.whatapp.model;

public class Notification {
    String from,type;

    public Notification() {
        from="";
        type="";
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

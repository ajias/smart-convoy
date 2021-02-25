package com.example.munsan.l;

public class Messages {
    private String  username, message, date, time,userID;

    public Messages(){

    }

    public Messages(String username, String message, String date, String time, String userID) {
        this.username = username;
        this.message = message;
        this.date = date;
        this.time = time;
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}

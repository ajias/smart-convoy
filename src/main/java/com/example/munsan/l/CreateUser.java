package com.example.munsan.l;

public class CreateUser {
    public CreateUser(){

    }

    public String name;

    public CreateUser(String name, String email, String password, String code, String isSharing, String lat, String lng, String imageUrl, String userId, String joincode, String usingCode) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.code = code;
        this.isSharing = isSharing;
        this.lat = lat;
        this.lng = lng;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.joincode = joincode;
        this.usingCode = usingCode;
    }

    public String email;
    public String password;
    public String code;
    public String isSharing;
    public String lat;
    public String lng;
    public String imageUrl;
    public String userId;
    public String joincode;
    public String usingCode;


}


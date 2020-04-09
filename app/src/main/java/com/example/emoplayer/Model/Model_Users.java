package com.example.emoplayer.Model;

public class Model_Users {

    String uid;
    String email;
    String userName;
    String name;
    String gender;
    String country;

    public Model_Users(){

    }

    public Model_Users(String uid, String email, String userName, String name, String gender, String country) {
        this.uid = uid;
        this.email = email;
        this.userName = userName;
        this.name = name;
        this.gender = gender;
        this.country = country;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}

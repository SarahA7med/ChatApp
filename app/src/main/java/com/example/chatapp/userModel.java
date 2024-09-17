package com.example.chatapp;

public class userModel {
    String id;
    String username;
    String email;
    String password;
    String profilePhotoUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public userModel() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public userModel(String id,String username,String email,String password,    String profilePhotoUrl) {
        this.email = email;
        this.id=id;
        this.password=password;
        this.username=username;
        this.profilePhotoUrl = profilePhotoUrl;

    }
}

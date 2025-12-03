package com.example.smartly.model;

public class UserProfile {
    public String username;
    public String email;
    public String avatar; // emoji string

    public UserProfile() {}

    public UserProfile(String username, String email, String avatar) {
        this.username = username;
        this.email = email;
        this.avatar = avatar;
    }
}

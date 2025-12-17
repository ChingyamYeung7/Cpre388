package com.example.smartly.model;

public class UserProfile {
    // Making these private with getters/setters is safer for Firestore
    public String username;
    public String email;
    public String avatarName; // Stores filename like "cool_pepe"

    // Empty constructor required for Firestore
    public UserProfile() {}

    public UserProfile(String username, String email, String avatarName) {
        this.username = username;
        this.email = email;
        this.avatarName = avatarName;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getAvatarName() { return avatarName; }
}
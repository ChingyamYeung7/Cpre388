package com.example.smartly;

public class User {
    private String username;
    private String email;
    private boolean isAdmin;

    // Empty Constructor (REQUIRED for Firestore)
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    // Normal Constructor (For when we create users in our code)
    public User(String username, String email, boolean isAdmin) {
        this.username = username;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    // Getters
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public boolean isAdmin() { return isAdmin; }
}
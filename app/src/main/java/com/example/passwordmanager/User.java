package com.example.passwordmanager;

public class User {
    private final int id;
    private final String  email;

    public User(int id, String email) {
        this.id = id;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}

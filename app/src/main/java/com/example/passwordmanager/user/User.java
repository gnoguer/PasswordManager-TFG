package com.example.passwordmanager.user;

import javax.crypto.SecretKey;

public class User {
    private final int id;
    private final String  email;
    private final String secret;

    public User(int id, String email, String secret) {
        this.id = id;
        this.email = email;
        this.secret = secret;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getSecret() { return secret; }
}

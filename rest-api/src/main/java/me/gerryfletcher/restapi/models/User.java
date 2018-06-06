package me.gerryfletcher.restapi.models;

import java.security.Principal;

public class User implements Principal {

    private String username;
    private String role;

    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }

    @Override
    public String getName() {
        return this.username;
    }

    public String getRole() {
        return this.role;
    }

}

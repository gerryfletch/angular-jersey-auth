package me.gerryfletcher.restapi.models;

import me.gerryfletcher.restapi.authentication.Role;

import java.security.Principal;

public class User implements Principal {

    private String username;
    private Role role;

    public User(String username, Role role) {
        this.username = username;
        this.role = role;
    }

    @Override
    public String getName() {
        return this.username;
    }

    public Role getRole() {
        return this.role;
    }

}

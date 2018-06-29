package me.gerryfletcher.restapi.permissions;

import java.util.Date;

public class UserPermission {
    private String username;
    private PermissionAction action;
    private Date issuedAt;
    private String description;

    public UserPermission(String username, PermissionAction action) {
        this.username = username;
        this.action = action;
        this.issuedAt = new Date();
    }

    public UserPermission(String username, PermissionAction action, String description) {
        this(username, action);
        this.description = description;
    }

    public String getUsername() {
        return this.username;
    }

    public PermissionAction getAction() {
        return action;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public String getDescription() {
        return description;
    }
}
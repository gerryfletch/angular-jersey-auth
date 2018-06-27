package me.gerryfletcher.restapi.permissions;

import java.util.Date;

public class UserPermissions {

    private String username;
    private PermissionAction action;
    private Date issuedAt;

    private String description;

    public UserPermissions(String username, PermissionAction action, Date issuedAt) {
        this.username = username;
        this.action = action;
        this.issuedAt = issuedAt;
    }

    public UserPermissions(String username, PermissionAction action, Date issuedAt, String description) {
        this(username, action, issuedAt);
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public PermissionAction getAction() {
        return action;
    }

    public Date issuedAt() {
        return issuedAt;
    }

    public String getDescription() {
        return (description != null) ? description : "";
    }
}

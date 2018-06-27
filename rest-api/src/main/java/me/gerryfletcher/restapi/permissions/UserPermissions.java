package me.gerryfletcher.restapi.permissions;

import java.util.Date;

public class UserPermissions {

    private String username;
    private PermissionAction action;
    private Date timestamp;

    private String description;

    public UserPermissions(String username, PermissionAction action, Date timestamp) {
        this.username = username;
        this.action = action;
        this.timestamp = timestamp;
    }

    public UserPermissions(String username, PermissionAction action, Date timestamp, String description) {
        this(username, action, timestamp);
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public PermissionAction getAction() {
        return action;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return (description != null) ? description : "";
    }
}

package me.gerryfletcher.restapi.permissions;

import java.util.Date;

public class Permission {
    private PermissionAction action;
    private Date issuedAt;
    private String description;

    public Permission(PermissionAction action, Date issuedAt) {
        this.action = action;
        this.issuedAt = issuedAt;
    }

    public Permission(PermissionAction action, Date issuedAt, String description) {
        this(action, issuedAt);
        this.description = description;
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
package me.gerryfletcher.restapi.permissions;

import me.gerryfletcher.restapi.authentication.Role;

import java.util.function.Function;

public enum PermissionAction {
    // Make a user -> admin
    PROMOTE(userInfo -> userInfo.setRole(Role.ADMIN)),
    // Make a admin -> user
    DEMOTE(userInfo -> userInfo.setRole(Role.USER)),
    // Clear a user of a revokation
    CLEAR(userInfo -> userInfo.clear()),
    // Revoke a users account
    REVOKE(userInfo -> userInfo.setRevoked(true)),
    // Log a user out
    LOGOUT(userInfo -> userInfo.setLoggedIn(false));

    private Function<UserInfo, UserInfo> action;

    PermissionAction(Function<UserInfo, UserInfo> action) {
        this.action = action;
    }

    public UserInfo applyTo(UserInfo userInfo) {
        return action.apply(userInfo);
    }

}

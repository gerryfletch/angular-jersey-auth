package me.gerryfletcher.restapi.data;

public enum PermissionAction {
    // Make a user -> admin
    PROMOTE,
    // Make a admin -> user
    DEMOTE,
    // Clear a user of a revokation
    CLEAR,
    // Revoke a users account
    REVOKE,
    // Log a user out
    LOGOUT
}

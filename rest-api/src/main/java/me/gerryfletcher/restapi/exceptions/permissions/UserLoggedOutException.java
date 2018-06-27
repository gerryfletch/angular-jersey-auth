package me.gerryfletcher.restapi.exceptions.permissions;

public class UserLoggedOutException extends PermissionException {
    public UserLoggedOutException() {
        super("Your session has ended. Please re-enter your credentials to log back in.");
    }
}

package me.gerryfletcher.restapi.exceptions.permissions;

public class UserRevokedException extends PermissionException {
    public UserRevokedException() {
        super("This account has been revoked. Contact an administrator for further assistance.");
    }
}

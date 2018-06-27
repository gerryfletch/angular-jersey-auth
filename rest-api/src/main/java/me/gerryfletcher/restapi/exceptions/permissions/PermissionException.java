package me.gerryfletcher.restapi.exceptions.permissions;

public class PermissionException extends RuntimeException {
    PermissionException(String message) {
        super(message);
    }
}
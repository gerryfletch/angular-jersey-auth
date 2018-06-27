package me.gerryfletcher.restapi.exceptions;

public class UserRevokedException extends Exception {
    public UserRevokedException() { super("This account has been revoked. Contact an administrator for further assistance."); }
}

package me.gerryfletcher.restapi.exceptions;

public class InvalidLoginException extends Exception {
    public InvalidLoginException() {
        super("Your login details were incorrect.");
    }

    public InvalidLoginException(String message) {
        super(message);
    }
}

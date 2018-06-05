package me.gerryfletcher.restapi.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class InvalidLoginException extends Exception implements ExceptionMapper<InvalidLoginException> {
    public InvalidLoginException() {
        super("Your login details were incorrect.");
    }

    public InvalidLoginException(String message) {
        super(message);
    }

    @Override
    public Response toResponse(InvalidLoginException e) {
        return Response
                .status(Response.Status.UNAUTHORIZED)
                .entity(e.getMessage())
                .build();
    }
}

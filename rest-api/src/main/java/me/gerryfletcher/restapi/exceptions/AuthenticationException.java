package me.gerryfletcher.restapi.exceptions;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * An authentication exception that returns a 401 Unauthorized.
 */
public class AuthenticationException extends Exception implements ExceptionMapper<ForbiddenException> {

    public AuthenticationException() {
        super("You do not have the correct permissions for this resource.");
    }

    public AuthenticationException(String message) {
        super(message);
    }

    @Override
    public Response toResponse(ForbiddenException e) {
        return Response
                .status(Response.Status.UNAUTHORIZED)
                .entity(e.getMessage())
                .type("text/plain")
                .build();
    }
}

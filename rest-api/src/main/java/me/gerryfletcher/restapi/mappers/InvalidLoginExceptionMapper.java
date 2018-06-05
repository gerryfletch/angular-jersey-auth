package me.gerryfletcher.restapi.mappers;

import me.gerryfletcher.restapi.exceptions.InvalidLoginException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * If a bad login occurs, generate a 400 Bad Request response.
 */
@Provider
public class InvalidLoginExceptionMapper implements ExceptionMapper<InvalidLoginException> {

    @Override
    public Response toResponse(InvalidLoginException e) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .build();
    }
}

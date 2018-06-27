package me.gerryfletcher.restapi.mappers;

import me.gerryfletcher.restapi.exceptions.UserRevokedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class UserRevokedExceptionMapper implements ExceptionMapper<UserRevokedException> {
    @Override
    public Response toResponse(UserRevokedException exception) {
        int status = Response.Status.UNAUTHORIZED.getStatusCode();
        return Response.status(status, exception.getMessage()).build();
    }
}

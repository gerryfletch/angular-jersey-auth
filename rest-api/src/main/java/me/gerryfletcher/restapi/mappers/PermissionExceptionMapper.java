package me.gerryfletcher.restapi.mappers;

import me.gerryfletcher.restapi.exceptions.permissions.PermissionException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class PermissionExceptionMapper implements ExceptionMapper<PermissionException> {
    @Override
    public Response toResponse(PermissionException exception) {
        int status = Response.Status.UNAUTHORIZED.getStatusCode();
        return Response.status(status, exception.getMessage()).build();
    }
}

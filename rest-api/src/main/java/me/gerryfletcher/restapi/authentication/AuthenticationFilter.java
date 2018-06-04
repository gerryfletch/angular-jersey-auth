package me.gerryfletcher.restapi.authentication;

import com.auth0.jwt.interfaces.DecodedJWT;
import me.gerryfletcher.restapi.exceptions.AuthenticationException;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AuthenticationFilter implements ContainerRequestFilter {

    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHORIZATION_TYPE     = "Bearer";

    private static final Response UNAUTHORIZED = Response.status(Response.Status.UNAUTHORIZED).build();
    private static final Response FORBIDDEN    = Response.status(Response.Status.FORBIDDEN).build();

    private static final TokenService tokenService = new TokenService();

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        Method method = this.resourceInfo.getResourceMethod();

        if (method.isAnnotationPresent(PermitAll.class)) {
            return;
        } else if (method.isAnnotationPresent(DenyAll.class)) {
            requestContext.abortWith(FORBIDDEN);
            return;
        }

        List<String> authHeader = requestContext.getHeaders().getOrDefault(AUTHORIZATION_PROPERTY, new ArrayList<>());

        // Validate token and check its role against end point annotation.
        try {
            DecodedJWT token = tokenService.getDecodedJWT(getToken(authHeader));
            String role = token.getClaim("role").asString();

            String[] annotationRoles = method.getAnnotation(RolesAllowed.class).value();

            for (String annotationRole: annotationRoles) {

                // The users role matches the allowed roles.
                if (annotationRole.equalsIgnoreCase(role)) {
                    return;
                }
            }

        } catch (AuthenticationException e) {
            requestContext.abortWith(UNAUTHORIZED);
        }

    }

    private String getToken(List<String> authHeader) throws AuthenticationException {
        // If there is no authorization present; block access.
        if (authHeader.isEmpty()) {
            throw new AuthenticationException("No authorization provided.");
        }

        String[] authMakeup = authHeader.get(0).split(" ");

        if (authMakeup.length != 2) {
            throw new AuthenticationException("Invalid authorization syntax.");
        }

        String authType = authMakeup[0];
        String token = authMakeup[1];

        if (! authType.equals(AUTHORIZATION_TYPE)) {
            throw new AuthenticationException("Incorrect form of authorization.");
        }

        return token;
    }

}

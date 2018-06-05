package me.gerryfletcher.restapi.authentication;

import com.auth0.jwt.interfaces.DecodedJWT;
import me.gerryfletcher.restapi.exceptions.AuthenticationException;

import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;

@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHORIZATION_TYPE     = "BEARER";

    private static final TokenService tokenService = new TokenService();

    @Context
    private ResourceInfo resourceInfo;

    /**
     * Authenticates and filters incoming requests.
     * @param requestContext The request.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        Method method = this.resourceInfo.getResourceMethod();

        if (method.isAnnotationPresent(PermitAll.class)) {
            return;
        } else if (method.isAnnotationPresent(DenyAll.class)) {
            forbidden(requestContext );
            return;
        }

        String auth = requestContext.getHeaderString(AUTHORIZATION_PROPERTY);

        try {
            DecodedJWT token = tokenService.getDecodedJWT(getToken(auth));
            String[] roles = method.getAnnotation(RolesAllowed.class).value();

            if (! tokenIsValidRole(token, roles)) {
                forbidden(requestContext);
            }

        } catch (AuthenticationException e) {
            unauthorized(requestContext, e.getMessage());
        }

    }

    /**
     * Checks for Bearer authentication before returning the token.
     * @param authHeader Header in the form "Bearer xxx.yyy.zzz".
     * @return The token part of the header.
     * @throws AuthenticationException If there is no bearer auth type.
     */
    private String getToken(String authHeader) throws AuthenticationException {
        if (authHeader == null || authHeader.isEmpty()) throw new AuthenticationException("No auth provided.");
        // Check that it is bearer auth.
        if (! authHeader.toUpperCase().startsWith(AUTHORIZATION_TYPE)) {
            throw new AuthenticationException("Incorrect authorization type.");
        }

        return authHeader.split(" ")[1];
    }

    /**
     * @return True if a tokens role is permitted.
     */
    private boolean tokenIsValidRole(DecodedJWT token, String[] permittedRoles) {
        String userRole = token.getClaim("role").asString();
        for (String role: permittedRoles) if (role.equals(userRole)) return true;
        return false;
    }

    /**
     * Set the context to abort with 401 Unauthorized.
     */
    private void unauthorized(ContainerRequestContext context, String message) {
        context.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(message).build());
    }

    /**
     * Set the context to abort with 403 Forbidden.
     */
    private void forbidden(ContainerRequestContext context) {
        context.abortWith(Response.status(Response.Status.FORBIDDEN)
                .entity("You can't access this resource :(")
                .build());
    }

}

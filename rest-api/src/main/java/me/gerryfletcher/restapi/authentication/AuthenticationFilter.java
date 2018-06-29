package me.gerryfletcher.restapi.authentication;

import com.auth0.jwt.interfaces.DecodedJWT;
import me.gerryfletcher.restapi.config.Secured;
import me.gerryfletcher.restapi.exceptions.AuthenticationException;
import me.gerryfletcher.restapi.models.User;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final String AUTHORIZATION_PROPERTY = "authorization";
    private static final String AUTHORIZATION_TYPE = "Bearer";

    private static final TokenService tokenService = new TokenService();

    @Context
    private ResourceInfo resourceInfo;

    /**
     * Authenticates and filters incoming requests.
     *
     * @param requestContext The request.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        Class<?> resourceClass = resourceInfo.getResourceClass();
        List<Role> classRoles = extractRoles(resourceClass);

        Method method = this.resourceInfo.getResourceMethod();
        List<Role> methodRoles = extractRoles(method);

        // Permit all users.
        if (classRoles.isEmpty() && methodRoles.isEmpty()) {
            return;
        }

        String auth = requestContext.getHeaderString(AUTHORIZATION_PROPERTY);
        DecodedJWT token;

        try {
            token = tokenService.getDecodedJWT(getToken(auth));
        } catch (Exception e) {
            unauthorized(requestContext, e.getMessage());
            return;
        }

        // Gather security context data.
        String scheme = requestContext.getUriInfo().getRequestUri().getScheme();
        String username = token.getClaim("username").asString();
        Role role = Role.valueOf(token.getClaim("role").asString());

        try {
            if (methodRoles.isEmpty()) {
                isRoleValid(role, classRoles);
            } else {
                isRoleValid(role, methodRoles);
            }
        } catch (AuthenticationException e) {
            forbidden(requestContext);
        }

        User user = new User(username, role);
        requestContext.setSecurityContext(new UserSecurityContext(user, scheme, token));
    }

    /**
     * Checks for Bearer authentication before returning the token.
     *
     * @param authHeader Header in the form "Bearer xxx.yyy.zzz".
     * @return The token part of the header.
     * @throws AuthenticationException If there is no bearer auth type.
     */
    private String getToken(String authHeader) throws AuthenticationException {
        if (authHeader == null || authHeader.isEmpty()) throw new AuthenticationException("No auth provided.");
        // Check that it is bearer auth.
        if (!authHeader.toUpperCase().startsWith(AUTHORIZATION_TYPE.toUpperCase())) {
            throw new AuthenticationException("Incorrect authorization type.");
        }

        return authHeader.split(" ")[1];
    }

    private void isRoleValid(Role role, List<Role> permittedRoles) throws AuthenticationException {
        if (! permittedRoles.contains(role)) {
            throw new AuthenticationException("You don't have permission for this resource.");
        }
    }

    private List<Role> extractRoles(AnnotatedElement annotatedElement) {
        if (annotatedElement == null) {
            return new ArrayList<>();
        } else {
            Secured secured = annotatedElement.getAnnotation(Secured.class);
            if (secured == null) {
                return new ArrayList<>();
            } else {
                Role[] allowedRoles = secured.value();
                return Arrays.asList(allowedRoles);
            }
        }
    }

    /**
     * Set the context to abort with 401 Unauthorized.
     */
    private void unauthorized(ContainerRequestContext context, String message) {
        int code = Response.Status.UNAUTHORIZED.getStatusCode();
        context.abortWith(Response.status(code, message).build());
    }

    /**
     * Set the context to abort with 403 Forbidden.
     */
    private void forbidden(ContainerRequestContext context) {
        int code = Response.Status.FORBIDDEN.getStatusCode();
        String text = "You don't have permission to access this resource.";
        context.abortWith(Response.status(code, text).build());
    }

}

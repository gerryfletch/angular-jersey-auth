package me.gerryfletcher.restapi.authentication;

import com.auth0.jwt.interfaces.DecodedJWT;
import me.gerryfletcher.restapi.models.User;

import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class UserSecurityContext implements SecurityContext {

    private final String scheme;
    private final User user;

    private final DecodedJWT token;

    UserSecurityContext(User user, String scheme, DecodedJWT token) {
        this.user = user;
        this.scheme = scheme;
        this.token = token;
    }

    @Override
    public User getUserPrincipal() {
        return this.user;
    }

    @Override
    public boolean isUserInRole(String role) {
        Role r = Role.valueOf(role.toUpperCase());
        return user.getRole() == r;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String getAuthenticationScheme() {
        return this.scheme;
    }

    public DecodedJWT getToken() {
        return this.token;
    }
}

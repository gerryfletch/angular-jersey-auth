package me.gerryfletcher.restapi.permissions;

import com.auth0.jwt.interfaces.DecodedJWT;
import me.gerryfletcher.restapi.authentication.Role;

/**
 * WIP. A different permission system.
 */
public class UserInfo {
    private String username;
    private boolean loggedIn = true;
    private boolean revoked = false;
    Role role = Role.USER;

    public UserInfo fromAccessToken(DecodedJWT token) {
        this.username = token.getClaim("username").asString();
        return this;
    }

    public UserInfo setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
        return this;
    }

    public UserInfo setRole(Role role) {
        this.role = role;
        return this;
    }

}

package me.gerryfletcher.restapi.permissions;

import com.auth0.jwt.interfaces.DecodedJWT;
import me.gerryfletcher.restapi.authentication.Role;

import java.util.Date;

/**
 * WIP. A different permission system.
 */
public class UserInfo {
    private String username;
    private boolean isLoggedIn = true;
    private boolean isRevoked = false;
    private Role role = Role.USER;
    private Date tokenIssuedAt;

    public UserInfo fromAccessToken(DecodedJWT token) {
        this.username = token.getClaim("username").asString();
        this.role = Role.valueOf(token.getClaim("role").asString().toUpperCase());
        this.tokenIssuedAt = token.getIssuedAt();
        return this;
    }
    
    public UserInfo clear() {
        this.isLoggedIn = true;
        this.isRevoked = false;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Date getTokenIssueDate() {
        return this.tokenIssuedAt;
    }

    public boolean isLoggedIn() {
        return this.isLoggedIn;
    }

    public boolean isRevoked() {
        return this.isRevoked;
    }

    public Role getRole() {
        return this.role;
    }

    public UserInfo setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
        return this;
    }

    public UserInfo setRevoked(boolean isRevoked) {
        this.isRevoked = isRevoked;
        return this;
    }

    public UserInfo setRole(Role role) {
        this.role = role;
        return this;
    }

}

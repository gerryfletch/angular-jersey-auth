package me.gerryfletcher.restapi.authentication;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import me.gerryfletcher.restapi.exceptions.AuthenticationException;
import me.gerryfletcher.restapi.exceptions.InvalidLoginException;
import me.gerryfletcher.restapi.models.AuthTokens;
import me.gerryfletcher.restapi.permissions.PermissionService;
import me.gerryfletcher.restapi.permissions.UserPermissions;

public class AuthenticationService {

    private TokenService tokenService;
    private PermissionService permissionService;

    public AuthenticationService() {
        this.tokenService = new TokenService();
        this.permissionService = new PermissionService();
    }

    /**
     * Check if the user has the correct credentials.
     * This would be done using a database and hashed + salted passwords.
     * <b>This is purely an example.</b>
     * @return The newly created Auth Tokens.
     * @throws InvalidLoginException The username and password combination is incorrect.
     */
    public AuthTokens login(String username, String password) throws InvalidLoginException, JWTCreationException {
        if (username.equals("admin") && password.equals("password")) {
            return tokenService.createAuthTokens(username, Role.ADMIN);
        } else if (username.equals("user") && password.equals("password")) {
            return tokenService.createAuthTokens(username, Role.USER);
        } else {
            throw new InvalidLoginException();
        }
    }

    /**
     * Checks if a user is permitted to refresh a token.
     * @return A new access token with the users details.
     */
    public String refreshAccessToken(String username, String role, DecodedJWT token) throws JWTCreationException, AuthenticationException {
        if (token == null) throw new AuthenticationException();
        UserPermissions userPermissions = permissionService.getUserPermissions(username);
        return tokenService.createAccessToken(username, role);
    }

}

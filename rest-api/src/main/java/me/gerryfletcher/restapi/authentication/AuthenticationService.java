package me.gerryfletcher.restapi.authentication;

import com.auth0.jwt.interfaces.DecodedJWT;
import me.gerryfletcher.restapi.exceptions.InvalidLoginException;
import me.gerryfletcher.restapi.exceptions.permissions.UserLoggedOutException;
import me.gerryfletcher.restapi.exceptions.permissions.UserRevokedException;
import me.gerryfletcher.restapi.models.AuthTokens;
import me.gerryfletcher.restapi.permissions.*;

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
     *
     * @return The newly created Auth Tokens.
     * @throws InvalidLoginException The username and password combination is incorrect.
     */
    public AuthTokens login(String username, String password) {
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
     * <b>Important:</b> If a user is affected by permissions, it is only affected for the duration of the access
     * tokens lifetime. This is so that we can demonstrate the permissions working easily.
     *
     * @return A new access token with the users details.
     */
    public String refreshAccessToken(DecodedJWT token) {
        UserInfo userInfo = new UserInfo().fromAccessToken(token);
        userInfo = permissionService.updateUserPermissions(userInfo);

        if (! userInfo.isLoggedIn()) throw new UserLoggedOutException();
        if (userInfo.isRevoked()) throw new UserRevokedException();

        return tokenService.createAccessToken(userInfo.getUsername(), userInfo.getRole());
    }

}

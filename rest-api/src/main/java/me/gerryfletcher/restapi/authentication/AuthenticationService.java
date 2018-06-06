package me.gerryfletcher.restapi.authentication;

import com.auth0.jwt.exceptions.JWTCreationException;
import me.gerryfletcher.restapi.exceptions.AuthenticationException;
import me.gerryfletcher.restapi.exceptions.InvalidLoginException;
import me.gerryfletcher.restapi.models.AuthTokens;

public class AuthenticationService {

    private TokenService tokenService;

    public AuthenticationService() {
        this.tokenService = new TokenService();
    }

    /**
     * Check if the user has the correct credentials.
     * This would be done using a database and hashed + salted passwords.
     * <b>This is purely an example.</b>
     * @return The newly created Auth Tokens.
     * @throws InvalidLoginException The username and password combination is incorrect.
     * @throws AuthenticationException If the tokens cannot be created.
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

    public AuthTokens refreshTokens(String username, String role) throws JWTCreationException {
        return tokenService.createAuthTokens(username, role);
    }

}
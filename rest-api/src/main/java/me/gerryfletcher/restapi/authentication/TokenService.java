package me.gerryfletcher.restapi.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import me.gerryfletcher.restapi.exceptions.AuthenticationException;
import me.gerryfletcher.restapi.models.AuthTokens;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TokenService {

    private static final String secret = "test_secret";
    private static Algorithm algorithm;
    private static JWTVerifier verifier;

    public TokenService() {
        try {
            algorithm = Algorithm.HMAC256(secret);
            verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.err.println("There was a problem creating the JWT verifier.");
        }
    }

    /**
     * @param token The encoded token.
     * @return The verified and decoded token.
     * @throws AuthenticationException If the token is invalid.
     */
    DecodedJWT getDecodedJWT(String token) throws AuthenticationException {
        try {
            return verifier.verify(token);
        } catch (JWTVerificationException exception) {
            throw new AuthenticationException("Invalid token.");
        }
    }

    /**
     * @param username The user whom this is for.
     * @param role  The role this user holds.
     * @return  A valid access token.
     * @throws JWTCreationException If the signature configuration is invalid.
     */
    String createAccessToken(String username, String role) throws JWTCreationException {
        return JWT.create()
                .withIssuer("auth0")
                .withClaim("username", username)
                .withClaim("role", role)
//                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(7)))
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(3)))
                .sign(algorithm);
    }

    /**
     * Create a refresh token with an expiration date of one year.
     * This means that a secure user with the refresh token can retrieve an access token for up to a year.
     * @param username The user whom this is for.
     * @return A valid refresh token.
     * @throws JWTCreationException If the signature configuration is invalid.
     */
    String createRefreshToken(String username) throws JWTCreationException {
        return JWT.create()
                .withIssuer("auth0")
                .withClaim("username", username)
                .withClaim("role", Role.REFRESH)
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365)))
                .sign(algorithm);
    }

    AuthTokens createAuthTokens(String username, String role) throws JWTCreationException {
        String accessToken = createAccessToken(username, role);
        String refreshToken = createRefreshToken(username);
        return new AuthTokens(refreshToken, accessToken);
    }

}

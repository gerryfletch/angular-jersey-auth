package me.gerryfletcher.restapi.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import me.gerryfletcher.restapi.exceptions.AuthenticationException;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TokenService {

    private static final String secret = "test_secret";
    private static Algorithm algorithm;
    private static JWTVerifier verifier;

    TokenService() {
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
    public DecodedJWT getDecodedJWT(String token) throws AuthenticationException {
        try {
            return verifier.verify(token);
        } catch (JWTVerificationException exception) {
            throw new AuthenticationException("Invalid token.");
        }
    }

    /**
     * @param uid   The user whom this is for.
     * @param role  The role this user holds.
     * @return  A valid JSON Web Token.
     * @throws AuthenticationException
     */
    public String createAccessToken(int uid, String role) throws AuthenticationException {
        try {
            return JWT.create()
                    .withIssuer("auth0")
                    .withClaim("uid", uid)
                    .withClaim("role", role)
                    .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(7)))
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new AuthenticationException("Invalid signature configuration.");
        }
    }


}

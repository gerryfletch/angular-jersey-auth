package me.gerryfletcher.restapi.models;

public class AuthTokens {

    private final String accessToken;
    private final String refreshToken;

    public AuthTokens(String refreshToken, String accessToken) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}


package com.nt118.foodsellingapp.dto;

public class AuthResponse {
    private String token;
    private String refreshToken;

    public AuthResponse() {}
    public AuthResponse(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public String getToken() { return token; }
    public void setToken(String accessToken) { this.token = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
package com.example.hiring.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class LogoutRequest {
    @NotBlank
    private String refreshToken;

    public LogoutRequest() {}

    public LogoutRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}

package com.example.hiring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/oauth2")
    public Map<String, Object> testOAuth2() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Click the link below to test Google OAuth2");
        response.put("oauth2_url", "http://localhost:8080/api/oauth2/authorization/google");
        response.put("instructions", new String[]{
                "1. Click the oauth2_url",
                "2. Sign in with Google",
                "3. You should see a JSON response with tokens",
                "4. Copy the accessToken from the response",
                "5. Test protected endpoint: GET /api/auth/me with Authorization: Bearer {accessToken}"
        });
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "Backend is running");
        status.put("timestamp", LocalDateTime.now());
        status.put("oauth2_test_url", "http://localhost:8080/api/test/oauth2");
        status.put("oauth2_login_url", "http://localhost:8080/api/oauth2/authorization/google");
        status.put("endpoints", new String[]{
                "GET /api/test/status - Check backend status",
                "GET /api/test/oauth2 - OAuth2 test instructions",
                "GET /api/oauth2/authorization/google - Start OAuth2 flow",
                "POST /api/auth/register - Register new user",
                "POST /api/auth/login - Login with email/password",
                "GET /api/auth/me - Get current user (requires token)"
        });
        return status;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", "auth-service");
        health.put("timestamp", LocalDateTime.now());
        return health;
    }
}
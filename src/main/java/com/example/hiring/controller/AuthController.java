package com.example.hiring.controller;

import com.example.hiring.dto.auth.*;
import com.example.hiring.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                              HttpServletResponse response) {
        AuthResponse authResponse = authService.login(loginRequest);

        // Set secure cookies
        addTokenCookie(response, "accessToken", authResponse.getAccessToken(), 24 * 60 * 60);
        addTokenCookie(response, "refreshToken", authResponse.getRefreshToken(), 7 * 24 * 60 * 60);

        log.info("User logged in successfully: {}", loginRequest.getEmail());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody SignUpRequest signUpRequest,
                                                 HttpServletResponse response) {
        AuthResponse authResponse = authService.register(signUpRequest);

        // Set secure cookies
        addTokenCookie(response, "accessToken", authResponse.getAccessToken(), 24 * 60 * 60);
        addTokenCookie(response, "refreshToken", authResponse.getRefreshToken(), 7 * 24 * 60 * 60);

        log.info("User registered successfully: {}", signUpRequest.getEmail());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest,
                                                     HttpServletResponse response) {
        AuthResponse authResponse = authService.refreshToken(refreshTokenRequest);

        // Update cookies with new tokens
        addTokenCookie(response, "accessToken", authResponse.getAccessToken(), 24 * 60 * 60);

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@Valid @RequestBody LogoutRequest logoutRequest,
                                                      HttpServletResponse response) {
        authService.logout(logoutRequest.getRefreshToken());

        // Clear cookies
        clearTokenCookie(response, "accessToken");
        clearTokenCookie(response, "refreshToken");

        log.info("User logged out successfully");
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserProfile> getCurrentUser() {
        UserProfile userProfile = authService.getCurrentUser();
        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/validate")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> validateToken() {
        UserProfile userProfile = authService.getCurrentUser();
        return ResponseEntity.ok(Map.of(
                "valid", true,
                "user", userProfile
        ));
    }

    @GetMapping("/oauth2/authorize/google")
    public ResponseEntity<Map<String, String>> getGoogleAuthUrl() {
        String authUrl = "https://hiring-be-production.up.railway.app/api/oauth2/authorization/google";
        return ResponseEntity.ok(Map.of(
                "authUrl", authUrl,
                "message", "Redirect to this URL to start Google OAuth2 flow"
        ));
    }

    @GetMapping("/check-auth")
    public ResponseEntity<Map<String, Object>> checkAuthStatus(HttpServletRequest request) {
        try {
            UserProfile userProfile = authService.getCurrentUser();
            return ResponseEntity.ok(Map.of(
                    "authenticated", true,
                    "user", userProfile
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "authenticated", false,
                    "message", "Not authenticated"
            ));
        }
    }

    private void addTokenCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }

    private void clearTokenCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
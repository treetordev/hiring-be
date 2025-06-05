package com.example.hiring.security;

import com.example.hiring.dto.auth.AuthResponse;
import com.example.hiring.service.OAuth2Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private OAuth2Service oAuth2Service;

    @Value("${app.frontend.base-url:https://your-frontend-domain.com}")
    private String frontendBaseUrl;

    @Value("${app.oauth2.response-mode:redirect}") // Change default to redirect
    private String responseMode;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        try {
            AuthResponse authResponse = oAuth2Service.processOAuth2Login(authentication);

            if ("json".equals(responseMode)) {
                handleJsonResponse(response, authResponse);
            } else {
                handleRedirectResponse(response, authResponse);
            }

        } catch (Exception e) {
            //log.error("OAuth2 authentication failed", e);
            handleErrorResponse(response, e);
        }
    }

    private void handleRedirectResponse(HttpServletResponse response, AuthResponse authResponse) throws IOException {
        String targetUrl = determineTargetUrl(authResponse);

        // Store tokens in secure cookies
        addTokensToCookies(response, authResponse);

        response.sendRedirect(targetUrl);
    }

    private String determineTargetUrl(AuthResponse authResponse) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(frontendBaseUrl);

        if (!authResponse.isProfileComplete()) {
            // Redirect to profile completion page
            builder.path("/profile/complete");
        } else {
            // Redirect to dashboard
            builder.path("/dashboard");
        }

        // Add success indicator
        builder.queryParam("auth", "success");

        return builder.build().toUriString();
    }

    private void addTokensToCookies(HttpServletResponse response, AuthResponse authResponse) {
        // Access token cookie
        Cookie accessTokenCookie = new Cookie("accessToken", authResponse.getAccessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(24 * 60 * 60); // 24 hours
       // accessTokenCookie.setSameSite(Cookie.SameSite.LAX);
        response.addCookie(accessTokenCookie);

        // Refresh token cookie
        Cookie refreshTokenCookie = new Cookie("refreshToken", authResponse.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        //refreshTokenCookie.setSameSite(Cookie.SameSite.LAX);
        response.addCookie(refreshTokenCookie);
    }

    private void handleJsonResponse(HttpServletResponse response, AuthResponse authResponse) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);

        var successResponse = Map.of(
                "success", true,
                "message", "OAuth2 authentication successful",
                "accessToken", authResponse.getAccessToken(),
                "refreshToken", authResponse.getRefreshToken(),
                "tokenType", authResponse.getTokenType(),
                "isProfileComplete", authResponse.isProfileComplete(),
                "redirectTo", authResponse.isProfileComplete() ? "/dashboard" : "/profile/complete",
                "user", Map.of(
                        "id", authResponse.getUserId(),
                        "email", authResponse.getEmail(),
                        "fullName", authResponse.getFullName(),
                        "roles", authResponse.getRoles()
                )
        );

        mapper.writeValue(response.getOutputStream(), successResponse);
    }

    private void handleErrorResponse(HttpServletResponse response, Exception e) throws IOException {
        if ("json".equals(responseMode)) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            var errorResponse = Map.of(
                    "success", false,
                    "error", "authentication_failed",
                    "message", e.getMessage()
            );

            mapper.writeValue(response.getOutputStream(), errorResponse);
        } else {
            String errorUrl = UriComponentsBuilder.fromUriString(frontendBaseUrl)
                    .path("/auth/error")
                    .queryParam("error", "authentication_failed")
                    .build().toUriString();

            response.sendRedirect(errorUrl);
        }
    }
}
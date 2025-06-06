package com.example.hiring.security;

import com.example.hiring.dto.auth.AuthResponse;
import com.example.hiring.service.OAuth2Service;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuth2Service oAuth2Service;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        try {
            AuthResponse authResponse = oAuth2Service.processOAuth2Login(authentication);

            // Add secure cookies for better security
            addTokenCookie(response, "accessToken", authResponse.getAccessToken(), 24 * 60 * 60); // 24 hours
            addTokenCookie(response, "refreshToken", authResponse.getRefreshToken(), 7 * 24 * 60 * 60); // 7 days

            // Determine redirect URL based on profile completion status
            String redirectPath = authResponse.isProfileComplete() ? "/dashboard" : "/user-profile";

            String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + redirectPath)
                    .queryParam("token", authResponse.getAccessToken())
                    .queryParam("refreshToken", authResponse.getRefreshToken())
                    .queryParam("success", "true")
                    .queryParam("userId", authResponse.getUserId())
                    .queryParam("profileComplete", authResponse.isProfileComplete())
                    .build().toUriString();

            log.info("OAuth2 login successful for user: {} (ID: {})",
                    authResponse.getEmail(), authResponse.getUserId());

            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            log.error("Error processing OAuth2 authentication success", e);

            String errorUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/login")
                    .queryParam("error", "processing_failed")
                    .queryParam("success", "false")
                    .queryParam("message", URLEncoder.encode("Authentication processing failed", StandardCharsets.UTF_8))
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, errorUrl);
        }
    }

    private void addTokenCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Only send over HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }
}
package com.example.hiring.security;

import com.example.hiring.dto.auth.AuthResponse;
import com.example.hiring.service.OAuth2Service;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private OAuth2Service oAuth2Service;

    @Value("${app.frontend.url:https://job-match-portal.lovable.app}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        try {
            log.info("OAuth2 authentication success - processing user");

            AuthResponse authResponse = oAuth2Service.processOAuth2Login(authentication);

            // Simple redirect with tokens in URL
            String redirectPath = authResponse.isProfileComplete() ? "/dashboard" : "/user-profile";

            String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + redirectPath)
                    .queryParam("success", "true")
                    .queryParam("token", authResponse.getAccessToken())
                    .queryParam("userId", authResponse.getUserId())
                    .queryParam("email", authResponse.getEmail())
                    .queryParam("profileComplete", authResponse.isProfileComplete())
                    .build().toUriString();

            log.info("OAuth2 success - redirecting user {} to: {}", authResponse.getEmail(), redirectPath);

            response.sendRedirect(targetUrl);

        } catch (Exception e) {
            log.error("OAuth2 processing failed", e);

            String errorUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/login")
                    .queryParam("error", "oauth2_failed")
                    .queryParam("message", "Login failed. Please try again.")
                    .build().toUriString();

            response.sendRedirect(errorUrl);
        }
    }
}
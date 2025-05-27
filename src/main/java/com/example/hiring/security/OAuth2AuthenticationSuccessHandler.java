package com.example.hiring.security;

import com.example.hiring.dto.auth.AuthResponse;
import com.example.hiring.service.OAuth2Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
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

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private OAuth2Service oAuth2Service;

    @Value("${app.cors.allowed-origins[0]:http://localhost:3000}")//url ya domain rediretion
    private String frontendUrl;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        try {
            AuthResponse authResponse = oAuth2Service.processOAuth2Login(authentication);

            // For testing backend only - return JSON
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_OK);

            // refactor
            var successResponse = new java.util.HashMap<String, Object>();
            successResponse.put("success", true);
            successResponse.put("message", "OAuth2 authentication successful");
            successResponse.put("accessToken", authResponse.getAccessToken());
            successResponse.put("refreshToken", authResponse.getRefreshToken());
            successResponse.put("tokenType", authResponse.getTokenType());
            successResponse.put("user", new java.util.HashMap<String, Object>() {{
                put("id", authResponse.getUserId());
                put("email", authResponse.getEmail());
                put("fullName", authResponse.getFullName());
                put("roles", authResponse.getRoles());
            }});

            mapper.writeValue(response.getOutputStream(), successResponse);

            // Agar frontend aata then redirect idhr se karwayenge
            /*
            String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/auth/callback")
                .queryParam("token", authResponse.getAccessToken())
                .queryParam("refreshToken", authResponse.getRefreshToken())
                .queryParam("success", "true")
                .build().toUriString();

            response.sendRedirect(targetUrl);
            */

        } catch (Exception e) {
            // For testing - return JSON error response
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            var errorResponse = new java.util.HashMap<String, Object>();
            errorResponse.put("success", false);
            errorResponse.put("error", "authentication_failed");
            errorResponse.put("message", e.getMessage());

            mapper.writeValue(response.getOutputStream(), errorResponse);

            // Frontend aane pe
            /*
            String errorUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/auth/callback")
                .queryParam("error", "authentication_failed")
                .queryParam("success", "false")
                .build().toUriString();

            response.sendRedirect(errorUrl);
            */
        }
    }
}
package com.example.authservice.security;


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

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        try {
            AuthResponse authResponse = oAuth2Service.processOAuth2Login(authentication);

            // Redirect to frontend user-profile page with tokens
            String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/user-profile")
                    .queryParam("token", authResponse.getAccessToken())
                    .queryParam("refreshToken", authResponse.getRefreshToken())
                    .queryParam("success", "true")
                    .build().toUriString();

            response.sendRedirect(targetUrl);

            // For testing backend only - comment above and uncomment below
            /*
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_OK);

            // Create a success response
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
            */

        } catch (Exception e) {
            // Redirect to frontend with error
            String errorUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/user-profile")
                    .queryParam("error", "authentication_failed")
                    .queryParam("success", "false")
                    .queryParam("message", e.getMessage())
                    .build().toUriString();

            response.sendRedirect(errorUrl);
        }
    }
}
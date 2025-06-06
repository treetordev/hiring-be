package com.example.hiring.service;

import com.example.hiring.dto.auth.AuthResponse;
import com.example.hiring.entity.AuthProvider;
import com.example.hiring.entity.User;
import com.example.hiring.repository.UserRepository;
import com.example.hiring.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class OAuth2Service {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired(required = false)
    private RefreshTokenService refreshTokenService;

    public AuthResponse processOAuth2Login(Authentication authentication) {
        try {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = oauth2User.getAttributes();

            String email = (String) attributes.get("email");
            String fullName = (String) attributes.get("name");
            String givenName = (String) attributes.get("given_name");
            String familyName = (String) attributes.get("family_name");
            String providerId = (String) attributes.get("sub");
            String profilePicture = (String) attributes.get("picture");

            log.info("OAuth2 user attributes: email={}, name={}, given_name={}, family_name={}",
                    email, fullName, givenName, familyName);

            if (!StringUtils.hasText(email)) {
                throw new RuntimeException("Email not found from OAuth2 provider");
            }

            // Handle missing first/last names
            String firstName = extractFirstName(givenName, familyName, fullName);
            String lastName = extractLastName(givenName, familyName, fullName);

            User user = findOrCreateUser(email, firstName, lastName, providerId, profilePicture);

            String accessToken = jwtUtils.generateTokenFromUser(user);

            // Try to create refresh token, but don't fail if it doesn't work
            String refreshToken = null;
            try {
                if (refreshTokenService != null) {
                    refreshToken = refreshTokenService.createRefreshToken(user);
                }
            } catch (Exception e) {
                log.warn("Failed to create refresh token for user {}: {}", user.getEmail(), e.getMessage());
                // Continue without refresh token
            }

            log.info("OAuth2 authentication successful for user: {}", user.getEmail());

            return new AuthResponse(
                    accessToken,
                    refreshToken, // Can be null
                    "Bearer",
                    user.getId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getRoles(),
                    user.isSetupComplete()
            );
        } catch (Exception e) {
            log.error("Error processing OAuth2 user", e);
            throw new RuntimeException("Error processing OAuth2 authentication: " + e.getMessage());
        }
    }

    private String extractFirstName(String givenName, String familyName, String fullName) {
        if (StringUtils.hasText(givenName)) {
            return givenName;
        }

        if (StringUtils.hasText(fullName)) {
            String[] nameParts = fullName.trim().split("\\s+");
            return nameParts[0];
        }

        return "User"; // Fallback
    }

    private String extractLastName(String givenName, String familyName, String fullName) {
        if (StringUtils.hasText(familyName)) {
            return familyName;
        }

        if (StringUtils.hasText(fullName)) {
            String[] nameParts = fullName.trim().split("\\s+");
            if (nameParts.length > 1) {
                return String.join(" ", java.util.Arrays.copyOfRange(nameParts, 1, nameParts.length));
            }
        }

        return "User"; // Fallback
    }

    private User findOrCreateUser(String email, String firstName, String lastName,
                                  String providerId, String profilePicture) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            boolean needsUpdate = false;

            // Update provider if it was local before
            if (existingUser.getProvider() == AuthProvider.LOCAL) {
                existingUser.setProvider(AuthProvider.GOOGLE);
                existingUser.setProviderId(providerId);
                existingUser.setEmailVerified(true);
                needsUpdate = true;
            }

            // Update profile picture if provided and different
            if (StringUtils.hasText(profilePicture) &&
                    !profilePicture.equals(existingUser.getProfilePicture())) {
                existingUser.setProfilePicture(profilePicture);
                needsUpdate = true;
            }

            if (needsUpdate) {
                existingUser.setUpdatedAt(LocalDateTime.now());
                existingUser = userRepository.save(existingUser);
                log.info("Updated existing user: {}", existingUser.getEmail());
            }

            return existingUser;
        } else {
            // Create new user
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setProvider(AuthProvider.GOOGLE);
            user.setProviderId(providerId);
            user.setProfilePicture(profilePicture);
            user.setEmailVerified(true);
            user.setSetupComplete(false);

            user = userRepository.save(user);
            log.info("Created new user from OAuth2: {} (firstName: {}, lastName: {})",
                    user.getEmail(), user.getFirstName(), user.getLastName());

            return user;
        }
    }
}
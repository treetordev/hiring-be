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

            log.info("OAuth2 processing for email: {}", email);

            if (!StringUtils.hasText(email)) {
                throw new RuntimeException("Email not found from OAuth2 provider");
            }

            // Simple name extraction
            String firstName = StringUtils.hasText(givenName) ? givenName :
                    (StringUtils.hasText(fullName) ? fullName.split(" ")[0] : "User");
            String lastName = StringUtils.hasText(familyName) ? familyName : "User";

            User user = findOrCreateUser(email, firstName, lastName, providerId, profilePicture);

            // Generate only access token for now - no refresh token to avoid complications
            String accessToken = jwtUtils.generateTokenFromUser(user);

            log.info("OAuth2 authentication successful for user: {}", user.getEmail());

            return new AuthResponse(
                    accessToken,
                    null, // No refresh token for now
                    "Bearer",
                    user.getId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getRoles(),
                    user.isSetupComplete()
            );
        } catch (Exception e) {
            log.error("Error processing OAuth2 user: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing OAuth2 authentication");
        }
    }

    private User findOrCreateUser(String email, String firstName, String lastName,
                                  String providerId, String profilePicture) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            log.info("Found existing user: {}", existingUser.getEmail());

            // Simple update without complex logic
            if (existingUser.getProvider() == AuthProvider.LOCAL) {
                existingUser.setProvider(AuthProvider.GOOGLE);
                existingUser.setProviderId(providerId);
                existingUser.setEmailVerified(true);
                existingUser.setUpdatedAt(LocalDateTime.now());
                return userRepository.save(existingUser);
            }

            return existingUser;
        } else {
            // Create new user with minimal required fields
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setProvider(AuthProvider.GOOGLE);
            user.setProviderId(providerId);
            user.setProfilePicture(profilePicture);
            user.setEmailVerified(true);
            user.setSetupComplete(false);

            // Set required fields explicitly
            user.setEnabled(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);

            User savedUser = userRepository.save(user);
            log.info("Created new OAuth2 user: {}", savedUser.getEmail());

            return savedUser;
        }
    }
}
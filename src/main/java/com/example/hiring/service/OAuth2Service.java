package com.example.hiring.service;

import com.example.hiring.dto.auth.AuthResponse;
import com.example.hiring.entity.AuthProvider;
import com.example.hiring.entity.User;
import com.example.hiring.exception.OAuth2AuthenticationProcessingException;
import com.example.hiring.repository.UserRepository;
import com.example.hiring.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class OAuth2Service {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse processOAuth2Login(Authentication authentication) {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        try {
            User user = processOAuth2User(oauth2User);

            String accessToken = jwtUtils.generateTokenFromUser(user);
            String refreshToken = refreshTokenService.createRefreshToken(user);

            log.info("OAuth2 authentication successful for user: {}", user.getEmail());

            return new AuthResponse(
                    accessToken,
                    refreshToken,
                    "Bearer",
                    user.getId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getRoles(),
                    user.isSetupComplete()
            );
        } catch (Exception e) {
            log.error("Error processing OAuth2 user", e);
            throw new OAuth2AuthenticationProcessingException("Error processing OAuth2 authentication", e);
        }
    }

    private User processOAuth2User(OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo("google", attributes);

        if (!StringUtils.hasText(userInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(userInfo.getEmail());

        if (userOptional.isPresent()) {
            return updateExistingUser(userOptional.get(), userInfo);
        } else {
            return createNewUser(userInfo);
        }
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo userInfo) {
        boolean needsUpdate = false;

        // Update provider if it was local before
        if (existingUser.getProvider() == AuthProvider.LOCAL) {
            existingUser.setProvider(AuthProvider.GOOGLE);
            existingUser.setProviderId(userInfo.getId());
            existingUser.setEmailVerified(true);
            needsUpdate = true;
        }

        // Update profile picture if provided and different
        if (StringUtils.hasText(userInfo.getImageUrl()) &&
                !userInfo.getImageUrl().equals(existingUser.getProfilePicture())) {
            existingUser.setProfilePicture(userInfo.getImageUrl());
            needsUpdate = true;
        }

        // Update name if not set
        if (!StringUtils.hasText(existingUser.getFirstName()) && StringUtils.hasText(userInfo.getFirstName())) {
            existingUser.setFirstName(userInfo.getFirstName());
            needsUpdate = true;
        }

        if (!StringUtils.hasText(existingUser.getLastName()) && StringUtils.hasText(userInfo.getLastName())) {
            existingUser.setLastName(userInfo.getLastName());
            needsUpdate = true;
        }

        if (needsUpdate) {
            existingUser.setUpdatedAt(LocalDateTime.now());
            existingUser = userRepository.save(existingUser);
            log.info("Updated existing user: {}", existingUser.getEmail());
        }

        return existingUser;
    }

    private User createNewUser(OAuth2UserInfo userInfo) {
        User user = new User();
        user.setFirstName(userInfo.getFirstName());
        user.setLastName(userInfo.getLastName());
        user.setEmail(userInfo.getEmail());
        user.setProvider(AuthProvider.GOOGLE);
        user.setProviderId(userInfo.getId());
        user.setProfilePicture(userInfo.getImageUrl());
        user.setEmailVerified(true);
        user.setSetupComplete(false);

        user = userRepository.save(user);
        log.info("Created new user from OAuth2: {}", user.getEmail());

        return user;
    }

    // OAuth2UserInfo interface and factory for loose coupling
    public interface OAuth2UserInfo {
        String getId();
        String getFirstName();
        String getLastName();
        String getEmail();
        String getImageUrl();
    }

    public static class GoogleOAuth2UserInfo implements OAuth2UserInfo {
        private final Map<String, Object> attributes;

        public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
            this.attributes = attributes;
        }

        @Override
        public String getId() {
            return (String) attributes.get("sub");
        }

        @Override
        public String getFirstName() {
            return (String) attributes.get("given_name");
        }

        @Override
        public String getLastName() {
            return (String) attributes.get("family_name");
        }

        @Override
        public String getEmail() {
            return (String) attributes.get("email");
        }

        @Override
        public String getImageUrl() {
            return (String) attributes.get("picture");
        }
    }

    public static class OAuth2UserInfoFactory {
        public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
            if ("google".equalsIgnoreCase(registrationId)) {
                return new GoogleOAuth2UserInfo(attributes);
            } else {
                throw new OAuth2AuthenticationProcessingException("Login with " + registrationId + " is not supported");
            }
        }
    }
}
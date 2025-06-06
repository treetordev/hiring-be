package com.example.hiring.service;

import com.example.hiring.dto.auth.AuthResponse;
import com.example.hiring.entity.User;
import com.example.hiring.repository.UserRepository;
import com.example.hiring.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static com.example.hiring.entity.AuthProvider.GOOGLE;

@Service
@Transactional
public class OAuth2Service {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public AuthResponse processOAuth2Login(Authentication authentication) {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email = (String) attributes.get("email");
        String firstName = (String) attributes.get("given_name");
        String lastName = (String) attributes.get("family_name");
        String providerId = (String) attributes.get("sub");
        String profilePicture = (String) attributes.get("picture");

        User user = findOrCreateUser(email, firstName, lastName, providerId, profilePicture);

        String accessToken = jwtUtils.generateTokenFromUser(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

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
    }

    private User findOrCreateUser(String email, String firstName, String lastName,
                                  String providerId, String profilePicture) {
        Optional<User> userOptional = userRepository.findByEmailAndProvider(email, GOOGLE);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            // Update user information , need to update this discuss karna
            boolean updated = false;

            /*if (!providerId.equals(existingUser.getProviderId())) {
                existingUser.setProviderId(providerId);
                updated = true;
            }
             */

            if (profilePicture != null && !profilePicture.equals(existingUser.getProfilePicture())) {
                existingUser.setProfilePicture(profilePicture);
                updated = true;
            }

            if (updated) {
                existingUser.setUpdatedAt(LocalDateTime.now());
                return userRepository.save(existingUser);
            }

            return existingUser;
        }

        // for checking agar user exists karta with same email but different provider lke github
        Optional<User> existingUserWithEmail = userRepository.findByEmail(email);
        if (existingUserWithEmail.isPresent()) {
            User existingUser = existingUserWithEmail.get();
            // Link OAuth account to existing user
            existingUser.setProvider(GOOGLE);
            existingUser.setProviderId(providerId);
            existingUser.setEmailVerified(true);
            if (profilePicture != null) {
                existingUser.setProfilePicture(profilePicture);
            }
            existingUser.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(existingUser);
        }

        // Create new user
        User newUser = new User(firstName, lastName, email, GOOGLE, providerId);
        newUser.setProfilePicture(profilePicture);
        newUser.setEmailVerified(true);
        newUser.setSetupComplete(false);

        return userRepository.save(newUser);
    }
}
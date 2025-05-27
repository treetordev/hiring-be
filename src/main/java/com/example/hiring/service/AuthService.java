package com.example.hiring.service;

import com.example.hiring.dto.auth.*;
import com.example.hiring.entity.RefreshToken;
import com.example.hiring.entity.User;
import com.example.hiring.exception.BadRequestException;
import com.example.hiring.exception.ResourceNotFoundException;
import com.example.hiring.repository.RefreshTokenRepository;
import com.example.hiring.repository.UserRepository;
import com.example.hiring.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        String accessToken = jwtUtils.generateJwtToken(authentication);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRoles()
        );
    }

    public AuthResponse register(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email address already in use.");
        }

        User user = new User(
                signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword())
        );

        User savedUser = userRepository.save(user);

        // ye auto-login kar dega after registration
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedUser, null, savedUser.getAuthorities()
        );

        String accessToken = jwtUtils.generateTokenFromUser(savedUser);
        String refreshToken = refreshTokenService.createRefreshToken(savedUser);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getRoles()
        );
    }

    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String requestRefreshToken = refreshTokenRequest.getRefreshToken();

        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(requestRefreshToken);

        if (refreshTokenOpt.isEmpty()) {
            throw new BadRequestException("Refresh token not found!");
        }

        RefreshToken refreshToken = refreshTokenOpt.get();

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new BadRequestException("Refresh token expired!");
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtUtils.generateTokenFromUser(user);

        return new AuthResponse(
                newAccessToken,
                requestRefreshToken,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRoles()
        );
    }

    public void logout(String refreshToken) {
        refreshTokenService.deleteRefreshToken(refreshToken);
    }

    public UserProfile getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("User not authenticated");
        }

        User user = (User) authentication.getPrincipal();
        return new UserProfile(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getProfilePicture(),
                user.getProvider(),
                user.getRoles(),
                user.getEmailVerified(),
                user.getCreatedAt()
        );
    }

    public void cleanupExpiredTokens() {
        refreshTokenService.cleanupExpiredTokens();
    }
}
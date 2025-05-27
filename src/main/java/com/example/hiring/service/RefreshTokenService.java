package com.example.hiring.service;

import com.example.hiring.entity.RefreshToken;
import com.example.hiring.entity.User;
import com.example.hiring.repository.RefreshTokenRepository;
import com.example.hiring.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${app.jwt.refresh-expiration}")
    private int jwtRefreshExpirationMs;

    public String createRefreshToken(User user) {
        // Delete existing refresh token for the user
        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);

        String tokenValue = jwtUtils.generateRefreshToken(user);
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(jwtRefreshExpirationMs / 1000);

        RefreshToken refreshToken = new RefreshToken(tokenValue, expiryDate, user);
        refreshTokenRepository.save(refreshToken);

        return tokenValue;
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }

    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
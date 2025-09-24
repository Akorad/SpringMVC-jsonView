package org.Akorad.service.security.impl;

import lombok.RequiredArgsConstructor;
import org.Akorad.entity.RefreshToken;
import org.Akorad.entity.User;
import org.Akorad.repository.RefreshTokenRepository;
import org.Akorad.service.UserService;
import org.Akorad.service.security.RefreshTokenService;
import org.Akorad.util.JWTUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshTtl;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final JWTUtils jwtUtils;

    @Override
    public RefreshToken createRefreshToken(HashMap<String, Object> claims, UserDetails userDetails) {
        RefreshToken token = new RefreshToken();
        token.setUser(userService.getUserByUsername(userDetails.getUsername()));
        token.setToken(jwtUtils.generateRefreshToken(claims, userDetails));
        token.setExpiryDate(Instant.now().plusMillis(refreshTtl));
        return refreshTokenRepository.save(token);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public boolean isExpired(RefreshToken refreshToken) {
        return refreshToken.getExpiryDate().isBefore(Instant.now());
    }

    @Override
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteAllByUser_Id(userId);
    }
}

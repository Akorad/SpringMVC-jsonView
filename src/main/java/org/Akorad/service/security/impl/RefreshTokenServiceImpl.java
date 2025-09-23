package org.Akorad.service.security.impl;

import lombok.RequiredArgsConstructor;
import org.Akorad.entity.RefreshToken;
import org.Akorad.entity.User;
import org.Akorad.repository.RefreshTokenRepository;
import org.Akorad.service.security.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshTtl;

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken createRefreshToken(User user) {
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(java.util.UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusMillis(refreshTtl));
        return refreshTokenRepository.save(token);
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

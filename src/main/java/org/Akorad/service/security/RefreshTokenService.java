package org.Akorad.service.security;

import org.Akorad.entity.RefreshToken;
import org.Akorad.entity.User;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    boolean isExpired(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(Long userId);
}

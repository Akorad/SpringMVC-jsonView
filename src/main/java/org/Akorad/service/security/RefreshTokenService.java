package org.Akorad.service.security;

import org.Akorad.entity.RefreshToken;
import org.Akorad.entity.User;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    boolean isExpired(RefreshToken refreshToken);
    void deleteByUserId(Long userId);
}

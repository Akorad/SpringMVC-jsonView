package org.Akorad.service.security;

import org.Akorad.entity.RefreshToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(HashMap<String, Object> claims, UserDetails userDetails);
    boolean isExpired(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(Long userId);
}

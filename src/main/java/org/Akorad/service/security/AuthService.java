package org.Akorad.service.security;

import org.Akorad.dto.AuthRequest;
import org.Akorad.dto.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest authRequest, String ipAddress);
    AuthResponse refreshToken(String refreshToken, String ipAddress);
    AuthResponse register(AuthRequest authRequest, String ipAddress);
    void logoutAllSessions(String refreshToken, String ipAddress);
}

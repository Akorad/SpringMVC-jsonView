package org.Akorad.service.security.impl;

import lombok.RequiredArgsConstructor;
import org.Akorad.dto.AuthRequest;
import org.Akorad.dto.AuthResponse;
import org.Akorad.entity.RefreshToken;
import org.Akorad.entity.User;
import org.Akorad.service.UserService;
import org.Akorad.service.security.AuthAuditService;
import org.Akorad.service.security.AuthService;
import org.Akorad.service.security.FailedLoginService;
import org.Akorad.service.security.RefreshTokenService;
import org.Akorad.util.JWTUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountLockedException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JWTUtils jwtUtils;
    private final UserService userService;
    private final FailedLoginService failedLoginService;
    private final RefreshTokenService refreshTokenService;
    private final AuthAuditService auditService;

    @Override
    public AuthResponse login(AuthRequest authRequest, String ipAddress) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.username(), authRequest.password()));

            UserDetails ud = (UserDetails) authentication.getPrincipal();

            failedLoginService.resetFailedAttempts(authRequest.username());

            User user = userService.getUserByUsername(ud.getUsername());

            String accessToken = jwtUtils.generateToken(ud);

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            auditService.logEvent(authRequest.username(), "LOGIN_SUCCESS", ipAddress);

            return new AuthResponse(accessToken, refreshToken.getToken());
        } catch (BadCredentialsException ex){
            failedLoginService.onFailedLogin(authRequest.username());
            auditService.logEvent(authRequest.username(), "LOGIN_FAILED", ipAddress);
            throw new BadCredentialsException("Invalid username or password");
        } catch (LockedException ex) {
            auditService.logEvent(authRequest.username(), "LOGIN_FAILED_ACCOUNT_LOCKED", ipAddress);
            throw new AccountLockedException("Account is locked due to multiple failed login attempts");
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken, String ipAddress) {
        return null;
    }
}

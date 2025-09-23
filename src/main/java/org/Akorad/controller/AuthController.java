package org.Akorad.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.Akorad.dto.AuthRequest;
import org.Akorad.dto.AuthResponse;
import org.Akorad.service.security.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        AuthResponse response = authService.login(authRequest, ipAddress);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody String refreshToken, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        AuthResponse response = authService.refreshToken(refreshToken, ipAddress);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        AuthResponse response = authService.register(authRequest, ipAddress);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout-all" )
    public ResponseEntity<String> logoutAllSessions(@RequestBody String refreshToken, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        authService.logoutAllSessions(refreshToken, ipAddress);
        return ResponseEntity.ok("Logged out from all sessions.");
    }
}

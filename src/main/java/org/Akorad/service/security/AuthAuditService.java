package org.Akorad.service.security;

public interface AuthAuditService {
    void logEvent(String username, String event, String ipAddress);
}

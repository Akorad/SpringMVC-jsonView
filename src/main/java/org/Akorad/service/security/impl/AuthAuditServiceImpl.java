package org.Akorad.service.security.impl;

import lombok.RequiredArgsConstructor;
import org.Akorad.entity.AuthAudit;
import org.Akorad.repository.AuthAuditRepository;
import org.Akorad.service.security.AuthAuditService;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthAuditServiceImpl implements AuthAuditService {

    private final AuthAuditRepository auditRepository;

    @Override
    public void logEvent(String username, String event, String ipAddress) {
        AuthAudit audit = new AuthAudit();
        audit.setUsername(username);
        audit.setEvent(event);
        audit.setIpAddress(ipAddress);
        audit.setEventTime(Instant.now());
        auditRepository.save(audit);
    }
}

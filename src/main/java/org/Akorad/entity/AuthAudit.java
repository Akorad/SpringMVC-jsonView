package org.Akorad.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "auth_audit")
@Getter
@Setter
public class AuthAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;

    private String event;

    private Instant eventTime;

    private String ipAddress;
}

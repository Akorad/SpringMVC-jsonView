package org.Akorad.repository;

import org.Akorad.entity.AuthAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthAuditRepository extends JpaRepository<AuthAudit, Long> {
}

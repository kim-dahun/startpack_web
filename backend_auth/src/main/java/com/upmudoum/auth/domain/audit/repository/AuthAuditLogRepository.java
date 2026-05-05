package com.upmudoum.auth.domain.audit.repository;

import com.upmudoum.auth.domain.audit.entity.AuthAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthAuditLogRepository extends JpaRepository<AuthAuditLog, Long> {

    List<AuthAuditLog> findTop50ByOrderByCreatedAtDesc();

    List<AuthAuditLog> findTop50BySubjectOrderByCreatedAtDesc(String subject);
}

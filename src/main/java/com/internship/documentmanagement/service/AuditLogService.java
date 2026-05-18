package com.internship.documentmanagement.service;

import com.internship.documentmanagement.dto.response.AuditLogResponse;

import java.util.List;

public interface AuditLogService {
    void logAction(Long userId, String action, String resourceType, Long resourceId, String reason);
    List<AuditLogResponse> getUserAuditLogs(Long userId);
    List<AuditLogResponse> getActionAuditLogs(String action);
    List<AuditLogResponse> getAllAuditLogs();
}

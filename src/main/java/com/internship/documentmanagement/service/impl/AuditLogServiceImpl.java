package com.internship.documentmanagement.service.impl;

import com.internship.documentmanagement.dto.response.AuditLogResponse;
import com.internship.documentmanagement.model.AuditLog;
import com.internship.documentmanagement.model.User;
import com.internship.documentmanagement.repository.AuditLogRepository;
import com.internship.documentmanagement.repository.UserRepository;
import com.internship.documentmanagement.service.AuditLogService;
import com.internship.documentmanagement.util.RequestContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    @Override
    public void logAction(Long userId, String action, String resourceType, Long resourceId, String reason){
        User user = null;
        if(userId != null){
            user = userRepository.findById(userId).orElse(null);
        }

        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action(action)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .ipAddress(RequestContextUtil.getClientIpAddress())
                .userAgent(RequestContextUtil.getUserAgent())
                .reason(reason)
                .build();

        auditLogRepository.save(auditLog);
    }

    @Override
    public List<AuditLogResponse> getUserAuditLogs(Long userId){
        return auditLogRepository.findByUserId(userId)
                .stream()
                .map(this::mapToAuditLogResponse)
                .toList();
    }

    @Override
    public List<AuditLogResponse> getActionAuditLogs(String action){
        return auditLogRepository.findByAction(action)
                .stream()
                .map(this::mapToAuditLogResponse)
                .toList();
    }

    @Override
    public List<AuditLogResponse> getAllAuditLogs(){
        return auditLogRepository.findAllWithUsers()
                .stream()
                .map(this::mapToAuditLogResponse)
                .toList();
    }

    private AuditLogResponse mapToAuditLogResponse(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .username(auditLog.getUser() != null ? auditLog.getUser().getUsername() : "SYSTEM")
                .action(auditLog.getAction())
                .resourceType(auditLog.getResourceType())
                .resourceId(auditLog.getResourceId())
                .ipAddress(auditLog.getIpAddress())
                .userAgent(auditLog.getUserAgent())
                .reason(auditLog.getReason())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }
}

package com.internship.documentmanagement.controller;

import com.internship.documentmanagement.dto.response.AuditLogResponse;
import com.internship.documentmanagement.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getAllAuditLogs(){
        List<AuditLogResponse> logs = auditLogService.getAllAuditLogs();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLogResponse>> getUserAuditLogs(@PathVariable Long userId){
        List<AuditLogResponse> logs = auditLogService.getUserAuditLogs(userId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<List<AuditLogResponse>> getActionAuditLogs(@PathVariable String action){
        List<AuditLogResponse> logs = auditLogService.getActionAuditLogs(action);
        return ResponseEntity.ok(logs);
    }
}

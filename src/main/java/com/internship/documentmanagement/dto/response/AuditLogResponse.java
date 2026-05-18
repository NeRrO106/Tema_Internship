package com.internship.documentmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    private Long id;
    private String username;
    private String action;
    private String resourceType;
    private Long resourceId;
    private String ipAddress;
    private String userAgent;
    private String reason;
    private LocalDateTime createdAt;
}

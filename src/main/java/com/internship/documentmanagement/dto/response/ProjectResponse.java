package com.internship.documentmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private String status;
    private String ownerUsername;
    private Set<String> memberUsernames;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

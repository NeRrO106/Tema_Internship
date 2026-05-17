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
public class DocumentResponse {

    private Long id;
    private String fileName;
    private Long fileSize;
    private String mimeType;
    private String fileHash;
    private Long projectId;
    private String uploadedByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

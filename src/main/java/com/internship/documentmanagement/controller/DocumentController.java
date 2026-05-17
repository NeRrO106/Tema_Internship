package com.internship.documentmanagement.controller;


import com.internship.documentmanagement.dto.request.DocumentUploadRequest;
import com.internship.documentmanagement.dto.response.DocumentResponse;
import com.internship.documentmanagement.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/documents")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<DocumentResponse> uploadDocument(
            @PathVariable Long projectId,
            @RequestParam MultipartFile file,
            @RequestParam(required = false) String reason,
            Authentication authentication
            ){
        DocumentUploadRequest request = new DocumentUploadRequest();
        request.setFile(file);
        request.setReason(reason);

        DocumentResponse response = documentService.uploadDocument(projectId, authentication.getName(), request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getProjectDocuments(@PathVariable Long projectId){
        List<DocumentResponse> documents = documentService.getProjectDocuments(projectId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentResponse> getDocument(
            @PathVariable Long projectId,
            @PathVariable Long documentId
    ){
        DocumentResponse document = documentService.getDocument(documentId, projectId);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<InputStreamResource> downloadDocument(
            @PathVariable Long projectId,
            @PathVariable Long documentId,
            Authentication authentication
    ){
        InputStream fileStream = documentService.downloadDocument(documentId, projectId, authentication.getName());
        DocumentResponse doc = documentService.getDocument(documentId,projectId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(fileStream));
    }

    @DeleteMapping("{/documentId}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long projectId,
            @PathVariable Long documentId,
            Authentication authentication
    ){
        documentService.deleteDocument(documentId, projectId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}

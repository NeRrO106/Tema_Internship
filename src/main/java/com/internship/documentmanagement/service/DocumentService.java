package com.internship.documentmanagement.service;


import com.internship.documentmanagement.dto.request.DocumentUploadRequest;
import com.internship.documentmanagement.dto.response.DocumentResponse;

import java.io.InputStream;
import java.util.List;

public interface DocumentService {
    DocumentResponse uploadDocument(Long projectId, String uploaderEmail, DocumentUploadRequest request);
    InputStream downloadDocument(Long documentId, Long projectId, String downloaderEmail);
    void deleteDocument(Long documentId, Long projectId, String deleterEmail);
    List<DocumentResponse> getProjectDocuments(Long projectId);
    DocumentResponse getDocument(Long documentId, Long projectId);
}

package com.internship.documentmanagement.service.impl;

import com.internship.documentmanagement.dto.request.DocumentUploadRequest;
import com.internship.documentmanagement.dto.response.DocumentResponse;
import com.internship.documentmanagement.model.Document;
import com.internship.documentmanagement.model.Project;
import com.internship.documentmanagement.model.User;
import com.internship.documentmanagement.repository.DocumentRepository;
import com.internship.documentmanagement.repository.ProjectRepository;
import com.internship.documentmanagement.repository.UserRepository;
import com.internship.documentmanagement.service.AuditLogService;
import com.internship.documentmanagement.service.DocumentService;
import com.internship.documentmanagement.util.FileHashUtil;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Override
    public DocumentResponse uploadDocument(Long projectId, String uploaderEmail, DocumentUploadRequest request){
        Project project = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(()->new RuntimeException("Project not found"));

        User uploader = userRepository.findByEmail(uploaderEmail)
                .orElseThrow(()->new RuntimeException("User not found"));

        MultipartFile file = request.getFile();
        if(file.isEmpty()){
            throw new RuntimeException("File cannot be empty");
        }

        try {
            String fileExtension = getFileExtension(file.getOriginalFilename());
            String storagePath = "projects/" + projectId + "/" + UUID.randomUUID() + "." + fileExtension;

            String fileHash;
            try (InputStream hashStream = file.getInputStream()) {
                fileHash = FileHashUtil.calculateSHA256(hashStream);
            }
            try (InputStream minioStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(storagePath)
                                .stream(minioStream, file.getSize(), -1)
                                .build()
                );
            }

            Document document = Document.builder()
                    .fileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .mimeType(file.getContentType())
                    .storagePath(storagePath)
                    .fileHash(fileHash)
                    .project(project)
                    .uploadedBy(uploader)
                    .build();

            documentRepository.save(document);
            auditLogService.logAction(uploader.getId(), "UPLOAD", "DOCUMENT", document.getId(), request.getReason());

            return mapToDocumentResponse(document);
        }
        catch (Exception e){
            throw new RuntimeException("Failed to upload document: " + e.getMessage());
        }
    }

    @Override
    public InputStream downloadDocument(Long documentId, Long projectId, String downloaderEmail){
        Document document = documentRepository.findByIdAndProjectId(documentId, projectId)
                .orElseThrow(()-> new RuntimeException("Document not found in this project"));

        User downloader = userRepository.findByEmail(downloaderEmail)
                .orElseThrow(()-> new RuntimeException("User not found"));

        try{
            auditLogService.logAction(downloader.getId(), "DOWNLOAD", "DOCUMENT", documentId, null);

            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(document.getStoragePath())
                            .build()
            );
        }
        catch(Exception e){
            throw new RuntimeException("Failed to download document: " + e.getMessage());
        }
    }

    @Override
    public void deleteDocument(Long documentId, Long projectId, String deleterEmail){
        Document document = documentRepository.findByIdAndProjectId(documentId, projectId)
                .orElseThrow(()-> new RuntimeException("Document not found in this project"));

        User deleter = userRepository.findByEmail(deleterEmail)
                .orElseThrow(()-> new RuntimeException("User not found"));

        try{
            minioClient.removeObject(
                    RemoveObjectArgs
                            .builder()
                            .bucket(bucketName)
                            .object(document.getStoragePath())
                            .build()
            );
            documentRepository.delete(document);
            auditLogService.logAction(deleter.getId(), "DELETE", "DOCUMENT", documentId, null);
        }
        catch (Exception e){
            throw new RuntimeException("Failed to delete document: " + e.getMessage());
        }

    }

    @Override
    public List<DocumentResponse> getProjectDocuments(Long projectId){
        projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(()-> new RuntimeException("Project not found"));

        return documentRepository.findByProjectId(projectId)
                .stream()
                .map(this::mapToDocumentResponse)
                .toList();
    }

    @Override
    public DocumentResponse getDocument(Long documentId, Long projectId){
        Document document = documentRepository.findByIdAndProjectId(documentId, projectId)
                .orElseThrow(()-> new RuntimeException("Document not found in this project"));

        return mapToDocumentResponse(document);
    }

    private DocumentResponse mapToDocumentResponse(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .fileName(document.getFileName())
                .fileSize(document.getFileSize())
                .mimeType(document.getMimeType())
                .fileHash(document.getFileHash())
                .projectId(document.getProject().getId())
                .uploadedByUsername(document.getUploadedBy().getUsername())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "bin";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}

package com.internship.documentmanagement.service;

import com.internship.documentmanagement.dto.request.ProjectRequest;
import com.internship.documentmanagement.dto.response.ProjectResponse;

import java.util.List;

public interface ProjectService {
    ProjectResponse createProject(String ownerEmail, ProjectRequest request);
    ProjectResponse getProjectById(Long id);
    List<ProjectResponse> getAllProjects();
    List<ProjectResponse> getMyProjects(String email);
    ProjectResponse updateProject(Long id, String ownerEmail, ProjectRequest request);
    void deleteProject(Long id, String ownerEmail);
    ProjectResponse addMember(Long projectId, Long userId, String ownerEmail);
    ProjectResponse removeMember(Long projectId, Long userId, String ownerEmail);
}

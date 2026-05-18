package com.internship.documentmanagement.service.impl;

import com.internship.documentmanagement.dto.request.ProjectRequest;
import com.internship.documentmanagement.dto.response.ProjectResponse;
import com.internship.documentmanagement.model.Project;
import com.internship.documentmanagement.model.ProjectStatus;
import com.internship.documentmanagement.model.User;
import com.internship.documentmanagement.repository.ProjectRepository;
import com.internship.documentmanagement.repository.UserRepository;
import com.internship.documentmanagement.service.AuditLogService;
import com.internship.documentmanagement.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Override
    public ProjectResponse createProject(String ownerEmail, ProjectRequest request){
        User owner = userRepository.findByEmail(ownerEmail).orElseThrow(() -> new RuntimeException("User not found"));

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status(ProjectStatus.ACTIVE)
                .owner(owner)
                .build();
        project.getMembers().add(owner);
        projectRepository.save(project);
        auditLogService.logAction(owner.getId(), "PROJECT_CREATE", "PROJECT", project.getId(),
                "Project created with name: " + project.getName());
        return mapToProjectResponse(project);
    }

    @Override
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return mapToProjectResponse(project);
    }

    @Override
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(this::mapToProjectResponse)
                .toList();
    }

    @Override
    public List<ProjectResponse> getMyProjects(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return projectRepository.findAllByMemberAndDeletedAtIsNull(user)
                .stream()
                .map(this::mapToProjectResponse)
                .toList();
    }

    @Override
    public ProjectResponse updateProject(Long id, String ownerEmail, ProjectRequest request) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwner().getEmail().equals(ownerEmail)) {
            throw new RuntimeException("Only the owner can update this project");
        }

        project.setName(request.getName());
        project.setDescription(request.getDescription());

        if (request.getStatus() != null) {
            project.setStatus(ProjectStatus.valueOf(request.getStatus().toUpperCase()));
        }

        projectRepository.save(project);
        auditLogService.logAction(project.getOwner().getId(), "PROJECT_UPDATE", "PROJECT", project.getId(),
                "Project details or status updated");
        return mapToProjectResponse(project);
    }
    @Override
    public void deleteProject(Long id, String ownerEmail) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwner().getEmail().equals(ownerEmail)) {
            throw new RuntimeException("Only the owner can delete this project");
        }

        project.setDeletedAt(LocalDateTime.now());
        projectRepository.save(project);
        auditLogService.logAction(project.getOwner().getId(), "PROJECT_DELETE", "PROJECT", project.getId(),
                "Project soft-deleted");
    }

    @Override
    public ProjectResponse addMember(Long projectId, Long userId, String ownerEmail) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwner().getEmail().equals(ownerEmail)) {
            throw new RuntimeException("Only the owner can add members");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        project.getMembers().add(user);
        projectRepository.save(project);
        auditLogService.logAction(project.getOwner().getId(), "PROJECT_MEMBER_ADD", "PROJECT", project.getId(),
                String.format("Added user [ID: %d, Username: %s] to the project", user.getId(), user.getUsername()));
        return mapToProjectResponse(project);
    }

    @Override
    public ProjectResponse removeMember(Long projectId, Long userId, String ownerEmail) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwner().getEmail().equals(ownerEmail)) {
            throw new RuntimeException("Only the owner can remove members");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        project.getMembers().remove(user);
        projectRepository.save(project);
        auditLogService.logAction(project.getOwner().getId(), "PROJECT_MEMBER_REMOVE", "PROJECT", project.getId(),
                String.format("Removed user [ID: %d, Username: %s] from the project", user.getId(), user.getUsername()));
        return mapToProjectResponse(project);
    }

    private ProjectResponse mapToProjectResponse(Project project) {
        return ProjectResponse.builder()
            .id(project.getId())
            .name(project.getName())
            .description(project.getDescription())
            .status(project.getStatus().name())
            .ownerUsername(project.getOwner().getUsername())
            .memberUsernames(
                project.getMembers().stream()
                    .map(User::getUsername)
                    .collect(Collectors.toSet())
            )
            .createdAt(project.getCreatedAt())
            .updatedAt(project.getUpdatedAt())
            .build();
    }
}

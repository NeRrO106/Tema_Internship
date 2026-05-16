package com.internship.documentmanagement.controller;

import com.internship.documentmanagement.dto.request.ProjectRequest;
import com.internship.documentmanagement.dto.response.ProjectResponse;
import com.internship.documentmanagement.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjectResponse> createProject(
            Authentication authentication,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(projectService.createProject(authentication.getName(), request));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProjectResponse>> getMyProjects(Authentication authentication) {
        return ResponseEntity.ok(projectService.getMyProjects(authentication.getName()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(
                projectService.updateProject(id, authentication.getName(), request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long id,
            Authentication authentication) {
        projectService.deleteProject(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/members/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjectResponse> addMember(
            @PathVariable Long id,
            @PathVariable Long userId,
            Authentication authentication) {
        return ResponseEntity.ok(
                projectService.addMember(id, userId, authentication.getName()));
    }

    @DeleteMapping("/{id}/members/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjectResponse> removeMember(
            @PathVariable Long id,
            @PathVariable Long userId,
            Authentication authentication) {
        return ResponseEntity.ok(
                projectService.removeMember(id, userId, authentication.getName()));
    }
}

package com.internship.documentmanagement.controller;

import com.internship.documentmanagement.dto.request.TaskRequest;
import com.internship.documentmanagement.dto.response.TaskResponse;
import com.internship.documentmanagement.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskResponse> createTask(@PathVariable Long projectId, Authentication authentication, @Valid @RequestBody TaskRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(projectId, authentication.getName(), request));
    }

    @GetMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long projectId, @PathVariable Long taskId){
        return ResponseEntity.ok(taskService.getTask(taskId, projectId));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TaskResponse>> getProjectTasks(@PathVariable Long projectId, @RequestParam(required = false) String status, @RequestParam(required = false) String priority){
        if(status != null && priority != null){
            return ResponseEntity.ok(taskService.getProjectTasksByStatusAndPriority(projectId, status, priority));
        }
        else if(status != null){
            return ResponseEntity.ok(taskService.getProjectTasksByStatus(projectId, status));
        }
        else if(priority != null){
            return ResponseEntity.ok(taskService.getProjectTasksByPriority(projectId, priority));
        }
        else{
            return ResponseEntity.ok(taskService.getProjectTasks(projectId));
        }
    }
    @PutMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long projectId, @PathVariable Long taskId, Authentication authentication, @Valid @RequestBody TaskRequest request){
        return ResponseEntity.ok(taskService.updateTask(taskId, projectId, authentication.getName(), request));
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteTask(@PathVariable Long projectId, @PathVariable Long taskId, Authentication authentication){
        taskService.deleteTask(taskId, projectId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{taskId}/assign/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> assignTask(@PathVariable Long projectId, @PathVariable Long taskId, @PathVariable Long userId, Authentication authentication){
        taskService.assignTask(taskId, projectId, userId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{taskId}/unassign")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unassignTask(@PathVariable Long projectId, @PathVariable Long taskId, Authentication authentication){
        taskService.unassignTask(taskId, projectId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

}

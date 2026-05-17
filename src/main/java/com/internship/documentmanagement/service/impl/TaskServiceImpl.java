package com.internship.documentmanagement.service.impl;

import com.internship.documentmanagement.dto.request.TaskRequest;
import com.internship.documentmanagement.dto.response.TaskResponse;
import com.internship.documentmanagement.model.*;
import com.internship.documentmanagement.repository.TaskRepository;
import com.internship.documentmanagement.repository.ProjectRepository;
import com.internship.documentmanagement.repository.UserRepository;
import com.internship.documentmanagement.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    public TaskResponse createTask(Long projectId, String creatorEmail, TaskRequest request) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User assignedTo = null;
        if (request.getAssignedToId() != null) {
            assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("Assigned user not found"));

            Project projectWithMembers = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            if (!projectWithMembers.getMembers().contains(assignedTo)) {
                throw new RuntimeException("Assigned user is not a member of this project");
            }
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority() != null ? TaskPriority.valueOf(request.getPriority().toUpperCase()) : TaskPriority.MEDIUM)
                .status(request.getStatus() != null ? TaskStatus.valueOf(request.getStatus().toUpperCase()) : TaskStatus.TODO)
                .deadline(request.getDeadline())
                .project(project)
                .assignedTo(assignedTo)
                .createdBy(creator)
                .build();

        taskRepository.save(task);
        return mapToTaskResponse(task);
    }

    @Override
    public TaskResponse getTask(Long taskId, Long projectId) {
        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new RuntimeException("Task not found in this project"));
        return mapToTaskResponse(task);
    }

    @Override
    public List<TaskResponse> getProjectTasks(Long projectId) {
        projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(this::mapToTaskResponse)
                .toList();
    }

    @Override
    public List<TaskResponse> getProjectTasksByStatus(Long projectId, String status) {
        projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
        return taskRepository.findByProjectIdAndStatus(projectId, taskStatus)
                .stream()
                .map(this::mapToTaskResponse)
                .toList();
    }

    @Override
    public List<TaskResponse> getProjectTasksByPriority(Long projectId, String priority) {
        projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        TaskPriority taskPriority = TaskPriority.valueOf(priority.toUpperCase());
        return taskRepository.findByProjectIdAndPriority(projectId, taskPriority)
                .stream()
                .map(this::mapToTaskResponse)
                .toList();
    }

    @Override
    public List<TaskResponse> getProjectTasksByStatusAndPriority(Long projectId, String status, String priority) {
        projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
        TaskPriority taskPriority = TaskPriority.valueOf(priority.toUpperCase());

        return taskRepository.findByProjectIdStatusAndPriority(projectId, taskStatus, taskPriority)
                .stream()
                .map(this::mapToTaskResponse)
                .toList();
    }

    @Override
    public TaskResponse updateTask(Long taskId, Long projectId, String updaterEmail, TaskRequest request) {
        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new RuntimeException("Task not found in this project"));

        userRepository.findByEmail(updaterEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        if (request.getPriority() != null) {
            task.setPriority(TaskPriority.valueOf(request.getPriority().toUpperCase()));
        }
        if (request.getStatus() != null) {
            task.setStatus(TaskStatus.valueOf(request.getStatus().toUpperCase()));
        }
        if (request.getDeadline() != null) {
            task.setDeadline(request.getDeadline());
        }

        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("Assigned user not found"));

            Project projectWithMembers = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            if (!projectWithMembers.getMembers().contains(assignedTo)) {
                throw new RuntimeException("Assigned user is not a member of this project");
            }
            task.setAssignedTo(assignedTo);
        }

        taskRepository.save(task);
        return mapToTaskResponse(task);
    }

    @Override
    public void deleteTask(Long taskId, Long projectId, String deleterEmail) {
        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new RuntimeException("Task not found in this project"));

        userRepository.findByEmail(deleterEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        taskRepository.delete(task);
    }

    @Override
    public void assignTask(Long taskId, Long projectId, Long userId, String assignerEmail) {
        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new RuntimeException("Task not found in this project"));

        User assignedUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Re-fetch project with eager-loaded members to avoid LazyInitializationException
        Project projectWithMembers = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!projectWithMembers.getMembers().contains(assignedUser)) {
            throw new RuntimeException("User is not a member of this project");
        }

        task.setAssignedTo(assignedUser);
        taskRepository.save(task);
    }

    @Override
    public void unassignTask(Long taskId, Long projectId, String assignerEmail) {
        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new RuntimeException("Task not found in this project"));

        task.setAssignedTo(null);
        taskRepository.save(task);
    }

    private TaskResponse mapToTaskResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority().name())
                .status(task.getStatus().name())
                .deadline(task.getDeadline())
                .projectId(task.getProject().getId())
                .projectName(task.getProject().getName())
                .assignedToUsername(task.getAssignedTo() != null ? task.getAssignedTo().getUsername() : null)
                .createdByUsername(task.getCreatedBy().getUsername())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
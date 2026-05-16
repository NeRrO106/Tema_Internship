package com.internship.documentmanagement.service;

import com.internship.documentmanagement.dto.request.TaskRequest;
import com.internship.documentmanagement.dto.response.TaskResponse;
import java.util.List;

public interface TaskService {
    TaskResponse createTask (Long projectId, String creatorEmail, TaskRequest request);
    TaskResponse getTask(Long taskId, Long projectId);

    List<TaskResponse> getProjectTasks(Long projectId);
    List<TaskResponse> getProjectTasksByStatus(Long projectId, String status);
    List<TaskResponse> getProjectTasksByPriority(Long projectId, String priority);
    List<TaskResponse> getProjectTasksByStatusAndPriority(Long projectId, String status, String priority);
    TaskResponse updateTask(Long taskId, Long projectId, String updaterEmail, TaskRequest request);

    void deleteTask(Long taskId, Long projectId, String deleterEmail);
    void assignTask(Long taskId, Long projectId, Long userId, String assignerEmail);
    void unassignTask(Long taskId, Long projectId, String assignerEmail);
}

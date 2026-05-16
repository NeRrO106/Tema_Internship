package com.internship.documentmanagement.repository;

import com.internship.documentmanagement.model.Project;
import com.internship.documentmanagement.model.Task;
import com.internship.documentmanagement.model.TaskPriority;
import com.internship.documentmanagement.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);

    List<Task> findByProjectIdAndPriority(Long projectId, TaskPriority priority);

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.status = :status AND t.priority = :priority")
    List<Task> findByProjectIdStatusAndPriority(
            @Param("projectId") Long projectId,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority
    );

    List<Task> findByAssignedToId(Long userId);

    Optional<Task> findByIdAndProjectId(Long taskId, Long projectId);

    Long project(Project project);
}

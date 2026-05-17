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

    @Query("SELECT t FROM Task t JOIN FETCH t.project LEFT JOIN FETCH t.assignedTo JOIN FETCH t.createdBy WHERE t.project.id = :projectId")
    List<Task> findByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT t FROM Task t JOIN FETCH t.project LEFT JOIN FETCH t.assignedTo JOIN FETCH t.createdBy WHERE t.project.id = :projectId AND t.status = :status")
    List<Task> findByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") TaskStatus status);

    @Query("SELECT t FROM Task t JOIN FETCH t.project LEFT JOIN FETCH t.assignedTo JOIN FETCH t.createdBy WHERE t.project.id = :projectId AND t.priority = :priority")
    List<Task> findByProjectIdAndPriority(@Param("projectId") Long projectId, @Param("priority") TaskPriority priority);

    @Query("SELECT t FROM Task t " +
            "JOIN FETCH t.project " +
            "LEFT JOIN FETCH t.assignedTo " +
            "JOIN FETCH t.createdBy " +
            "WHERE t.project.id = :projectId " +
            "AND t.status = :status " +
            "AND t.priority = :priority")
    List<Task> findByProjectIdStatusAndPriority(
            @Param("projectId") Long projectId,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority
    );

    @Query("SELECT t FROM Task t JOIN FETCH t.project LEFT JOIN FETCH t.assignedTo JOIN FETCH t.createdBy WHERE t.assignedTo.id = :userId")
    List<Task> findByAssignedToId(@Param("userId") Long userId);

    @Query("SELECT t FROM Task t JOIN FETCH t.project LEFT JOIN FETCH t.assignedTo JOIN FETCH t.createdBy WHERE t.id = :taskId AND t.project.id = :projectId")
    Optional<Task> findByIdAndProjectId(@Param("taskId") Long taskId, @Param("projectId") Long projectId);
}

package com.internship.documentmanagement.repository;


import com.internship.documentmanagement.model.AuditLog;
import com.internship.documentmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("SELECT a FROM AuditLog a LEFT JOIN FETCH a.user WHERE a.user.id = :userId ORDER BY a.createdAt DESC")
    List<AuditLog> findByUserId(@Param("userId") Long userId);

    @Query("SELECT a FROM AuditLog a LEFT JOIN FETCH a.user WHERE a.action = :action ORDER BY a.createdAt DESC")
    List<AuditLog> findByAction(@Param("action") String action);

    @Query("SELECT a FROM AuditLog a LEFT JOIN FETCH a.user WHERE a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    List<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM AuditLog a LEFT JOIN FETCH a.user ORDER BY a.createdAt DESC")
    List<AuditLog> findAllWithUsers();

}

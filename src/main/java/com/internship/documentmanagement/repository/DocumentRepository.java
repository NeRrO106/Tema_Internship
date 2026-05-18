package com.internship.documentmanagement.repository;

import com.internship.documentmanagement.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query("SELECT d FROM Document d JOIN FETCH d.project JOIN FETCH d.uploadedBy WHERE d.project.id = :projectId")
    List<Document> findByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT d FROM Document d JOIN FETCH d.project JOIN FETCH d.uploadedBy WHERE d.id = :id AND d.project.id = :projectId")
    Optional<Document> findByIdAndProjectId(@Param("id") Long id, @Param("projectId") Long projectId);

    @Query("SELECT d FROM Document d WHERE d.storagePath = :storagePath")
    Optional<Document> findByStoragePath(@Param("storagePath") String storagePath);

}

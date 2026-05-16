package com.internship.documentmanagement.repository;

import com.internship.documentmanagement.model.Project;
import com.internship.documentmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.members WHERE p.deletedAt IS NULL")
    List<Project> findAllByDeletedAtIsNull();

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.members WHERE p.owner = :owner AND p.deletedAt IS NULL")
    List<Project> findAllByOwnerAndDeletedAtIsNull(@Param("owner") User owner);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.members WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Project> findByIdAndDeletedAtIsNull(@Param("id") Long id);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.members m WHERE m = :user AND p.deletedAt IS NULL")
    List<Project> findAllByMemberAndDeletedAtIsNull(@Param("user") User user);
}

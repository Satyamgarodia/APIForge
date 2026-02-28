package com.satyam.apiforge.workspace;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
    List<Workspace> findByOwnerId(UUID ownerId);
    Optional<Workspace> findBySlug(String slug);
    boolean existsBySlug(String slug);
    @Query("SELECT w FROM Workspace w JOIN FETCH w.owner WHERE w.id = :id")
    Optional<Workspace> findByIdWithOwner(@jakarta.annotation.Nullable UUID id);

}
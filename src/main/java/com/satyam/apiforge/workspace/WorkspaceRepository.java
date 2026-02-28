package com.satyam.apiforge.workspace;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
    List<Workspace> findByOwnerId(UUID ownerId);
    Optional<Workspace> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
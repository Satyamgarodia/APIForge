package com.satyam.apiforge.endpoint;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface MockEndpointRepository extends JpaRepository<MockEndpoint, UUID> {
    List<MockEndpoint> findByWorkspaceId(UUID workspaceId);
    Optional<MockEndpoint> findByWorkspaceSlugAndMethodAndPath(
            String slug, String method, String path);
}
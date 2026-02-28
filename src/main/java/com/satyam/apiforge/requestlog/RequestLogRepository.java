package com.satyam.apiforge.requestlog;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface RequestLogRepository extends JpaRepository<RequestLog, UUID> {
    List<RequestLog> findByWorkspaceSlugOrderByCalledAtDesc(String slug);
    List<RequestLog> findByEndpointIdOrderByCalledAtDesc(UUID endpointId);
}
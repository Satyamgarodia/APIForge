package com.satyam.apiforge.requestlog;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "request_logs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID endpointId;

    private String workspaceSlug;
    private String method;
    private String path;

    @Column(columnDefinition = "TEXT")
    private String requestHeaders;

    @Column(columnDefinition = "TEXT")
    private String requestBody;

    private int responseStatus;
    private long responseTimeMs;

    @Column(updatable = false)
    private LocalDateTime calledAt = LocalDateTime.now();
}
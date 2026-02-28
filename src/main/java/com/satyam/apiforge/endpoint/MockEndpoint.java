package com.satyam.apiforge.endpoint;

import com.satyam.apiforge.workspace.Workspace;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mock_endpoints")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class MockEndpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @Column(nullable = false)
    private String method; // GET, POST, PUT, DELETE

    @Column(nullable = false)
    private String path; // e.g. /users or /users/{id}

    @Column(columnDefinition = "TEXT", nullable = false)
    private String responseBody; // JSON response

    @Column(nullable = false)
    private int statusCode = 200;

    @Column(nullable = false)
    private int delayMs = 0; // artificial delay in ms

    @Column(nullable = false)
    private boolean active = true;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
package com.satyam.apiforge.endpoint;

import com.satyam.apiforge.common.ApiResponse;
import com.satyam.apiforge.security.User;
import com.satyam.apiforge.workspace.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/endpoints")
@RequiredArgsConstructor
public class MockEndpointController {

    private final MockEndpointRepository endpointRepository;
    private final WorkspaceRepository workspaceRepository;

    // Create mock endpoint
    @PostMapping
    public ResponseEntity<?> create(@PathVariable UUID workspaceId,
                                    @RequestBody CreateEndpointRequest request,
                                    @AuthenticationPrincipal User user) {

        return workspaceRepository.findByIdWithOwner(workspaceId)
                .filter(w -> w.getOwner().getId().equals(user.getId()))
                .map(workspace -> {
                    MockEndpoint endpoint = MockEndpoint.builder()
                            .workspace(workspace)
                            .method(request.method().toUpperCase())
                            .path(request.path())
                            .responseBody(request.responseBody())
                            .statusCode(request.statusCode())
                            .delayMs(request.delayMs())
                            .build();
                    return ResponseEntity.ok(ApiResponse.ok(endpointRepository.save(endpoint)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all endpoints in workspace
    @GetMapping
    public ResponseEntity<List<MockEndpoint>> getAll(@PathVariable UUID workspaceId,
                                                     @AuthenticationPrincipal User user) {
        return workspaceRepository.findById(workspaceId)
                .filter(w -> w.getOwner().getId().equals(user.getId()))
                .map(w -> ResponseEntity.ok((endpointRepository.findByWorkspaceId(workspaceId))))
                .orElse(ResponseEntity.notFound().build());
    }

    // Update endpoint
    @PutMapping("/{endpointId}")
    public ResponseEntity<?> update(@PathVariable UUID workspaceId,
                                    @PathVariable UUID endpointId,
                                    @RequestBody CreateEndpointRequest request,
                                    @AuthenticationPrincipal User user) {

        return workspaceRepository.findById(workspaceId)
                .filter(w -> w.getOwner().getId().equals(user.getId()))
                .flatMap(w -> endpointRepository.findById(endpointId))
                .map(endpoint -> {
                    endpoint.setMethod(request.method().toUpperCase());
                    endpoint.setPath(request.path());
                    endpoint.setResponseBody(request.responseBody());
                    endpoint.setStatusCode(request.statusCode());
                    endpoint.setDelayMs(request.delayMs());
                    return ResponseEntity.ok(ApiResponse.ok(endpointRepository.save(endpoint)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Toogle Active/Inactive State of Endpoint
    @PatchMapping("/{endpointId}/toggle")
    public ResponseEntity<?> toggle(@PathVariable UUID workspaceId,
                                    @PathVariable UUID endpointId,
                                    @AuthenticationPrincipal User user) {

        return workspaceRepository.findById(workspaceId)
                .filter(w -> w.getOwner().getId().equals(user.getId()))
                .flatMap(w -> endpointRepository.findById(endpointId))
                .map(endpoint -> {
                    endpoint.setActive(!endpoint.isActive());
                    endpointRepository.save(endpoint);
                    String status = endpoint.isActive() ? "activated" : "deactivated";
                    return ResponseEntity.ok(
                            ApiResponse.ok("Endpoint " + status, endpoint));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete endpoint
    @DeleteMapping("/{endpointId}")
    public ResponseEntity<?> delete(@PathVariable UUID workspaceId,
                                    @PathVariable UUID endpointId,
                                    @AuthenticationPrincipal User user) {

        return workspaceRepository.findById(workspaceId)
                .filter(w -> w.getOwner().getId().equals(user.getId()))
                .flatMap(w -> endpointRepository.findById(endpointId))
                .map(endpoint -> {
                    endpointRepository.delete(endpoint);
                    return ResponseEntity.ok(ApiResponse.ok("Deleted endpoint", endpoint));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    record CreateEndpointRequest(
            String method,
            String path,
            String responseBody,
            int statusCode,
            int delayMs
    ) {}
}